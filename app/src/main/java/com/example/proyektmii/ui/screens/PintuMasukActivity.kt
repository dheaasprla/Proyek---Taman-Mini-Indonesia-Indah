package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.card.Card
import com.cloudpos.rfcardreader.RFCardReaderDevice
import com.cloudpos.rfcardreader.RFCardReaderOperationResult
import com.example.proyektmii.R
import com.example.proyektmii.util.PrintHelper

/**
 * Activity Pintu Masuk:
 * - Menunggu tap kartu NFC.
 * - Setelah sukses, cetak struk dengan logo & font besar/tebal.
 */
class PintuMasukActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var textStatus: TextView
    private lateinit var backButton: ImageButton

    private var rfReader: RFCardReaderDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk)

        statusTextView = findViewById(R.id.status_textview)
        textStatus     = findViewById(R.id.text_status)
        backButton     = findViewById(R.id.back_button_pintu_masuk)

        backButton.setOnClickListener { finish() }

        startCardDetection()
    }

    private fun startCardDetection() {
        statusTextView.text = "Menunggu kartu..."
        textStatus.text     = "Tempelkan kartu Anda"

        Thread {
            try {
                rfReader = POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.rfcardreader") as RFCardReaderDevice

                rfReader?.open(RFCardReaderDevice.MODE_AUTO, 0)

                val result: RFCardReaderOperationResult =
                    rfReader!!.waitForCardPresent(15000) // timeout 15s

                val card: Card = result.card
                val uid = card.id.joinToString("") { "%02X".format(it) }

                runOnUiThread {
                    statusTextView.text = "Kartu terdeteksi!"
                    textStatus.text     = "UID: $uid"

                    // Cetak struk masuk
                    val items = listOf(
                        PrintHelper.ReceiptItem(
                            name  = "Tiket Masuk TMII",
                            qty   = 1,
                            price = 15000L
                        )
                    )
                    PrintHelper.printReceipt(
                        context = this,
                        title   = "TIKET MASUK TMII",
                        uid     = uid,
                        items   = items
                    )

                    // Pindah ke layar sukses
                    val intent = Intent(this, PintuMasukSuksesActivity::class.java)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                    finish()
                }

            } catch (e: DeviceException) {
                runOnUiThread {
                    statusTextView.text = "Error: ${e.message}"
                    textStatus.text     = "Silakan coba lagi"
                }
            } finally {
                try { rfReader?.close() } catch (_: Exception) {}
            }
        }.start()
    }
}
