package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyektmii.ui.screens.*
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // State Pintu Masuk
    private var isNfcTapped by mutableStateOf(false)
    private var showPintuMasukScreen by mutableStateOf(false)

    // State Kantin
    private var showCanteenMenuScreen by mutableStateOf(false)
    private var showCartScreen by mutableStateOf(false)
    private var showPaymentScreen by mutableStateOf(false)
    private var showPaymentSuccessScreen by mutableStateOf(false)
    private var cartItems by mutableStateOf(listOf<CartItem>())
    private var totalPrice by mutableStateOf(0)
    private var nfcTappedForPayment by mutableStateOf(false)

    // State Parkir
    private var showParkingScreen by mutableStateOf(false)
    private var showThankYouScreen by mutableStateOf(false)
    private var parkingCost by mutableStateOf(0)
    private var parkingEntryTime by mutableStateOf<Long?>(null)
    private var isCheckedIn by mutableStateOf(false)

    // State Destinasi/Tiket
    private var showDestinationMenuScreen by mutableStateOf(false)
    private var showDestinationSelectionScreen by mutableStateOf(false)
    private var showTicketCartScreen by mutableStateOf(false) // New state
    private var selectedTicket by mutableStateOf<TicketItem?>(null)
    private var isWahanaSelected by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        setContent {
            ProyekTMIITheme {
                var showOnboarding by remember { mutableStateOf(true) }
                var showPintuMasukSuksesScreen by remember { mutableStateOf(false) }

                // Logika NFC untuk Pintu Masuk
                if (isNfcTapped) {
                    showPintuMasukScreen = false
                    showPintuMasukSuksesScreen = true
                    isNfcTapped = false
                }

                when {
                    // ✅ Pintu Masuk Sukses
                    showPintuMasukSuksesScreen -> {
                        PintuMasukSuksesScreen(
                            onBackToHome = { showPintuMasukSuksesScreen = false }
                        )
                    }
                    // ✅ Pintu Masuk
                    showPintuMasukScreen -> {
                        PintuMasukScreen(
                            onBack = { showPintuMasukScreen = false }
                        )
                    }
                    // ✅ Pembayaran sukses (Kantin, Parkir, atau Tiket)
                    showPaymentSuccessScreen -> {
                        PaymentSuccessScreen(
                            totalPrice = totalPrice,
                            onBackToHome = {
                                showPaymentSuccessScreen = false
                                cartItems = emptyList()
                                selectedTicket = null
                                totalPrice = 0
                                showDestinationMenuScreen = false
                                parkingEntryTime = null
                                isCheckedIn = false
                            },
                            successMessage = when {
                                selectedTicket != null -> "Pembayaran Tiket Berhasil"
                                parkingCost > 0 -> "Pembayaran Parkir Berhasil"
                                else -> "Pembayaran Berhasil"
                            }
                        )
                    }
                    // ✅ Layar Pembayaran (NFC untuk semua)
                    showPaymentScreen -> {
                        PaymentScreen(
                            totalPrice = totalPrice,
                            nfcTapped = nfcTappedForPayment,
                            onNfcProcessed = { nfcTappedForPayment = false },
                            onPaymentSuccess = { price ->
                                totalPrice = price
                                showPaymentScreen = false
                                showPaymentSuccessScreen = true
                            }
                        )
                    }
                    // ✅ Pembayaran Tiket
                    showTicketCartScreen -> {
                        TicketCartScreen(
                            ticketItem = selectedTicket,
                            onProceedToPayment = { price ->
                                totalPrice = price
                                showTicketCartScreen = false
                                showPaymentScreen = true
                            },
                            onBack = {
                                showTicketCartScreen = false
                                showDestinationSelectionScreen = true
                            },
                            onUpdateQuantity = { newQuantity ->
                                selectedTicket = selectedTicket?.copy(quantity = newQuantity)
                            }
                        )
                    }
                    // ✅ Keranjang Kantin
                    showCartScreen -> {
                        CartScreen(
                            cartItems = cartItems,
                            onProceedToPayment = { price ->
                                totalPrice = price
                                showCartScreen = false
                                showPaymentScreen = true
                            },
                            onBack = {
                                showCartScreen = false
                                showCanteenMenuScreen = true
                            },
                            onUpdateQuantity = { menuItem, newQuantity ->
                                cartItems = if (newQuantity > 0) {
                                    cartItems.map { cartItem ->
                                        if (cartItem.menuItem == menuItem) {
                                            cartItem.copy(quantity = newQuantity)
                                        } else {
                                            cartItem
                                        }
                                    }
                                } else {
                                    cartItems.filter { it.menuItem != menuItem }
                                }
                            },
                            modifier = Modifier
                        )
                    }
                    // ✅ Menu Kantin
                    showCanteenMenuScreen -> {
                        CanteenMenuScreen(
                            onProceedToCart = { items ->
                                cartItems = items
                                showCanteenMenuScreen = false
                                showCartScreen = true
                            },
                            onBack = { showCanteenMenuScreen = false }
                        )
                    }
                    // ✅ Pilih Destinasi
                    showDestinationSelectionScreen -> {
                        DestinationSelectionScreen(
                            isWahana = isWahanaSelected,
                            onProceedToCart = { ticket ->
                                selectedTicket = ticket
                                showDestinationSelectionScreen = false
                                showTicketCartScreen = true // Go to cart instead of direct payment
                            },
                            onBack = {
                                showDestinationSelectionScreen = false
                                showDestinationMenuScreen = true
                            }
                        )
                    }
                    // ✅ Menu Destinasi
                    showDestinationMenuScreen -> {
                        DestinationMenuScreen(
                            onNavigateToWahana = {
                                isWahanaSelected = true
                                showDestinationMenuScreen = false
                                showDestinationSelectionScreen = true
                            },
                            onNavigateToMuseum = {
                                isWahanaSelected = false
                                showDestinationMenuScreen = false
                                showDestinationSelectionScreen = true
                            },
                            onBack = {
                                showDestinationMenuScreen = false
                            }
                        )
                    }

                    // ✅ Parkir
                    showParkingScreen -> {
                        ParkingScreen(
                            nfcTapped = nfcTappedForPayment,
                            parkingEntryTime = parkingEntryTime,
                            isCheckedIn = isCheckedIn,
                            onNfcProcessed = { nfcTappedForPayment = false },
                            onCheckIn = { time ->
                                parkingEntryTime = time
                                isCheckedIn = true
                            },
                            onCheckOut = { cost ->
                                parkingCost = cost
                                totalPrice = cost
                                showParkingScreen = false
                                showPaymentScreen = true
                            }
                        )
                    }
                    // ✅ Layar Terima Kasih Parkir
                    showThankYouScreen -> {
                        ThankYouScreen(
                            parkingCost = parkingCost,
                            onBackToHome = { showThankYouScreen = false }
                        )
                    }
                    // ✅ Onboarding
                    showOnboarding -> {
                        OnboardingScreen { showOnboarding = false }
                    }
                    // ✅ Home
                    else -> {
                        HomeScreen(
                            onCardClick = { featureType ->
                                when (featureType) {
                                    "Pintu Masuk" -> showPintuMasukScreen = true
                                    "Kantin" -> showCanteenMenuScreen = true
                                    "Parkir" -> showParkingScreen = true
                                    "Destinasi" -> showDestinationMenuScreen = true
                                }
                            }
                        )
                    }
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
            // NFC untuk Pintu Masuk
            if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action && showPintuMasukScreen) {
                Toast.makeText(this, "Kartu terdeteksi, memproses...", Toast.LENGTH_SHORT).show()
                isNfcTapped = true
            }
            // NFC untuk Pembayaran Kantin, Parkir, dan Tiket
            else if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action &&
                (showPaymentScreen || showParkingScreen)) {
                Toast.makeText(this, "Kartu terdeteksi, memproses pembayaran...", Toast.LENGTH_SHORT).show()
                nfcTappedForPayment = true
            }
        }
    }
}
