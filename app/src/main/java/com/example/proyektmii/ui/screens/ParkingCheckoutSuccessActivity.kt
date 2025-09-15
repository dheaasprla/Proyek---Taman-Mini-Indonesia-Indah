package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.local.AppPreferences
import java.text.SimpleDateFormat
import java.util.*

class ParkingCheckoutSuccessActivity : AppCompatActivity() {

    private lateinit var uidTextView: TextView
    private lateinit var exitTimeText: TextView
    private lateinit var feeText: TextView
    private lateinit var balanceText: TextView
    private lateinit var backButton: ImageButton
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkout_success)

        prefs = AppPreferences(this)

        uidTextView = findViewById(R.id.uid_textview)
        exitTimeText = findViewById(R.id.exit_time_text)
        feeText = findViewById(R.id.fee_text)
        balanceText = findViewById(R.id.balance_text)
        backButton = findViewById(R.id.back_button)

        // Ambil data dari Intent
        val uid = intent.getStringExtra("uid") ?: "-"
        val entryTime = intent.getLongExtra("entryTime", 0L)
        val exitTime = intent.getLongExtra("exitTime", 0L)
        val cost = intent.getIntExtra("cost", 0)

        // Ambil saldo dari SharedPreferences
        val cardData = prefs.getCardData(uid)
        var balance = cardData?.balance ?: 0
        val name = cardData?.name ?: "Pengunjung"

        balance -= cost
        prefs.saveCardData(uid, name, balance)

        // Format jam
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))
        val entryStr = if (entryTime > 0) formatter.format(Date(entryTime)) else "-"
        val exitStr = if (exitTime > 0) formatter.format(Date(exitTime)) else "-"

        // Tampilkan data
        uidTextView.text = "UID: $uid"
        exitTimeText.text = "Masuk: $entryStr\nKeluar: $exitStr"
        feeText.text = "Biaya Parkir: Rp $cost"
        balanceText.text = "Sisa Saldo: Rp $balance"

        backButton.setOnClickListener { finish() }
    }
}
