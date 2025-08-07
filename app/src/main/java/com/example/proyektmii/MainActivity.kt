package com.example.proyektmii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.proyektmii.ui.screens.HomeScreen
import com.example.proyektmii.ui.screens.OnboardingScreen
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyekTMIITheme {
                var showOnboarding by remember { mutableStateOf(true) }
                if (showOnboarding) {
                    OnboardingScreen {
                        showOnboarding = false
                    }
                } else {
                    HomeScreen()
                }
            }
        }
    }
}