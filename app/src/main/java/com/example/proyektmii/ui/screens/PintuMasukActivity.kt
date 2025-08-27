package com.example.proyektmii.ui.screens

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class PintuMasukActivity : AppCompatActivity() {
    private lateinit var appPreferences: AppPreferences
    private var nfcAdapter: NfcAdapter? = null

    private val HARGA_TIKET_MASUK = 25000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk)

        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        findViewById<ImageButton>(R.id.back_button_pintu_masuk).setOnClickListener {
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

            val cardData = appPreferences.getCardData(cardId)

            if (cardData != null) {
                if (cardData.balance >= HARGA_TIKET_MASUK) {
                    val newBalance = cardData.balance - HARGA_TIKET_MASUK
                    appPreferences.saveCardData(cardId, cardData.userName ?: "Default User", newBalance)

                    val newHistoryItem = PaymentHistoryItem(
                        cardId = cardId,
                        userName = cardData.userName,
                        transactionType = "Tiket Masuk",
                        items = listOf("Tiket Masuk TMII"),
                        totalPrice = HARGA_TIKET_MASUK,
                        timestamp = System.currentTimeMillis()
                    )
                    appPreferences.addPaymentToHistory(newHistoryItem)

                    Toast.makeText(this, "Pembayaran berhasil!\nSaldo tersisa: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(newBalance)}", Toast.LENGTH_LONG).show()
                    val successIntent = Intent(this, PintuMasukSuksesActivity::class.java)
                    startActivity(successIntent)
                } else {
                    Toast.makeText(this, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Data kartu tidak ditemukan. Silahkan lakukan top up atau beli kartu baru.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}