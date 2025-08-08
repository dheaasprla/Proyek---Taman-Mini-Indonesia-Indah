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

    // State untuk Pintu Masuk
    private var isNfcTapped by mutableStateOf(false)
    private var showPintuMasukScreen by mutableStateOf(false)

    // State untuk Kantin
    private var showCanteenMenuScreen by mutableStateOf(false)
    private var showCartScreen by mutableStateOf(false)
    private var showPaymentScreen by mutableStateOf(false)
    private var showPaymentSuccessScreen by mutableStateOf(false)
    private var cartItems by mutableStateOf(listOf<CartItem>())
    private var totalPrice by mutableStateOf(0)
    private var nfcTappedForPayment by mutableStateOf(false)

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
                    // Pintu Masuk Sukses
                    showPintuMasukSuksesScreen -> {
                        PintuMasukSuksesScreen(
                            onBackToHome = { showPintuMasukSuksesScreen = false }
                        )
                    }
                    // Pintu Masuk
                    showPintuMasukScreen -> {
                        PintuMasukScreen(
                            onBack = { showPintuMasukScreen = false }
                        )
                    }
                    // Pembayaran Kantin sukses
                    showPaymentSuccessScreen -> {
                        PaymentSuccessScreen(
                            totalPrice = totalPrice,
                            onBackToHome = {
                                showPaymentSuccessScreen = false
                                cartItems = emptyList()
                                totalPrice = 0
                            }
                        )
                    }
                    // Layar Pembayaran Kantin
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
                    // Keranjang Kantin
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
                    // Menu Kantin
                    showCanteenMenuScreen -> {
                        CanteenMenuScreen(
                            onProceedToCart = { items ->
                                cartItems = items
                                showCanteenMenuScreen = false
                                showCartScreen = true
                            },
                            onBack = {
                                showCanteenMenuScreen = false
                            }
                        )
                    }
                    // Onboarding
                    showOnboarding -> {
                        OnboardingScreen { showOnboarding = false }
                    }
                    // Home
                    else -> {
                        HomeScreen(
                            onCardClick = { featureType ->
                                when (featureType) {
                                    "Pintu Masuk" -> showPintuMasukScreen = true
                                    "Kantin" -> showCanteenMenuScreen = true
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
            // NFC untuk Pembayaran Kantin
            else if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action && showPaymentScreen) {
                Toast.makeText(this, "Kartu terdeteksi, memproses pembayaran...", Toast.LENGTH_SHORT).show()
                nfcTappedForPayment = true
            }
        }
    }
}