package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class PintuMasukSuksesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk_sukses)

        val backButton: ImageButton = findViewById(R.id.back_button_pintu_sukses)
        val uidTextView: TextView = findViewById(R.id.uid_textview)

        // ambil UID dari Intent
        val uid = intent.getStringExtra("uid")
        uidTextView.text = if (!uid.isNullOrEmpty()) {
            "UID Kartu: $uid"
        } else {
            "UID Kartu tidak terbaca"
        }

        backButton.setOnClickListener { finish() }
    }
}
