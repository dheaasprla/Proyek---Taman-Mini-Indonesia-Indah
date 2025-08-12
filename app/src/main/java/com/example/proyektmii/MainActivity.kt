package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.TicketItem
import com.example.proyektmii.data.local.AppPreferences
import com.example.proyektmii.ui.screens.*
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var appPreferences: AppPreferences

    private var currentScreen by mutableStateOf("onboarding")
    private var isNfcTapped by mutableStateOf(false)
    private var totalPrice by mutableStateOf(0)

    private var cartItems by mutableStateOf(listOf<CartItem>())
    private var selectedTicket by mutableStateOf<TicketItem?>(null)
    private var isWahanaSelected by mutableStateOf(false)
    private var parkingEntryTime by mutableStateOf<Long?>(null)
    private var userName by mutableStateOf<String?>(null)
    private var cardId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        loadSavedData()

        setContent {
            ProyekTMIITheme {
                when (currentScreen) {
                    "onboarding" -> OnboardingScreen { currentScreen = "home" }
                    "home" -> HomeScreen(onCardClick = { featureType ->
                        when (featureType) {
                            "Pintu Masuk" -> currentScreen = "pintuMasuk"
                            "Kantin" -> {
                                cartItems = appPreferences.getCartItems()
                                currentScreen = "canteenMenu"
                            }
                            "Parkir" -> {
                                parkingEntryTime = appPreferences.getParkingEntryTime().takeIf { it > 0 }
                                currentScreen = "parking"
                            }
                            "Destinasi" -> {
                                selectedTicket = appPreferences.getTicketItem()
                                currentScreen = "destinationMenu"
                            }
                        }
                    })
                    "pintuMasuk" -> PintuMasukScreen(onBack = { currentScreen = "home" })
                    "pintuMasukSuccess" -> PintuMasukSuksesScreen(onBackToHome = { currentScreen = "home" })
                    "parking" -> {
                        val activeParkingEntryTime = appPreferences.getParkingEntryTime()
                        if (activeParkingEntryTime > 0) {
                            ParkingCheckoutScreen(
                                entryTime = activeParkingEntryTime,
                                totalPrice = calculateParkingPrice(activeParkingEntryTime),
                                nfcTapped = isNfcTapped,
                                onNfcProcessed = {
                                    Log.d("TMII_APP", "NFC processed for parking checkout")
                                    isNfcTapped = false
                                },
                                onPaymentSuccess = { paidAmount ->
                                    Log.d("TMII_APP", "Parking payment successful: $paidAmount")
                                    val newHistoryItem = PaymentHistoryItem(
                                        cardId = cardId,
                                        userName = userName,
                                        transactionType = "Parkir",
                                        items = listOf("Parkir"),
                                        totalPrice = paidAmount,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    appPreferences.addPaymentToHistory(newHistoryItem)
                                    appPreferences.clearParkingData()
                                    parkingEntryTime = null
                                    totalPrice = paidAmount
                                    currentScreen = "parkingCheckoutSuccess"
                                },
                                onBack = { currentScreen = "home" }
                            )
                        } else {
                            ParkingCheckinScreen(onBack = { currentScreen = "home" })
                        }
                    }
                    "parkingCheckinSuccess" -> ParkingCheckinSuccessScreen(onBackToHome = { currentScreen = "home" })
                    "parkingCheckoutSuccess" -> ParkingCheckoutSuccessScreen(totalPrice = totalPrice, onBackToHome = { currentScreen = "home" })
                    "canteenMenu" -> CanteenMenuScreen(
                        cartItems = cartItems,
                        onProceedToCart = { currentCartItems ->
                            cartItems = currentCartItems
                            appPreferences.saveCartItems(currentCartItems)
                            currentScreen = "cart"
                        },
                        onBack = { currentScreen = "home" },
                        onUpdateCart = { updatedCart ->
                            cartItems = updatedCart
                            appPreferences.saveCartItems(updatedCart)
                        }
                    )
                    "cart" -> CartScreen(
                        cartItems = cartItems,
                        onProceedToPayment = { price ->
                            totalPrice = price
                            currentScreen = "payment"
                        },
                        onBack = { currentScreen = "canteenMenu" },
                        onUpdateQuantity = { menuItem, newQuantity ->
                            cartItems = if (newQuantity > 0) {
                                cartItems.map { if (it.menuItem == menuItem) it.copy(quantity = newQuantity) else it }
                            } else {
                                cartItems.filter { it.menuItem != menuItem }
                            }
                            appPreferences.saveCartItems(cartItems)
                        }
                    )
                    "destinationMenu" -> DestinationMenuScreen(
                        onNavigateToWahana = {
                            isWahanaSelected = true
                            currentScreen = "destinationSelection"
                        },
                        onNavigateToMuseum = {
                            isWahanaSelected = false
                            currentScreen = "destinationSelection"
                        },
                        onBack = { currentScreen = "home" }
                    )
                    "destinationSelection" -> DestinationSelectionScreen(
                        isWahana = isWahanaSelected,
                        selectedTicket = selectedTicket,
                        onProceedToCart = {
                            currentScreen = "ticketCart"
                        },
                        onBack = { currentScreen = "destinationMenu" },
                        onUpdateTicket = { newTicket ->
                            selectedTicket = newTicket
                            appPreferences.saveTicketItem(newTicket)
                        }
                    )
                    "ticketCart" -> TicketCartScreen(
                        ticketItem = selectedTicket,
                        onProceedToPayment = { price ->
                            totalPrice = price
                            currentScreen = "payment"
                        },
                        onBack = { currentScreen = "destinationSelection" },
                        onUpdateQuantity = { newQuantity ->
                            selectedTicket = selectedTicket?.copy(quantity = newQuantity)
                            appPreferences.saveTicketItem(selectedTicket)
                        }
                    )
                    "payment" -> PaymentScreen(
                        totalPrice = totalPrice,
                        nfcTapped = isNfcTapped,
                        onNfcProcessed = {
                            Log.d("TMII_APP", "NFC processed for general payment")
                            isNfcTapped = false
                        },
                        onPaymentSuccess = { paidAmount ->
                            Log.d("TMII_APP", "General payment successful: $paidAmount")
                            val currentTime = System.currentTimeMillis()
                            val transactionType = if (selectedTicket != null) "Tiket" else "Kantin"
                            val itemsList = if (selectedTicket != null) {
                                listOf("${selectedTicket!!.destination.name} x${selectedTicket!!.quantity}")
                            } else {
                                cartItems.map { "${it.menuItem.name} x${it.quantity}" }
                            }

                            val newHistoryItem = PaymentHistoryItem(
                                cardId = cardId,
                                userName = userName,
                                transactionType = transactionType,
                                items = itemsList,
                                totalPrice = paidAmount,
                                timestamp = currentTime
                            )
                            appPreferences.addPaymentToHistory(newHistoryItem)

                            // Clear relevant data after payment
                            if (selectedTicket != null) {
                                appPreferences.saveTicketItem(null)
                                selectedTicket = null
                            }
                            if (cartItems.isNotEmpty()) {
                                appPreferences.clearCartItems()
                                cartItems = emptyList()
                            }

                            totalPrice = paidAmount
                            currentScreen = "paymentSuccess"
                        },
                        onBack = { currentScreen = if (selectedTicket != null) "ticketCart" else "cart" }
                    )
                    "paymentSuccess" -> PaymentSuccessScreen(
                        totalPrice = totalPrice,
                        onBackToHome = { currentScreen = "home" }
                    )
                }
            }
        }
    }

    private fun loadSavedData() {
        parkingEntryTime = appPreferences.getParkingEntryTime().takeIf { it > 0 }
        cartItems = appPreferences.getCartItems()
        selectedTicket = appPreferences.getTicketItem()
        cardId = appPreferences.getUserCardId()
        userName = appPreferences.getUserName()
    }

    private fun handleNfcTag() {
        Log.d("TMII_APP", "NFC terdeteksi di layar: $currentScreen")

        // Logika untuk menyimpan data user dan cardId saat ini
        if (cardId != null && userName != null) {
            appPreferences.saveUserData(cardId!!, userName!!)
            Log.d("TMII_APP", "Saving current user data: Card ID = $cardId, Name = $userName")
        }

        when (currentScreen) {
            "pintuMasuk" -> {
                Log.d("TMII_APP", "Processing Pintu Masuk entry")
                appPreferences.savePintuMasukEntry(System.currentTimeMillis())
                currentScreen = "pintuMasukSuccess"
            }
            "parking" -> {
                val currentParkingTime = appPreferences.getParkingEntryTime()
                if (currentParkingTime == 0L) {
                    Log.d("TMII_APP", "Processing parking check-in")
                    val currentTime = System.currentTimeMillis()
                    appPreferences.saveParkingEntryTime(currentTime)
                    parkingEntryTime = currentTime
                    currentScreen = "parkingCheckinSuccess"
                } else {
                    Log.d("TMII_APP", "Processing parking check-out")
                    // Set flag untuk trigger LaunchedEffect di ParkingCheckoutScreen
                    isNfcTapped = true
                }
            }
            "payment" -> {
                Log.d("TMII_APP", "Processing payment")
                // Set flag untuk trigger LaunchedEffect di PaymentScreen
                isNfcTapped = true
            }
        }
    }

    private fun calculateParkingPrice(entryTime: Long): Int {
        val currentTime = System.currentTimeMillis()
        val durationHours = ((currentTime - entryTime) / 3600000).toInt() + 1
        return durationHours * 5000
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val tagIdBytes = tag?.id
            val cardIdHex = tagIdBytes?.toHexString() ?: "ID_unknown"

            // Perbarui state cardId dan userName
            cardId = cardIdHex
            userName = "Pengunjung-" + cardIdHex.substring(cardIdHex.length - 4, cardIdHex.length)

            Log.d("TMII_APP", "Kartu terdeteksi. ID: $cardId, Screen: $currentScreen")
            Toast.makeText(this, "Kartu terdeteksi. ID: $cardId", Toast.LENGTH_SHORT).show()

            // Langsung panggil handleNfcTag
            handleNfcTag()
        }
    }

    // Fungsi helper untuk mengubah byte array menjadi string heksadesimal
    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}