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
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.FrameLayout // Tambahkan import ini
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private var nfcAdapter: NfcAdapter? = null

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var cardInfoLayout: LinearLayout
    private lateinit var cardIdText: TextView
    private lateinit var balanceText: TextView
    private lateinit var instructionText: TextView
    private lateinit var nfcContainer: FrameLayout // Tipe data sudah benar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Inisialisasi semua view dengan ID yang benar
        loadingIndicator = findViewById(R.id.loading_indicator)
        cardInfoLayout = findViewById(R.id.card_info_layout)
        cardIdText = findViewById(R.id.card_id_text)
        balanceText = findViewById(R.id.balance_text)
        instructionText = findViewById(R.id.instruction_text)
        // Perbaiki baris ini untuk secara eksplisit menentukan tipe data
        nfcContainer = findViewById(R.id.nfc_container)

        findViewById<ImageButton>(R.id.back_button_check_balance).setOnClickListener {
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
            val cardId = tagIdBytes?.toHexString() ?: "ID_unknown"

            loadingIndicator.visibility = View.VISIBLE
            nfcContainer.visibility = View.GONE
            instructionText.text = "Memproses..."

            Handler(Looper.getMainLooper()).postDelayed({
                val cardData = appPreferences.getCardData(cardId)
                if (cardData != null) {
                    cardIdText.text = cardData.id
                    balanceText.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(cardData.balance)}"
                    loadingIndicator.visibility = View.GONE
                    cardInfoLayout.visibility = View.VISIBLE
                    instructionText.text = "Informasi Kartu"
                } else {
                    Toast.makeText(this, "Data kartu tidak ditemukan. Kartu baru dibuat.", Toast.LENGTH_SHORT).show()
                    val newCardId = cardId
                    val newUserName = "Pengunjung-" + newCardId.substring(newCardId.length - 4, newCardId.length)
                    appPreferences.saveCardData(newCardId, newUserName, 100000)
                    cardIdText.text = newCardId
                    balanceText.text = "Rp 100.000"
                    loadingIndicator.visibility = View.GONE
                    cardInfoLayout.visibility = View.VISIBLE
                    instructionText.text = "Informasi Kartu"
                }
            }, 2000)
        }
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}