package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.proyektmii.R

class DestinationMenuActivity : AppCompatActivity() {
    private val TAG = "DestinationMenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_menu)
        Log.d(TAG, "onCreate: DestinationMenuActivity dimulai.")

        try {
            val backButton = findViewById<ImageButton>(R.id.back_button_destinasi_menu)
            val cardMuseum = findViewById<CardView>(R.id.card_museum)
            val cardWahana = findViewById<CardView>(R.id.card_wahana)
            Log.d(TAG, "onCreate: Semua views berhasil diinisialisasi.")

            backButton.setOnClickListener { onBackPressed() }

            cardMuseum.setOnClickListener {
                Log.d(TAG, "onClick: Tombol 'Museum' ditekan. Memulai DestinationSelectionActivity.")
                val intent = Intent(this, DestinationSelectionActivity::class.java)
                intent.putExtra("isWahana", false)
                startActivity(intent)
            }

            cardWahana.setOnClickListener {
                Log.d(TAG, "onClick: Tombol 'Wahana' ditekan. Memulai DestinationSelectionActivity.")
                val intent = Intent(this, DestinationSelectionActivity::class.java)
                intent.putExtra("isWahana", true)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saat inisialisasi view di onCreate", e)
            Toast.makeText(this, "Terjadi kesalahan pada aplikasi.", Toast.LENGTH_LONG).show()
        }
    }
}