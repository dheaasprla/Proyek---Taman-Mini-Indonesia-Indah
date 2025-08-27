package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.MainActivity
import com.example.proyektmii.R
import java.text.NumberFormat
import java.util.Locale

class ParkingCheckoutSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkout_success)

        val totalPrice = intent.getIntExtra("totalPrice", 0)
        val newBalance = intent.getIntExtra("newBalance", 0)

        val totalPriceTextView = findViewById<TextView>(R.id.total_price_text)
        val saldoTersisaTextView = findViewById<TextView>(R.id.saldo_tersisa_text)
        val backButton = findViewById<ImageButton>(R.id.back_button_parking_success)

        totalPriceTextView.text = "Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
        saldoTersisaTextView.text = "Saldo Tersisa: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(newBalance)}"

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
