package com.example.proyektmii

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyektmii.ui.screens.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<CardView>(R.id.card_pintu_masuk).setOnClickListener {
            startActivity(Intent(this, PintuMasukActivity::class.java))
        }

        findViewById<CardView>(R.id.card_parkir).setOnClickListener {
            startActivity(Intent(this, ParkingCheckinActivity::class.java))
        }

        findViewById<CardView>(R.id.card_destinasi).setOnClickListener {
            startActivity(Intent(this, DestinationMenuActivity::class.java))
        }

        findViewById<CardView>(R.id.card_kantin).setOnClickListener {
            startActivity(Intent(this, CanteenMenuActivity::class.java))
        }

        findViewById<CardView>(R.id.card_ceksaldo).setOnClickListener {
            startActivity(Intent(this, CheckBalanceActivity::class.java))
        }
    }
}
