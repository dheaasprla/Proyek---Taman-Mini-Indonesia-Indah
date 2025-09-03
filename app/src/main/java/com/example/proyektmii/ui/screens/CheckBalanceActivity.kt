package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.jniinterface.SmartCardInterface
import com.cloudpos.jniinterface.SmartCardSlotInfo
import com.example.proyektmii.R
import java.text.NumberFormat
import java.util.Locale

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var balanceTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        balanceTextView = findViewById(R.id.balance_textview)
        statusTextView = findViewById(R.id.status_textview)
        backButton = findViewById(R.id.back_button_check_balance)

        backButton.setOnClickListener {
            onBackPressed()
        }

        checkBalance()
    }

    private fun checkBalance() {
        val slot = SmartCardInterface.DEFAULT_SLOT

        statusTextView.text = "Memeriksa saldo..."
        balanceTextView.text = "Rp -"

        Thread {
            try {
                val openResult = SmartCardInterface.open(slot)
                if (openResult != 0) {
                    runOnUiThread { statusTextView.text = "Gagal membuka koneksi ke reader. (Kode: $openResult)" }
                    return@Thread
                }

                val powerOnResult = SmartCardInterface.powerOn(slot, ByteArray(64), SmartCardSlotInfo())
                if (powerOnResult != 0) {
                    runOnUiThread { statusTextView.text = "Gagal menyalakan kartu. (Kode: $powerOnResult)" }
                    return@Thread
                }

                val apdu = hexStringToByteArray("FFCA000000")
                val response = ByteArray(256)

                val transmitResult = SmartCardInterface.transmit(slot, apdu, response)

                if (transmitResult >= 0) {
                    runOnUiThread { statusTextView.text = "Data berhasil dibaca." }
                    val balanceHex = response.copyOfRange(0, transmitResult).toHex()
                    val balance = hexStringToDecimal(balanceHex)

                    runOnUiThread {
                        val formattedBalance = NumberFormat.getNumberInstance(Locale("in", "ID")).format(balance)
                        balanceTextView.text = "Rp $formattedBalance"
                    }
                } else {
                    runOnUiThread { statusTextView.text = "Gagal membaca data kartu. (Kode: $transmitResult)" }
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

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun hexStringToDecimal(hex: String): Long {
        return hex.toLong(16)
    }
}