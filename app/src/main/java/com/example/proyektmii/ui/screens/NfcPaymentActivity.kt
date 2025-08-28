package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.OperationResult
import com.cloudpos.card.MifareCard
import com.cloudpos.rfcardreader.RFCardReaderDevice
import com.cloudpos.rfcardreader.RFCardReaderOperationResult
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class NfcPaymentActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private var totalPrice: Int = 0
    private var cardId: String? = null
    private var userName: String? = null

    private lateinit var totalPriceTextView: TextView
    private lateinit var nfcInstructionTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var nfcCardImage: ImageView
    private lateinit var nfcAreaFrame: View // Add this line

    private val uiHandler = Handler(Looper.getMainLooper())
    private val TAG = "NfcPaymentActivity"

    private var rfCardReaderDevice: RFCardReaderDevice? = null
    private var nfcDetectionThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_payment)

        appPreferences = AppPreferences(this)
        totalPrice = intent.getIntExtra("totalPrice", 0)

        // Memperbaiki referensi ID
        totalPriceTextView = findViewById(R.id.total_price_text)
        nfcInstructionTextView = findViewById(R.id.nfc_instruction_text)
        loadingIndicator = findViewById(R.id.loading_indicator)
        loadingText = findViewById(R.id.loading_text)
        nfcCardImage = findViewById(R.id.nfc_card_image)
        nfcAreaFrame = findViewById(R.id.nfc_area_frame) // Add this line

        updateUI(isProcessing = false)

        findViewById<ImageButton>(R.id.back_button_nfc_payment).setOnClickListener {
            onBackPressed()
        }

        try {
            rfCardReaderDevice = POSTerminal.getInstance(applicationContext).getDevice("RFCardReader") as RFCardReaderDevice
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mendapatkan perangkat RFCardReader", e)
            Toast.makeText(this, "Gagal mendapatkan perangkat NFC.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        stopNfcDetection()
        startNfcDetection()
    }

    override fun onPause() {
        super.onPause()
        stopNfcDetection()
    }

    private fun startNfcDetection() {
        if (rfCardReaderDevice == null) {
            Log.e(TAG, "RFCardReaderDevice belum diinisialisasi.")
            return
        }
        updateUI(isProcessing = false)

        Log.d(TAG, "Memulai deteksi NFC...")
        nfcDetectionThread = Thread {
            try {
                rfCardReaderDevice?.open()
                val result = rfCardReaderDevice?.waitForCardPresent(60000)

                if (result?.resultCode == OperationResult.SUCCESS) {
                    val rfCardResult = result as? RFCardReaderOperationResult
                    val card = rfCardResult?.card as? MifareCard

                    if (card != null) {
                        val cardIdBytes = card.id
                        val cardIdHex = cardIdBytes.toHexString()
                        Log.d(TAG, "Tag NFC terdeteksi: $cardIdHex")

                        uiHandler.post {
                            if (!isFinishing) {
                                this@NfcPaymentActivity.handleNfcTag(cardIdHex)
                            }
                        }
                    } else {
                        Log.e(TAG, "Kartu tidak terbaca atau bukan tipe Mifare.")
                        uiHandler.post {
                            if (!isFinishing) {
                                Toast.makeText(this@NfcPaymentActivity, "Pembayaran gagal: Kartu tidak terbaca atau tidak kompatibel.", Toast.LENGTH_SHORT).show()
                                updateUI(isProcessing = false)
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Deteksi NFC gagal atau waktu habis: ${result?.resultCode}")
                    uiHandler.post {
                        if (!isFinishing) {
                            Toast.makeText(this@NfcPaymentActivity, "Deteksi kartu NFC gagal atau waktu habis.", Toast.LENGTH_SHORT).show()
                            updateUI(isProcessing = false)
                        }
                    }
                }
            } catch (e: DeviceException) {
                Log.e(TAG, "DeviceException selama deteksi NFC", e)
                uiHandler.post {
                    if (!isFinishing) {
                        Toast.makeText(this@NfcPaymentActivity, "Kesalahan perangkat NFC: ${e.message}", Toast.LENGTH_LONG).show()
                        updateUI(isProcessing = false)
                    }
                }
            } finally {
                try {
                    rfCardReaderDevice?.close()
                } catch (e: DeviceException) {
                    Log.e(TAG, "Gagal menutup perangkat RFCardReader", e)
                }
            }
        }
        nfcDetectionThread?.start()
    }

    private fun stopNfcDetection() {
        Log.d(TAG, "Menghentikan deteksi NFC...")
        try {
            rfCardReaderDevice?.cancelRequest()
            if (nfcDetectionThread?.isAlive == true) {
                nfcDetectionThread?.interrupt()
            }
        } catch (e: DeviceException) {
            Log.e(TAG, "Gagal membatalkan permintaan NFC", e)
        }
    }

    private fun handleNfcTag(tagId: String) {
        cardId = tagId
        userName = "Pengunjung-" + tagId.substring(tagId.length - 4, tagId.length)
        updateUI(isProcessing = true)

        uiHandler.postDelayed({
            handlePaymentLogic()
        }, 2000)
    }

    private fun updateUI(isProcessing: Boolean) {
        if (isProcessing) {
            totalPriceTextView.visibility = View.GONE
            nfcInstructionTextView.visibility = View.GONE
            nfcCardImage.visibility = View.GONE
            nfcAreaFrame.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE
        } else {
            totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
            totalPriceTextView.visibility = View.VISIBLE
            nfcInstructionTextView.visibility = View.VISIBLE
            nfcCardImage.visibility = View.VISIBLE
            nfcAreaFrame.visibility = View.VISIBLE
            loadingIndicator.visibility = View.GONE
            loadingText.visibility = View.GONE
        }
    }

    private fun handlePaymentLogic() {
        val currentCardId = cardId ?: run {
            updateUI(isProcessing = false)
            Toast.makeText(this, "Pembayaran gagal: ID kartu tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val cardData: CardData? = appPreferences.getCardData(currentCardId)
        val paidItems = appPreferences.getCartItems().map { "${it.menuItem.name} x${it.quantity}" }.toMutableList()
        val paidTicket = appPreferences.getTicketItem()
        if (paidTicket != null) {
            paidItems.add("${paidTicket.destination.name} x${paidTicket.quantity}")
        }

        if (cardData != null) {
            if (cardData.balance >= totalPrice) {
                val newBalance = cardData.balance - totalPrice
                val userToSave = cardData.userName ?: userName ?: "Default User"

                // Perbaikan: Gunakan method yang ada di AppPreferences untuk menyimpan data kartu
                appPreferences.saveCardData(cardData.id, userToSave, newBalance)

                val newHistoryItem = PaymentHistoryItem(
                    cardId = cardData.id,
                    userName = userToSave,
                    transactionType = if (paidTicket != null) "Tiket" else "Kantin",
                    items = paidItems,
                    totalPrice = totalPrice,
                    timestamp = System.currentTimeMillis()
                )
                appPreferences.addPaymentToHistory(newHistoryItem)

                appPreferences.clearCartItems()
                appPreferences.saveTicketItem(null)

                val successIntent = Intent(this, PaymentSuccessActivity::class.java)
                successIntent.putExtra("totalPrice", totalPrice)
                successIntent.putExtra("newBalance", newBalance)
                startActivity(successIntent)
                finish()
            } else {
                Toast.makeText(this, "Pembayaran gagal: Saldo tidak cukup.", Toast.LENGTH_SHORT).show()
                updateUI(isProcessing = false)
            }
        } else {
            Toast.makeText(this, "Pembayaran gagal: Kartu tidak ditemukan.", Toast.LENGTH_SHORT).show()
            updateUI(isProcessing = false)
        }
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}