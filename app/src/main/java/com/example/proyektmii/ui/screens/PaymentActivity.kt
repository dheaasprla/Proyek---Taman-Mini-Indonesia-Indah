package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.rfcardreader.RFCardReaderDevice
import com.cloudpos.rfcardreader.RFCardReaderOperationResult
import com.example.proyektmii.R
import com.example.proyektmii.data.local.DummySaldoManager
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var totalPriceTextView: TextView
    private lateinit var nfcSection: View
    private lateinit var loadingSection: View

    private var rfReader: RFCardReaderDevice? = null
    private val TAG = "PaymentActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        statusTextView = findViewById(R.id.status_textview)
        backButton = findViewById(R.id.back_button_payment)
        totalPriceTextView = findViewById(R.id.total_price_text)
        nfcSection = findViewById(R.id.nfc_section)
        loadingSection = findViewById(R.id.loading_section)

        val totalPrice = intent.getLongExtra("totalPrice", 0L)
        totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"

        backButton.setOnClickListener { onBackPressed() }

        // Memulai proses pembayaran secara otomatis saat layar terbuka
        performPayment(totalPrice)
    }

    private fun performPayment(amount: Long) {
        statusTextView.text = "Tempelkan kartu Anda..."
        nfcSection.visibility = View.VISIBLE
        loadingSection.visibility = View.GONE

        Thread {
            try {
                rfReader = POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.rfcardreader") as RFCardReaderDevice

                if (rfReader == null) {
                    runOnUiThread {
                        statusTextView.text = "Error: Perangkat NFC tidak ditemukan."
                        nfcSection.visibility = View.GONE
                    }
                    return@Thread
                }

                rfReader?.open(RFCardReaderDevice.MODE_AUTO, 0)
                val result: RFCardReaderOperationResult = rfReader!!.waitForCardPresent(15000)

                if (result.resultCode == RFCardReaderOperationResult.SUCCESS) {
                    runOnUiThread {
                        statusTextView.text = "Memproses pembayaran..."
                        nfcSection.visibility = View.GONE
                        loadingSection.visibility = View.VISIBLE
                    }

                    val cardId = result.card.id.joinToString("") { "%02X".format(it) }
                    val currentBalance = DummySaldoManager.getOrCreateBalance(cardId).toLong()

                    if (currentBalance >= amount) {
                        Thread.sleep(2000)

                        DummySaldoManager.updateBalance(cardId, amount)
                        val newBalance = DummySaldoManager.getBalance(cardId)

                        runOnUiThread {
                            statusTextView.text = "Pembayaran berhasil!"
                            val intent = Intent(this@PaymentActivity, PaymentSuccessActivity::class.java)
                            intent.putExtra("totalPrice", amount.toInt())
                            intent.putExtra("newBalance", newBalance.toInt())
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            statusTextView.text = "Saldo tidak mencukupi."
                            loadingSection.visibility = View.GONE
                            nfcSection.visibility = View.GONE
                        }
                    }
                } else {
                    runOnUiThread {
                        statusTextView.text = "Gagal mendeteksi kartu. Coba lagi."
                        nfcSection.visibility = View.GONE
                    }
                }

            } catch (e: DeviceException) {
                runOnUiThread {
                    statusTextView.text = "Error perangkat: ${e.message}"
                    nfcSection.visibility = View.GONE
                }
            } catch (e: Exception) {
                runOnUiThread {
                    statusTextView.text = "Kesalahan: ${e.message}"
                    nfcSection.visibility = View.GONE
                }
            } finally {
                try { rfReader?.close() } catch (_: Exception) {}
            }
        }.start()
    }
}