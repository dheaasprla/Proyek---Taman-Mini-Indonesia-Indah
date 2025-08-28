package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R

class PintuMasukActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pintu_masuk)

        val btnBack = findViewById<ImageButton>(R.id.btnBackPintu)
        val btnSimulate = findViewById<Button>(R.id.btnSimulateTap)

        btnBack.setOnClickListener { finish() }

        // Tombol simulasi (untuk dev) — nanti ganti dengan pemrosesan CloudPOS NFC
        btnSimulate.setOnClickListener {
            simulateNfcTap()
        }

        // TODO: Inisialisasi CloudPOS NFC reader di sini
        //  - register SDK listener
        //  - ketika tag terdeteksi -> verifikasi user/card -> buka gate / simpan log -> tampilkan sukses screen
    }

    private fun simulateNfcTap() {
        // simulasi berhasil: buka success screen
        val i = Intent(this, PintuMasukSuccessActivity::class.java)
        startActivity(i)
        // jika ingin menutup PintuMasukActivity sehingga user tidak bisa kembali ke layar tap:
        finish()
    }
}