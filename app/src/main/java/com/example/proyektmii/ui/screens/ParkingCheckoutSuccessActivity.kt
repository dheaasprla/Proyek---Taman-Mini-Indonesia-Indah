package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.local.AppPreferences
import com.example.proyektmii.data.local.DummySaldoManager
import com.example.proyektmii.util.PrintHelper
import java.text.NumberFormat
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

        uidTextView   = findViewById(R.id.uid_textview)
        exitTimeText  = findViewById(R.id.exit_time_text)
        feeText       = findViewById(R.id.fee_text)
        balanceText   = findViewById(R.id.balance_text)
        backButton    = findViewById(R.id.back_button)

        val uid       = intent.getStringExtra("uid") ?: "-"
        val entryTime = intent.getLongExtra("entryTime", 0L)
        val exitTime  = intent.getLongExtra("exitTime", 0L)
        val cost      = intent.getIntExtra("cost", 0)

        // saldo update
        var balance = DummySaldoManager.getOrCreateBalance(uid)
        if (balance >= cost) {
            DummySaldoManager.updateBalance(uid, cost.toLong())
            balance = DummySaldoManager.getBalance(uid)
        }

        // Format waktu
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))
        val entryStr = if (entryTime > 0) formatter.format(Date(entryTime)) else "-"
        val exitStr  = if (exitTime > 0) formatter.format(Date(exitTime)) else "-"

        // tampil di UI
        uidTextView.text  = "UID: $uid"
        exitTimeText.text = "Masuk: $entryStr\nKeluar: $exitStr"
        feeText.text      = "Biaya Parkir: Rp ${NumberFormat.getNumberInstance(Locale("in","ID")).format(cost)}"
        balanceText.text  = "Sisa Saldo: Rp ${NumberFormat.getNumberInstance(Locale("in","ID")).format(balance)}"

        // === Cetak struk keluar parkir ===
        PrintHelper.printParkingReceipt(
            context   = this,
            uid       = uid,
            entryTime = entryStr,
            exitTime  = exitStr,
            cost      = cost,
            balance   = balance
        )

        backButton.setOnClickListener { finish() }
    }
}
