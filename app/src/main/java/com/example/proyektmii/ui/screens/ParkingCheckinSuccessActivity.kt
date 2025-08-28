package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class ParkingCheckinSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkin_success)

        val btnBack = findViewById<ImageButton>(R.id.btnBackParkSuccess)
        btnBack.setOnClickListener {
            // kembali ke Home
            finishAffinity()
        }
    }
}