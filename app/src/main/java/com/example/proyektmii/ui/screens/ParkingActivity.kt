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
import com.example.proyektmii.data.local.AppPreferences

class ParkingActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var prefs: AppPreferences
    private var rfReader: RFCardReaderDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)

        statusTextView = findViewById(R.id.status_textview)
        backButton     = findViewById(R.id.back_button_parking)
        prefs          = AppPreferences(this)

        backButton.setOnClickListener { finish() }
        startCardDetection()
    }

    private fun startCardDetection() {
        statusTextView.text = "Tempelkan kartu untuk masuk / keluar parkir..."

        Thread {
            try {
                rfReader = POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.rfcardreader") as RFCardReaderDevice
                rfReader?.open(RFCardReaderDevice.MODE_AUTO, 0)

                // tunggu kartu ditempel
                val result: RFCardReaderOperationResult = rfReader!!.waitForCardPresent(15000)
                val card: Card? = result.card

                if (result.resultCode == RFCardReaderOperationResult.SUCCESS && card != null) {
                    val uid = card.id.joinToString("") { "%02X".format(it) }
                    runOnUiThread { handleCardTap(uid) }
                } else {
                    runOnUiThread { statusTextView.text = "Gagal mendeteksi kartu. Coba lagi." }
                }

            } catch (e: DeviceException) {
                runOnUiThread { statusTextView.text = "Error device: ${e.message}" }
            } finally {
                try { rfReader?.close() } catch (_: Exception) {}
            }
        }.start()
    }

    private fun handleCardTap(uid: String) {
        val entryTime = prefs.getParkingEntryTime()
        val cardData  = prefs.getCardData(uid)

        if (entryTime == 0L) {
            // === Check-in (Masuk) ===
            prefs.saveParkingEntryTime(System.currentTimeMillis())
            if (cardData == null) {
                // Buat data kartu jika belum ada
                prefs.saveCardData(uid, "Pengunjung", 100000)
            }
            val intent = Intent(this, ParkingSuccessActivity::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("entryTime", System.currentTimeMillis())
            startActivity(intent)
            finish()
        } else {
            // === Checkout (Keluar) ===
            val exitTime = System.currentTimeMillis()
            val durationMinutes = ((exitTime - entryTime) / 60000).toInt()
            val cost = calculateParkingFee(durationMinutes)

            prefs.clearParkingData()

            val intent = Intent(this, ParkingCheckoutSuccessActivity::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("entryTime", entryTime)
            intent.putExtra("exitTime", exitTime)
            intent.putExtra("cost", cost)
            startActivity(intent)
            finish()
        }
    }

    private fun calculateParkingFee(minutes: Int): Int {
        val baseRate = 5000
        val perHour  = 3000
        return if (minutes <= 60) baseRate
        else baseRate + ((minutes - 60) / 60) * perHour
    }
}
