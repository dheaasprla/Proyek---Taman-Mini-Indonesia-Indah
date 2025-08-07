package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ParkingScreen(
    nfcTapped: Boolean,
    onNfcProcessed: () -> Unit,
    onCardProcessed: (Int) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }

    // Mulai pemrosesan saat NFC di-tap
    LaunchedEffect(nfcTapped) {
        if (nfcTapped && !isProcessing) {
            isProcessing = true
            delay(2000) // Simulasi pemrosesan 2 detik
            onCardProcessed(10000) // Biaya hardcoded Rp10.000
            onNfcProcessed() // Reset nfcTapped
            isProcessing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Memproses...", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("Tempelkan kartu Anda", style = MaterialTheme.typography.headlineMedium)
        }
    }
}