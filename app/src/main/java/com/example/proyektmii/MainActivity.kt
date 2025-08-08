package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyektmii.ui.screens.CanteenMenuScreen
import com.example.proyektmii.ui.screens.CartScreen
import com.example.proyektmii.ui.screens.DestinationMenuScreen
import com.example.proyektmii.ui.screens.DestinationSelectionScreen
import com.example.proyektmii.ui.screens.HomeScreen
import com.example.proyektmii.ui.screens.NfcPaymentScreen
import com.example.proyektmii.ui.screens.OnboardingScreen
import com.example.proyektmii.ui.screens.ParkingScreen
import com.example.proyektmii.ui.screens.PaymentSuccessScreen
import com.example.proyektmii.ui.screens.TicketPaymentScreen
import com.example.proyektmii.ui.screens.ThankYouScreen
import com.example.proyektmii.ui.screens.CartItem
import com.example.proyektmii.ui.screens.TicketItem
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var showParkingScreen by mutableStateOf(false)
    private var showThankYouScreen by mutableStateOf(false)
    private var parkingCost by mutableStateOf(0)
    private var nfcTapped by mutableStateOf(false)
    private var showCanteenMenuScreen by mutableStateOf(false)
    private var showCartScreen by mutableStateOf(false)
    private var showPaymentScreen by mutableStateOf(false)
    private var showPaymentSuccessScreen by mutableStateOf(false)
    private var cartItems by mutableStateOf<List<CartItem>>(emptyList())
    private var totalPrice by mutableStateOf(0)
    private var showDestinationMenuScreen by mutableStateOf(false)
    private var showDestinationSelectionScreen by mutableStateOf(false)
    private var showTicketPaymentScreen by mutableStateOf(false)
    private var selectedTicket by mutableStateOf<TicketItem?>(null)
    private var isWahanaSelected by mutableStateOf(false) // State untuk melacak pilihan Wahana atau Museum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Perangkat tidak mendukung NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "Silakan aktifkan NFC", Toast.LENGTH_SHORT).show()
        }

        setContent {
            ProyekTMIITheme {
                var showOnboarding by remember { mutableStateOf(true) }

                if (showPaymentSuccessScreen) {
                    PaymentSuccessScreen(
                        totalPrice = totalPrice,
                        onBackToHome = {
                            showPaymentSuccessScreen = false
                            cartItems = emptyList()
                            selectedTicket = null
                            totalPrice = 0
                            showDestinationMenuScreen = false // Kembali ke HomeScreen
                        },
                        successMessage = if (selectedTicket != null) "Pembayaran Tiket Berhasil" else "Pembayaran Berhasil"
                    )
                } else if (showPaymentScreen) {
                    NfcPaymentScreen(
                        totalPrice = totalPrice,
                        nfcTapped = nfcTapped,
                        onNfcProcessed = { nfcTapped = false },
                        onPaymentSuccess = { price ->
                            totalPrice = price
                            showPaymentScreen = false
                            showPaymentSuccessScreen = true
                        }
                    )
                } else if (showTicketPaymentScreen) {
                    selectedTicket?.let { ticket ->
                        TicketPaymentScreen(
                            ticketItem = ticket,
                            onProceedToNfc = {
                                totalPrice = ticket.destination.price * ticket.quantity
                                showTicketPaymentScreen = false
                                showPaymentScreen = true
                            }
                        )
                    }
                } else if (showCartScreen) {
                    CartScreen(
                        cartItems = cartItems,
                        onProceedToPayment = { price ->
                            totalPrice = price
                            showCartScreen = false
                            showPaymentScreen = true
                        },
                        modifier = Modifier
                    )
                } else if (showCanteenMenuScreen) {
                    CanteenMenuScreen(
                        onProceedToCart = { items ->
                            cartItems = items
                            showCanteenMenuScreen = false
                            showCartScreen = true
                        }
                    )
                } else if (showDestinationSelectionScreen) {
                    DestinationSelectionScreen(
                        isWahana = isWahanaSelected,
                        onProceedToPayment = { ticket ->
                            selectedTicket = ticket
                            showDestinationSelectionScreen = false
                            showTicketPaymentScreen = true
                        }
                    )
                } else if (showDestinationMenuScreen) {
                    DestinationMenuScreen(
                        onNavigateToWahana = {
                            isWahanaSelected = true
                            showDestinationSelectionScreen = true
                        },
                        onNavigateToMuseum = {
                            isWahanaSelected = false
                            showDestinationSelectionScreen = true
                        }
                    )
                } else if (showParkingScreen) {
                    ParkingScreen(
                        nfcTapped = nfcTapped,
                        onNfcProcessed = { nfcTapped = false },
                        onCardProcessed = { cost ->
                            parkingCost = cost
                            showParkingScreen = false
                            showThankYouScreen = true
                        }
                    )
                } else if (showOnboarding) {
                    OnboardingScreen {
                        showOnboarding = false
                    }
                } else {
                    HomeScreen(
                        onNavigateToParking = { showParkingScreen = true },
                        onNavigateToCanteen = { showCanteenMenuScreen = true },
                        onNavigateToDestination = { showDestinationMenuScreen = true }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action && (showParkingScreen || showPaymentScreen)) {
                Toast.makeText(this, "Kartu terdeteksi, memproses...", Toast.LENGTH_SHORT).show()
                nfcTapped = true
            }
        }
    }
}