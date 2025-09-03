package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.jniinterface.SmartCardInterface
import com.cloudpos.jniinterface.SmartCardSlotInfo
import com.example.proyektmii.R
import java.text.NumberFormat
import java.util.Locale

class PintuMasukActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk)

        statusTextView = findViewById(R.id.status_textview)
        backButton = findViewById(R.id.back_button_pintu_masuk)

        backButton.setOnClickListener { onBackPressed() }

        // Memulai proses tap kartu di thread terpisah
        startCardWaitingProcess()
    }

    private fun startCardWaitingProcess() {
        runOnUiThread { statusTextView.text = "Silahkan Tap Kartu" }
        val slot = SmartCardInterface.DEFAULT_SLOT

        Thread {
            try {
                // Perbaikan: Logika yang benar adalah menunggu kartu terdeteksi terlebih dahulu.
                // Metode ini akan memblokir thread sampai kartu ditempel.
                SmartCardInterface.waitForCardPresent(slot)

                runOnUiThread { statusTextView.text = "Kartu terdeteksi. Membuka koneksi..." }

                // Lanjutkan ke transaksi NFC.
                performNfcTransaction(slot)

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    statusTextView.text = "Proses gagal. Silahkan coba lagi."
                }
            }
        }.start()
    }

    private fun performNfcTransaction(slot: Int) {
        try {
            // Setelah kartu terdeteksi, baru coba buka koneksi ke SmartCard reader.
            val openResult = SmartCardInterface.open(slot)
            if (openResult != 0) {
                runOnUiThread { statusTextView.text = "Gagal membuka koneksi ke reader. (Kode: $openResult)" }
                return
            }

            val powerOnResult = SmartCardInterface.powerOn(slot, ByteArray(64), SmartCardSlotInfo())
            if (powerOnResult != 0) {
                runOnUiThread { statusTextView.text = "Gagal menyalakan kartu. (Kode: $powerOnResult)" }
                return
            }

            runOnUiThread { statusTextView.text = "Kartu siap. Membaca data..." }

            val apdu = hexStringToByteArray("FFCA000000")
            val response = ByteArray(256)

            val transmitResult = SmartCardInterface.transmit(slot, apdu, response)

            if (transmitResult >= 0) {
                runOnUiThread {
                    statusTextView.text = "Transaksi berhasil!"
                    val intent = Intent(this@PintuMasukActivity, PintuMasukSuksesActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                runOnUiThread { statusTextView.text = "Tiket tidak valid. (Kode: $transmitResult)" }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread { statusTextView.text = "Terjadi kesalahan transaksi: ${e.message}" }
        } finally {
            try {
                SmartCardInterface.powerOff(slot)
                SmartCardInterface.close(slot)
            } catch (e: Exception) {
                // Biarkan saja jika ada error saat menutup koneksi
            }
        }
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}