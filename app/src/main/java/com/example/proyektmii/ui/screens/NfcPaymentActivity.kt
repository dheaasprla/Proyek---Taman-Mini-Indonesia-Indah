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
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class NfcPaymentActivity : AppCompatActivity() {
    private lateinit var appPreferences: AppPreferences
    private var nfcAdapter: NfcAdapter? = null

    private var totalPrice: Int = 0
    private var cardId: String? = null
    private var userName: String? = null

    private lateinit var instructionText: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var nfcImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_payment)

        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        totalPrice = intent.getIntExtra("totalPrice", 0)

        // Memperbaiki referensi ID
        instructionText = findViewById(R.id.nfc_instruction_text)
        loadingIndicator = findViewById(R.id.loading_indicator)
        loadingText = findViewById(R.id.loading_text)
        nfcImage = findViewById(R.id.nfc_card_image)

        updateUI(isProcessing = false, totalPrice)

        // Memperbaiki referensi ID
        findViewById<ImageButton>(R.id.back_button_nfc_payment).setOnClickListener {
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
            val tagId = tag?.id?.toHexString() ?: "ID_unknown"

            cardId = tagId
            userName = "Pengunjung-" + cardId!!.substring(cardId!!.length - 4, cardId!!.length)

            updateUI(isProcessing = true, totalPrice)

            Handler(Looper.getMainLooper()).postDelayed({
                handlePaymentLogic()
            }, 2000)
        }
    }

    private fun updateUI(isProcessing: Boolean, price: Int) {
        val totalPriceTextView: TextView = findViewById(R.id.total_price_text)
        if (isProcessing) {
            totalPriceTextView.visibility = View.GONE
            instructionText.visibility = View.GONE
            nfcImage.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE
            loadingText.text = "Memproses pembayaran..."
        } else {
            totalPriceTextView.visibility = View.VISIBLE
            instructionText.visibility = View.VISIBLE
            nfcImage.visibility = View.VISIBLE
            loadingIndicator.visibility = View.GONE
            loadingText.visibility = View.GONE
            totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(price)}"
            instructionText.text = "Silakan tempelkan kartu untuk membayar"
        }
    }

    private fun handlePaymentLogic() {
        val cardData: CardData? = cardId?.let { appPreferences.getCardData(it) }

        if (cardData != null) {
            if (cardData.balance >= totalPrice) {
                val newBalance = cardData.balance - totalPrice

                val userToSave = cardData.userName ?: "Default User"
                appPreferences.saveCardData(cardData.id, userToSave, newBalance)

                val successIntent = Intent(this, PaymentSuccessActivity::class.java)
                successIntent.putExtra("totalPrice", totalPrice)
                successIntent.putExtra("newBalance", newBalance)
                startActivity(successIntent)
                finish()
            } else {
                Toast.makeText(this, "Pembayaran gagal: Saldo tidak cukup.", Toast.LENGTH_SHORT).show()
                updateUI(isProcessing = false, totalPrice)
            }
        } else {
            Toast.makeText(this, "Pembayaran gagal: Kartu tidak valid.", Toast.LENGTH_SHORT).show()
            updateUI(isProcessing = false, totalPrice)
        }
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}