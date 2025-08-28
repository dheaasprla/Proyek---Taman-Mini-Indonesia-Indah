package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import java.text.SimpleDateFormat
import java.util.*

class ParkingCheckoutActivity : AppCompatActivity() {

    private var entryTime: Long = 0L
    private var totalPrice: Int = 0

    private lateinit var tvEntry: TextView
    private lateinit var tvExit: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnSimulateNfc: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkout)

        tvEntry = findViewById(R.id.tvEntryTime)
        tvExit = findViewById(R.id.tvExitTime)
        tvTotal = findViewById(R.id.tvTotalPrice)
        btnSimulateNfc = findViewById(R.id.btnSimulateNfcCheckout)
        btnBack = findViewById(R.id.btnBackCheckout)

        entryTime = intent.getLongExtra("entryTime", System.currentTimeMillis())
        // Simple calculation: misal tarif per jam 5000
        val hours = calculateHours(entryTime, System.currentTimeMillis())
        totalPrice = (hours * 5000).coerceAtLeast(5000) // minimal 5000
        bindTimes()

        btnBack.setOnClickListener { finish() }

        btnSimulateNfc.setOnClickListener {
            // tampilkan processing (sederhana: disable tombol dan gunakan Handler)
            btnSimulateNfc.isEnabled = false
            btnSimulateNfc.text = getString(R.string.processing)
            Handler(Looper.getMainLooper()).postDelayed({
                // pembayaran sukses -> buka success activity
                val i = Intent(this, ParkingCheckoutSuccessActivity::class.java)
                i.putExtra("totalPrice", totalPrice)
                startActivity(i)
                finish()
            }, 1000) // delay 1s, sesuai UX
        }

        // TODO: gantikan simulate dengan pemrosesan NFC lewat CloudPOS SDK
    }

    private fun bindTimes() {
        val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        tvEntry.text = fmt.format(Date(entryTime))
        tvExit.text = fmt.format(Date(System.currentTimeMillis()))
        tvTotal.text = "Total: Rp ${formatCurrency(totalPrice)}"
    }

    private fun calculateHours(start: Long, end: Long): Int {
        val diff = end - start
        val hours = (diff / (1000 * 60 * 60)).toInt()
        return if (hours == 0) 1 else hours
    }

    private fun formatCurrency(v: Int): String =
        java.text.NumberFormat.getInstance(java.util.Locale("in", "ID")).format(v)
}