package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.MainActivity
import com.example.proyektmii.R

class OnboardingActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val logoTmii: ImageView = findViewById(R.id.logo_tmii)

        // Animasi fade-in
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 2000
        fadeIn.fillAfter = true

        // Animasi scale
        val scaleUp = ScaleAnimation(
            0.5f, 1.5f, 0.5f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleUp.duration = 2000
        scaleUp.fillAfter = true

        logoTmii.startAnimation(fadeIn)
        logoTmii.startAnimation(scaleUp)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }
}