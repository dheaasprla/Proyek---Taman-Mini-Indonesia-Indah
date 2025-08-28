package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData

class PaymentSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        // Ubah getIntExtra ke getLongExtra
        val paid = intent.getLongExtra("paid", 0L)
        val cardData = intent.getParcelableExtra<CardData>("cardData")

        val tvPaid = findViewById<TextView>(R.id.tvPaidAmount)
        val tvRemaining = findViewById<TextView>(R.id.tvRemaining)
        val btnBack = findViewById<Button>(R.id.btnBackHome)

        tvPaid.text = "Total: Rp ${formatPrice(paid.toInt())}"
        tvRemaining.text = cardData?.let { "Sisa Saldo: Rp ${formatPrice(it.balance)}" } ?: ""

        btnBack.setOnClickListener {
            finishAffinity()
        }
    }

    // Ubah parameter dari Int ke Long
    private fun formatPrice(value: Int): String {
        return java.text.NumberFormat.getInstance(java.util.Locale("in", "ID")).format(value)
    }
}