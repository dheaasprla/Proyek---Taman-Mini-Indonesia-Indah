package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.rfcardreader.RFCardReaderDevice
import com.cloudpos.rfcardreader.RFCardReaderOperationResult
import com.cloudpos.card.Card
import com.example.proyektmii.R

class PintuMasukActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var textStatus: TextView
    private lateinit var backButton: ImageButton

    private var rfReader: RFCardReaderDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk)

        statusTextView = findViewById(R.id.status_textview)
        textStatus = findViewById(R.id.text_status)
        backButton = findViewById(R.id.back_button_pintu_masuk)

        backButton.setOnClickListener { finish() }

        // mulai scan saat activity dibuka
        startCardDetection()
    }

    private fun startCardDetection() {
        statusTextView.text = "Menunggu kartu..."
        textStatus.text = "Tempelkan kartu Anda"

        Thread {
            try {
                // ambil NFC reader dari terminal
                rfReader = POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.rfcardreader") as RFCardReaderDevice

                rfReader?.open(RFCardReaderDevice.MODE_AUTO, 0)

                // tunggu kartu ditempel, timeout 15 detik
                val result: RFCardReaderOperationResult =
                    rfReader!!.waitForCardPresent(15000)

                val card: Card = result.card
                val uidBytes: ByteArray = card.id
                val uid: String = uidBytes.joinToString("") { "%02X".format(it) }

                runOnUiThread {
                    statusTextView.text = "Kartu terdeteksi!"
                    textStatus.text = "UID: $uid"

                    // pindah ke halaman sukses
                    val intent = Intent(this, PintuMasukSuksesActivity::class.java)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                    finish()
                }

            } catch (e: DeviceException) {
                runOnUiThread {
                    statusTextView.text = "Error: ${e.message}"
                    textStatus.text = "Silakan coba lagi"
                }
            } finally {
                try { rfReader?.close() } catch (_: Exception) {}
            }
        }.start()
    }
}