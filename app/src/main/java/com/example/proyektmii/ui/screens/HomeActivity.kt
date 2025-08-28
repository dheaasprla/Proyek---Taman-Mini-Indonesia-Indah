package com.example.proyektmii.ui.screens

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeActivity", "onCreate dipanggil")
        setContentView(R.layout.activity_home)

        // Ambil data dari Intent (misalnya dari NFC / MainActivity)
        val tagId = intent.getStringExtra("tagId")
        if (tagId != null) {
            Log.d("HomeActivity", "Diterima tagId dari MainActivity: $tagId")
            Toast.makeText(this, "TagID: $tagId", Toast.LENGTH_SHORT).show()
        } else {
            Log.w("HomeActivity", "Tidak ada tagId diterima dari MainActivity")
        }

        val featureItems = listOf(
            FeatureItem(R.drawable.pintumasuk, "Pintu Masuk", "Pintu Masuk"),
            FeatureItem(R.drawable.parkir, "Parkir", "Parkir"),
            FeatureItem(R.drawable.destinasi, "Destinasi", "Destinasi"),
            FeatureItem(R.drawable.kantin, "Kantin", "Kantin"),
            FeatureItem(R.drawable.ceksaldo, "Cek Saldo", "Cek Saldo")
        )
        Log.d("HomeActivity", "FeatureItems disiapkan: ${featureItems.size} item")

        val rv = findViewById<RecyclerView>(R.id.rvFeatures)
        val spanCount = 2
        rv.layoutManager = GridLayoutManager(this, spanCount)
        rv.adapter = FeatureAdapter(featureItems) { type ->
            Log.d("HomeActivity", "User klik fitur: $type")

            when (type) {
                "Destinasi" -> {
                    val i = Intent(this, DestinationMenuActivity::class.java)
                    startActivity(i)
                }
                "Kantin" -> {
                    val i = Intent(this, CanteenActivity::class.java)
                    startActivity(i)
                }
                "Pintu Masuk" -> {
                    val i = Intent(this, PintuMasukActivity::class.java)
                    startActivity(i)
                }
                "Parkir" -> {
                    val i = Intent(this, ParkingCheckinActivity::class.java)
                    startActivity(i)
                }
                "Cek Saldo" -> {
                    val i = Intent(this, CheckBalanceActivity::class.java)
                    startActivity(i)
                }
                else -> {
                    Toast.makeText(this, "Fitur tidak dikenali: $type", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // gradient pada footer text (mirip Brush.horizontalGradient di Compose)
        val footerTitle = findViewById<TextView?>(R.id.footerTitle)
        if (footerTitle == null) {
            Log.e("HomeActivity", "footerTitle TIDAK ditemukan di layout activity_home.xml")
        } else {
            footerTitle.post {
                val width = footerTitle.paint.measureText(footerTitle.text.toString())
                val textShader = LinearGradient(
                    0f, 0f, width, footerTitle.textSize,
                    intArrayOf(android.graphics.Color.RED, android.graphics.Color.BLACK),
                    null,
                    Shader.TileMode.CLAMP
                )
                footerTitle.paint.shader = textShader
                footerTitle.invalidate()
                Log.d("HomeActivity", "Gradient berhasil diterapkan ke footerTitle")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeActivity", "onStart dipanggil")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeActivity", "onResume dipanggil")
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeActivity", "onPause dipanggil")
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeActivity", "onStop dipanggil")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "onDestroy dipanggil")
    }
}