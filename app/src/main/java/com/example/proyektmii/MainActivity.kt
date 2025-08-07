package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.proyektmii.ui.screens.HomeScreen
import com.example.proyektmii.ui.screens.OnboardingScreen
import com.example.proyektmii.ui.screens.ParkingScreen
import com.example.proyektmii.ui.screens.ThankYouScreen
import com.example.proyektmii.ui.theme.ProyekTMIITheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var showParkingScreen by mutableStateOf(false)
    private var showThankYouScreen by mutableStateOf(false)
    private var parkingCost by mutableStateOf(0)
    private var nfcTapped by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Perangkat tidak mendukung NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "Silakan aktifkan NFC", Toast.LENGTH_SHORT).show()
        }

        setContent {
            ProyekTMIITheme {
                var showOnboarding by remember { mutableStateOf(true) }

                if (showThankYouScreen) {
                    ThankYouScreen(
                        cost = parkingCost,
                        onBackToHome = {
                            showThankYouScreen = false
                            parkingCost = 0 // Reset biaya setelah kembali
                        }
                    )
                } else if (showParkingScreen) {
                    ParkingScreen(
                        nfcTapped = nfcTapped,
                        onNfcProcessed = { nfcTapped = false },
                        onCardProcessed = { cost ->
                            parkingCost = cost
                            showParkingScreen = false
                            showThankYouScreen = true
                        }
                    )
                } else if (showOnboarding) {
                    OnboardingScreen {
                        showOnboarding = false
                    }
                } else {
                    HomeScreen(
                        onNavigateToParking = { showParkingScreen = true }
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
            if (NfcAdapter.ACTION_TAG_DISCOVERED == it.action && showParkingScreen) {
                Toast.makeText(this, "Kartu terdeteksi, memproses...", Toast.LENGTH_SHORT).show()
                nfcTapped = true // Memicu pemrosesan di ParkingScreen
            }
        }
    }
}