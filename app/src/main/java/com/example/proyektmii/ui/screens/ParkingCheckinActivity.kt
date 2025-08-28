package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class ParkingCheckinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkin)

        val btnBack = findViewById<ImageButton>(R.id.btnBackPark)
        val btnCheckin = findViewById<Button>(R.id.btnCheckin)

        btnBack.setOnClickListener { finish() }

        // Simulasi check-in: simpan waktu masuk (kirim ke checkout)
        btnCheckin.setOnClickListener {
            val entryTime = System.currentTimeMillis()
            val totalEstimate = 0 // untuk checkin kita belum tahu total; nanti dihitung di checkout
            val i = Intent(this, ParkingCheckinSuccessActivity::class.java)
            i.putExtra("entryTime", entryTime)
            startActivity(i)
            finish()
        }

        // TODO: integrasi CloudPOS/EDC untuk registrasi kendaraan via NFC atau QR
    }
}