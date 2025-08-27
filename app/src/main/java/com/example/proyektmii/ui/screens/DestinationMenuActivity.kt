package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyektmii.R

class DestinationMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_menu)

        try {
            val backButton = findViewById<ImageButton>(R.id.back_button_destinasi_menu)
            val cardMuseum = findViewById<CardView>(R.id.card_museum)
            val cardWahana = findViewById<CardView>(R.id.card_wahana)

            backButton.setOnClickListener { onBackPressed() }

            cardMuseum.setOnClickListener {
                val intent = Intent(this, DestinationSelectionActivity::class.java)
                intent.putExtra("isWahana", false)
                startActivity(intent)
            }

            cardWahana.setOnClickListener {
                val intent = Intent(this, DestinationSelectionActivity::class.java)
                intent.putExtra("isWahana", true)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Tambahkan logging atau Toast jika diperlukan
        }
    }
}