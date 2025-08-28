package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class PintuMasukSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk_success)

        val btnBack = findViewById<ImageButton>(R.id.btnBackSuccess)
        btnBack.setOnClickListener {
            // kembali ke home — menutup stack agar user tidak kembali ke layar tap sebelumnya
            finishAffinity()
        }
    }
}