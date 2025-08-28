package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class DestinationMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_menu)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val cardMuseum = findViewById<View>(R.id.cardMuseum)
        val cardWahana = findViewById<View>(R.id.cardWahana)

        // set content for included cardMuseum
        val museumImage = cardMuseum.findViewById<ImageView>(R.id.cardImage)
        val museumTitle = cardMuseum.findViewById<TextView>(R.id.cardTitle)
        museumImage.setImageResource(R.drawable.museum)
        museumTitle.text = "Museum"

        val wrImage = cardWahana.findViewById<ImageView>(R.id.cardImage)
        val wrTitle = cardWahana.findViewById<TextView>(R.id.cardTitle)
        wrImage.setImageResource(R.drawable.wr)
        wrTitle.text = "Wahana & Rekreasi"

        btnBack.setOnClickListener { finish() }

        cardMuseum.setOnClickListener {
            val i = Intent(this, DestinationSelectionActivity::class.java)
            i.putExtra("isWahana", false)
            startActivity(i)
        }

        cardWahana.setOnClickListener {
            val i = Intent(this, DestinationSelectionActivity::class.java)
            i.putExtra("isWahana", true)
            startActivity(i)
        }
    }
}
