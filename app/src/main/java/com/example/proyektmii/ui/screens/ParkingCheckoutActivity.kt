package com.example.proyektmii.ui.screens

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ParkingCheckoutActivity : AppCompatActivity() {
    private lateinit var appPreferences: AppPreferences
    private var nfcAdapter: NfcAdapter? = null

    private lateinit var totalPriceTextView: TextView
    private lateinit var nfcInstructionTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var nfcCardImage: ImageView

    private var parkingEntryTime: Long = 0L
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkout)

        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        parkingEntryTime = appPreferences.getParkingEntryTime()
        totalPrice = calculateParkingPrice(parkingEntryTime)

        val entryTimeTextView = findViewById<TextView>(R.id.entry_time_text)
        val exitTimeTextView = findViewById<TextView>(R.id.exit_time_text)
        totalPriceTextView = findViewById(R.id.total_price_text)
        nfcInstructionTextView = findViewById(R.id.nfc_instruction_text)
        loadingIndicator = findViewById(R.id.loading_indicator)
        loadingText = findViewById(R.id.loading_text)
        nfcCardImage = findViewById(R.id.nfc_card_image)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        entryTimeTextView.text = dateFormat.format(Date(parkingEntryTime))
        exitTimeTextView.text = dateFormat.format(Date(System.currentTimeMillis()))
        totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"

        findViewById<ImageButton>(R.id.back_button_parking_checkout).setOnClickListener {
            onBackPressed()
        }
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
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val tagIdBytes = tag?.id
            // Menggunakan fungsi toHexString() manual
            val cardId = tagIdBytes?.toHexString() ?: "ID_unknown"

            nfcCardImage.visibility = View.GONE
            nfcInstructionTextView.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                handleCheckoutPayment(cardId)
            }, 500)
        }
    }

    private fun handleCheckoutPayment(cardId: String) {
        val cardData = appPreferences.getCardData(cardId)

        cardData?.let { currentCard ->
            if (currentCard.balance >= totalPrice) {
                val newBalance = currentCard.balance - totalPrice
                val userToSave = currentCard.userName ?: "Default User"
                appPreferences.saveCardData(cardId, userToSave, newBalance)
                appPreferences.clearParkingData()

                val newHistoryItem = PaymentHistoryItem(
                    cardId = cardId,
                    userName = currentCard.userName,
                    transactionType = "Parkir",
                    items = listOf("Parkir"),
                    totalPrice = totalPrice,
                    timestamp = System.currentTimeMillis()
                )
                appPreferences.addPaymentToHistory(newHistoryItem)

                val intent = Intent(this, ParkingCheckoutSuccessActivity::class.java)
                intent.putExtra("totalPrice", totalPrice)
                intent.putExtra("newBalance", newBalance)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                loadingIndicator.visibility = View.GONE
                loadingText.visibility = View.GONE
                nfcCardImage.visibility = View.VISIBLE
                nfcInstructionTextView.visibility = View.VISIBLE
            }
        } ?: run {
            Toast.makeText(this, "Data kartu tidak ditemukan!", Toast.LENGTH_SHORT).show()
            loadingIndicator.visibility = View.GONE
            loadingText.visibility = View.GONE
            nfcCardImage.visibility = View.VISIBLE
            nfcInstructionTextView.visibility = View.VISIBLE
        }
    }

    private fun calculateParkingPrice(entryTime: Long): Int {
        val currentTime = System.currentTimeMillis()
        val durationHours = ((currentTime - entryTime) / 3600000).toInt() + 1
        return durationHours * 5000
    }

    // Fungsi toHexString() manual
    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}