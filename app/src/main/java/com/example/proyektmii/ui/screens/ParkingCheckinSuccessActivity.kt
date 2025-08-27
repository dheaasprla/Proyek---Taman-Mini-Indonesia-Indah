package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.MainActivity
import com.example.proyektmii.R

class ParkingCheckinSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Perbaikan: Pastikan ini merujuk ke file layout yang baru dibuat
        setContentView(R.layout.activity_parking_checkin_success)

        findViewById<ImageButton>(R.id.back_button_parking_success).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }, 3000)
    }
}