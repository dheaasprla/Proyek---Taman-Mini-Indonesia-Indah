package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import com.example.proyektmii.data.CartItem

class PaymentActivity : AppCompatActivity() {

    private var totalPrice: Long = 0 // Ubah dari Int ke Long
    private lateinit var tvTotal: TextView
    private lateinit var progress: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var btnSimulate: Button
    private lateinit var btnBack: ImageButton
    private lateinit var imgTap: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Ubah getIntExtra ke getLongExtra
        totalPrice = intent.getLongExtra("total", 0L)
        val cart = intent.getParcelableArrayListExtra<CartItem>("cart")

        tvTotal = findViewById(R.id.tvTotalPayment)
        progress = findViewById(R.id.progressPayment)
        tvStatus = findViewById(R.id.tvTapStatus)
        btnSimulate = findViewById(R.id.btnSimulateNfc)
        btnBack = findViewById(R.id.btnBackPayment)
        imgTap = findViewById(R.id.imgTapCard)

        tvTotal.text = "Total: Rp ${formatPrice(totalPrice)}"

        btnBack.setOnClickListener { finish() }

        btnSimulate.setOnClickListener {
            startNfcProcessingSimulation()
        }
    }

    private fun startNfcProcessingSimulation() {
        progress.visibility = View.VISIBLE
        tvStatus.text = getString(R.string.waiting_verification)
        btnSimulate.isEnabled = false

        progress.postDelayed({
            progress.visibility = View.GONE
            btnSimulate.isEnabled = true
            // Pastikan menggunakan nilai Long (50000L)
            val cardData = CardData(id = "CARD123", balance = (50000L - totalPrice).toInt())
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            intent.putExtra("paid", totalPrice)
            intent.putExtra("cardData", cardData)
            startActivity(intent)
            finish()
        }, 2000)
    }

    // Ubah parameter dari Int ke Long
    private fun formatPrice(value: Long): String {
        return java.text.NumberFormat.getInstance(java.util.Locale("in", "ID")).format(value)
    }
}