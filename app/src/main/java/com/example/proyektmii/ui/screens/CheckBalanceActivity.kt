package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import java.text.NumberFormat
import java.util.*

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSimulate: Button
    private lateinit var tvCardId: TextView
    private lateinit var tvBalance: TextView
    private lateinit var pbProcessing: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        // Inisialisasi View
        btnBack = findViewById(R.id.btnBackCheck)
        btnSimulate = findViewById(R.id.btnSimulateCheck)
        tvCardId = findViewById(R.id.tvCardId)
        tvBalance = findViewById(R.id.tvBalance)
        pbProcessing = findViewById(R.id.pbProcessing)

        // Tombol kembali
        btnBack.setOnClickListener { finish() }

        // Ambil data dari intent (opsional)
        val cardIdExtra = intent.getStringExtra("cardId")
        val balanceExtra = intent.getLongExtra("balance", -1L)
        if (!cardIdExtra.isNullOrBlank() && balanceExtra >= 0L) {
            updateCardDisplay(CardData(cardIdExtra, balanceExtra.toInt()))
        }

        // Tombol simulasi (development)
        btnSimulate.setOnClickListener {
            simulateNfcRead()
        }
    }

    private fun simulateNfcRead() {
        // Tampilkan loading
        pbProcessing.visibility = View.VISIBLE
        btnSimulate.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            // Simulasi data kartu
            val simulated = CardData(id = "04A1B2C3D4E5F6", balance = 120_000)
            updateCardDisplay(simulated)

            // Sembunyikan loading
            pbProcessing.visibility = View.GONE
            btnSimulate.isEnabled = true
        }, 1000) // Diubah ke Int (1000) bukan Long (1000L)
    }

    private fun updateCardDisplay(cardData: CardData?) {
        if (cardData == null) {
            tvCardId.text = "xxxxxxxxxxxxxx"
            tvBalance.text = "Rp ..."
            return
        }

        // Tampilkan ID kartu
        tvCardId.text = cardData.id

        // Format saldo ke Rupiah
        val formatted = NumberFormat.getNumberInstance(Locale("in", "ID"))
            .format(cardData.balance)
        tvBalance.text = "Rp $formatted"
    }
}