package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.jniinterface.SmartCardInterface
import com.cloudpos.jniinterface.SmartCardSlotInfo
import com.example.proyektmii.R
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var payButton: MaterialButton
    private lateinit var totalPriceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        statusTextView = findViewById(R.id.status_textview)
        backButton = findViewById(R.id.back_button_payment)
        payButton = findViewById(R.id.pay_button)
        totalPriceTextView = findViewById(R.id.total_price_text)

        val totalPrice = intent.getLongExtra("totalPrice", 0L)
        totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"

        backButton.setOnClickListener { onBackPressed() }
        payButton.setOnClickListener { performPayment(totalPrice) }
    }

    private fun performPayment(amount: Long) {
        val slot = SmartCardInterface.DEFAULT_SLOT

        statusTextView.text = "Memproses pembayaran..."

        Thread {
            try {
                val openResult = SmartCardInterface.open(slot)
                if (openResult == 0) {
                    runOnUiThread { statusTextView.text = "Koneksi berhasil dibuka." }

                    val powerOnResult = SmartCardInterface.powerOn(slot, ByteArray(64), SmartCardSlotInfo())
                    if (powerOnResult == 0) {
                        runOnUiThread { statusTextView.text = "Kartu terdeteksi. Melakukan transaksi..." }

                        val amountHex = String.format("%08X", amount)
                        val apdu = hexStringToByteArray("D1D1D1D1" + amountHex)
                        val response = ByteArray(256)

                        val transmitResult = SmartCardInterface.transmit(slot, apdu, response)

                        if (transmitResult >= 0) {
                            runOnUiThread { statusTextView.text = "Pembayaran berhasil!" }
                        } else {
                            runOnUiThread { statusTextView.text = "Gagal melakukan transaksi. (Kode: $transmitResult)" }
                        }
                    } else {
                        runOnUiThread { statusTextView.text = "Gagal menyalakan kartu. (Kode: $powerOnResult)" }
                    }
                } else {
                    runOnUiThread { statusTextView.text = "Gagal membuka koneksi. (Kode: $openResult)" }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { statusTextView.text = "Terjadi kesalahan: ${e.message}" }
                SmartCardInterface.notifyCancel()
            } finally {
                try {
                    SmartCardInterface.powerOff(slot)
                    SmartCardInterface.close(slot)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
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