package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyektmii.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Mengambil referensi ke CardView dari layout dengan pengecekan null
        try {
            val cardPintuMasuk = findViewById<CardView>(R.id.card_pintu_masuk)
            val cardParkir = findViewById<CardView>(R.id.card_parkir)
            val cardDestinasi = findViewById<CardView>(R.id.card_destinasi)
            val cardKantin = findViewById<CardView>(R.id.card_kantin)
            val cardCekSaldo = findViewById<CardView>(R.id.card_ceksaldo)

            cardPintuMasuk.setOnClickListener {
                startActivity(Intent(this, PintuMasukActivity::class.java))
            }

            cardParkir.setOnClickListener {
                startActivity(Intent(this, ParkingCheckinActivity::class.java))
            }

            cardDestinasi.setOnClickListener {
                startActivity(Intent(this, DestinationMenuActivity::class.java))
            }

            cardKantin.setOnClickListener {
                startActivity(Intent(this, CanteenMenuActivity::class.java))
            }

            cardCekSaldo.setOnClickListener {
                startActivity(Intent(this, CheckBalanceActivity::class.java))
            }
        } catch (e: Exception) {
            // Ini akan menangkap jika ada View yang tidak ditemukan di layout
            // dan akan mencetak stack trace.
            e.printStackTrace()
        }
    }
}