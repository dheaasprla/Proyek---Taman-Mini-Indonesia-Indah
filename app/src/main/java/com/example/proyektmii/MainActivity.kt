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
import com.example.proyektmii.data.CardData
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.TicketItem
import com.example.proyektmii.data.local.AppPreferences
import com.example.proyektmii.ui.screens.*
import com.example.proyektmii.ui.theme.ProyekTMIITheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var appPreferences: AppPreferences

    private var currentScreen by mutableStateOf("onboarding")
    private var isNfcTapped by mutableStateOf(false)
    private var totalPrice by mutableStateOf(0)

    private var cardData by mutableStateOf<CardData?>(null)
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
                            "Cek Saldo" -> {
                                cardData = null
                                isNfcTapped = false
                                currentScreen = "cekSaldo"
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
                                    cardData?.let { currentCard ->
                                        if (currentCard.balance >= paidAmount) {
                                            val newBalance = currentCard.balance - paidAmount
                                            appPreferences.saveCardData(cardId!!, userName!!, newBalance)
                                            cardData = currentCard.copy(balance = newBalance)

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
                                        } else {
                                            Toast.makeText(this, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                                        }
                                    } ?: run {
                                        Toast.makeText(this, "Data kartu tidak ditemukan!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onBack = { currentScreen = "home" }
                            )
                        } else {
                            ParkingCheckinScreen(onBack = { currentScreen = "home" })
                        }
                    }
                    "parkingCheckinSuccess" -> ParkingCheckinSuccessScreen(onBackToHome = { currentScreen = "home" })
                    "parkingCheckoutSuccess" -> ParkingCheckoutSuccessScreen(
                        totalPrice = totalPrice,
                        onBackToHome = { currentScreen = "home" },
                        cardData = cardData // Kirim saldo terbaru
                    )
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
                            isNfcTapped = false
                        },
                        onPaymentSuccess = { paidAmount ->
                            cardData?.let { currentCard ->
                                if (currentCard.balance >= paidAmount) {
                                    val newBalance = currentCard.balance - paidAmount
                                    appPreferences.saveCardData(cardId!!, userName!!, newBalance)
                                    cardData = currentCard.copy(balance = newBalance)

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
                                } else {
                                    Toast.makeText(this, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Toast.makeText(this, "Data kartu tidak ditemukan!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBack = { currentScreen = if (selectedTicket != null) "ticketCart" else "cart" }
                    )
                    "paymentSuccess" -> PaymentSuccessScreen(
                        totalPrice = totalPrice,
                        onBackToHome = { currentScreen = "home" }
                    )
                    "cekSaldo" -> CheckBalanceScreen(
                        onBack = { currentScreen = "home" },
                        cardData = cardData,
                        isProcessing = isNfcTapped
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

        if (cardId != null && userName != null) {
            appPreferences.saveUserData(cardId!!, userName!!)
            // Periksa dan inisialisasi saldo jika belum ada
            val savedCardData = appPreferences.getCardData(cardId!!)
            if (savedCardData == null) {
                // Inisialisasi saldo awal (misalnya 100000) untuk ID baru
                appPreferences.saveCardData(cardId!!, userName!!, 100000)
                cardData = CardData(id = cardId!!, balance = 100000)
                Log.d("TMII_APP", "Inisialisasi baru untuk cardId: $cardId, Saldo: 100000")
            } else {
                cardData = savedCardData
                Log.d("TMII_APP", "Loaded existing card data: ID = $cardId, Balance = ${cardData?.balance}")
            }
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
                    isNfcTapped = true
                }
            }
            "payment" -> {
                Log.d("TMII_APP", "Processing payment")
                cardData?.let { currentCard ->
                    if (currentCard.balance >= totalPrice) {
                        val newBalance = currentCard.balance - totalPrice
                        appPreferences.saveCardData(cardId!!, userName!!, newBalance)
                        cardData = currentCard.copy(balance = newBalance) // Pastikan state diperbarui

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
                            totalPrice = totalPrice,
                            timestamp = currentTime
                        )
                        appPreferences.addPaymentToHistory(newHistoryItem)

                        if (selectedTicket != null) {
                            appPreferences.saveTicketItem(null)
                            selectedTicket = null
                        }
                        if (cartItems.isNotEmpty()) {
                            appPreferences.clearCartItems()
                            cartItems = emptyList()
                        }

                        // Pastikan state cardData sudah diperbarui sebelum navigasi
                        currentScreen = "paymentSuccess"
                    } else {
                        Toast.makeText(this, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "Data kartu tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
                isNfcTapped = false // Reset setelah proses
            }
            "cekSaldo" -> {
                Log.d("TMII_APP", "Processing balance check")
                isNfcTapped = false // Reset setelah proses
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

            cardId = cardIdHex
            userName = "Pengunjung-" + cardIdHex.substring(cardIdHex.length - 4, cardIdHex.length)

            Log.d("TMII_APP", "Kartu terdeteksi. ID: $cardId, Screen: $currentScreen")
            Toast.makeText(this, "Kartu terdeteksi. ID: $cardId", Toast.LENGTH_SHORT).show()

            handleNfcTag()
        }
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}
