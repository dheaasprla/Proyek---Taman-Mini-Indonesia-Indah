package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.rfcardreader.RFCardReaderDevice
import com.cloudpos.rfcardreader.RFCardReaderOperationResult
import com.cloudpos.card.Card
import com.example.proyektmii.R
import com.example.proyektmii.data.local.DummySaldoManager
import java.text.NumberFormat
import java.util.Locale

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var balanceTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var cardIdTextView: TextView
    private lateinit var backButton: ImageButton

    private var rfReader: RFCardReaderDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        balanceTextView = findViewById(R.id.balance_textview)
        statusTextView = findViewById(R.id.status_textview)
        cardIdTextView = findViewById(R.id.card_id_text)
        backButton = findViewById(R.id.back_button_check_balance)

        backButton.setOnClickListener { finish() }

        startCheckBalance()
    }

    private fun startCheckBalance() {
        statusTextView.text = "Tempelkan kartu Anda..."
        balanceTextView.text = "Rp -"
        cardIdTextView.text = "xxxxxxxxxxxx"

        Thread {
            try {
                rfReader = POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.rfcardreader") as RFCardReaderDevice

                // mode AUTO, speed default
                rfReader?.open(RFCardReaderDevice.MODE_AUTO, 0)

                // tunggu kartu selama 15 detik
                val result: RFCardReaderOperationResult =
                    rfReader!!.waitForCardPresent(15000)

                val card: Card = result.card
                val uidBytes: ByteArray = card.id
                val uid: String = uidBytes.joinToString("") { "%02X".format(it) }

                // ambil atau buat saldo dummy
                val saldo = DummySaldoManager.getOrCreateBalance(uid)
                val formatted = NumberFormat.getNumberInstance(Locale("in", "ID")).format(saldo)

                runOnUiThread {
                    statusTextView.text = "Kartu terdeteksi!"
                    cardIdTextView.text = uid
                    balanceTextView.text = "Rp $formatted"
                }

            } catch (e: DeviceException) {
                Log.e("CheckBalance", "DeviceException", e)
                runOnUiThread {
                    statusTextView.text = "Error: ${e.message}"
                    balanceTextView.text = "Rp -"
                }
            } catch (e: Exception) {
                Log.e("CheckBalance", "Exception", e)
                runOnUiThread {
                    statusTextView.text = "Kesalahan: ${e.message}"
                    balanceTextView.text = "Rp -"
                }
            } finally {
                try { rfReader?.close() } catch (_: Exception) {}
            }
        }.start()
    }
}
