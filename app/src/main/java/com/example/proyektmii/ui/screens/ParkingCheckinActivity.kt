package com.example.proyektmii.ui.screens

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.MainActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.local.AppPreferences

class ParkingCheckinActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_checkin)

        appPreferences = AppPreferences(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        findViewById<ImageButton>(R.id.back_button_parking_checkin).setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val tagIdBytes = tag?.id
            // Gunakan fungsi toHexString() manual yang dibuat di bawah
            val cardId = tagIdBytes?.toHexString() ?: "ID_unknown"

            val parkingEntryTime = appPreferences.getParkingEntryTime()
            if (parkingEntryTime == 0L) {
                // Check-in parkir
                appPreferences.saveParkingEntryTime(System.currentTimeMillis())
                Toast.makeText(this, "Check-in parkir berhasil!", Toast.LENGTH_SHORT).show()

                // Navigasi ke layar sukses check-in
                val successIntent = Intent(this, ParkingCheckinSuccessActivity::class.java)
                startActivity(successIntent)
                finish()
            } else {
                Toast.makeText(this, "Kartu ini sudah terdaftar di area parkir.", Toast.LENGTH_SHORT).show()
                // Pindah ke layar checkout jika kartu sudah terdaftar
                val checkoutIntent = Intent(this, ParkingCheckoutActivity::class.java)
                startActivity(checkoutIntent)
                finish()
            }
        }
    }

    // Tambahkan fungsi toHexString() manual di sini
    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}