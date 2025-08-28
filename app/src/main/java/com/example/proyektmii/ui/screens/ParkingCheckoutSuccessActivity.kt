package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import java.text.NumberFormat
import java.util.*

class ParkingCheckoutSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkout_success)

        val btnBack = findViewById<ImageButton>(R.id.btnBackCheckoutSuccess)
        val tvPaid = findViewById<TextView>(R.id.tvPaidTotal)

        val total = intent.getIntExtra("totalPrice", 0)
        tvPaid.text = "Rp ${NumberFormat.getInstance(Locale("in", "ID")).format(total)}"

        btnBack.setOnClickListener {
            // Kembali ke home dan tutup semua
            finishAffinity()
        }
    }
}