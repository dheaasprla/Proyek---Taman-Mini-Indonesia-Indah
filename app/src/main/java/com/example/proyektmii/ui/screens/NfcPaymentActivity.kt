package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.cloudpos.jniinterface.SmartCardInterface
import com.cloudpos.jniinterface.SmartCardSlotInfo
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class NfcPaymentActivity : AppCompatActivity() {

    private lateinit var balanceTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton // Perbaikan: Mengubah tipe dari MaterialButton menjadi ImageButton
    private lateinit var refreshButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_payment)

        // Perbaikan: Menggunakan ID yang benar sesuai XML
        balanceTextView = findViewById(R.id.total_price_text)
        statusTextView = findViewById(R.id.nfc_instruction_text)
        backButton = findViewById(R.id.back_button_nfc_payment)
        // refreshButton tidak ada di XML yang Anda berikan,
        // jadi baris ini harus dihapus atau Anda tambahkan ID-nya di XML.
        // refreshButton = findViewById(R.id.refresh_button_nfc_payment)

        backButton.setOnClickListener { onBackPressed() }
        // Jika refreshButton tidak ada, hapus bagian ini
        // refreshButton.setOnClickListener { checkNfcBalance() }

        checkNfcBalance()
    }

    private fun checkNfcBalance() {
        val slot = SmartCardInterface.DEFAULT_SLOT

        statusTextView.text = "Memeriksa saldo NFC..."
        balanceTextView.text = "Rp -"

        Thread {
            try {
                val openResult = SmartCardInterface.open(slot)
                if (openResult == 0) {
                    runOnUiThread { statusTextView.text = "Koneksi NFC berhasil dibuka." }

                    val slotInfo = SmartCardSlotInfo()
                    val atr = ByteArray(64)
                    val powerOnResult = SmartCardInterface.powerOn(slot, atr, slotInfo)

                    if (powerOnResult == 0) {
                        runOnUiThread { statusTextView.text = "Kartu NFC terdeteksi. Membaca data..." }

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
                    } else {
                        runOnUiThread { statusTextView.text = "Gagal menyalakan kartu. (Kode: $powerOnResult)" }
                    }
                } else {
                    runOnUiThread { statusTextView.text = "Gagal membuka koneksi ke NFC reader. (Kode: $openResult)" }
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