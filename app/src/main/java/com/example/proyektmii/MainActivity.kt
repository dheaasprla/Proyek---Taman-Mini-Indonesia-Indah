package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.proyektmii.ui.screens.HomeScreen
import com.example.proyektmii.ui.screens.OnboardingScreen
import com.example.proyektmii.ui.screens.PintuMasukScreen
import com.example.proyektmii.ui.screens.PintuMasukSuksesScreen
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var isNfcTapped by mutableStateOf(false)
    private var showPintuMasukScreen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        setContent {
            ProyekTMIITheme {
                var showOnboarding by remember { mutableStateOf(true) }
                var showPintuMasukSuksesScreen by remember { mutableStateOf(false) }

                if (isNfcTapped) {
                    showPintuMasukScreen = false
                    showPintuMasukSuksesScreen = true
                    isNfcTapped = false
                }

                if (showPintuMasukSuksesScreen) {
                    PintuMasukSuksesScreen(
                        onBackToHome = {
                            showPintuMasukSuksesScreen = false
                        }
                    )
                } else if (showPintuMasukScreen) {
                    PintuMasukScreen(
                        onBack = {
                            showPintuMasukScreen = false
                        }
                    )
                } else if (showOnboarding) {
                    OnboardingScreen {
                        showOnboarding = false
                    }
                } else {
                    HomeScreen(
                        onCardClick = { featureType ->
                            when (featureType) {
                                "Pintu Masuk" -> {
                                    showPintuMasukScreen = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action && showPintuMasukScreen) {
                Toast.makeText(this, "Kartu terdeteksi, memproses...", Toast.LENGTH_SHORT).show()
                isNfcTapped = true
            }
        }
    }
}