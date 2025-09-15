package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import java.text.SimpleDateFormat
import java.util.*

class ParkingSuccessActivity : AppCompatActivity() {

    private lateinit var uidTextView: TextView
    private lateinit var entryTimeTextView: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_success)

        uidTextView = findViewById(R.id.uid_textview)
        entryTimeTextView = findViewById(R.id.entry_time_text)
        backButton = findViewById(R.id.back_button_parking_success)

        val uid = intent.getStringExtra("uid") ?: "-"
        val entryTime = intent.getLongExtra("entryTime", 0L)

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))
        val entryStr = if (entryTime > 0) formatter.format(Date(entryTime)) else "-"

        uidTextView.text = "UID: $uid"
        entryTimeTextView.text = "Check-in sukses!\nWaktu masuk: $entryStr"

        backButton.setOnClickListener { finish() }
    }
}
