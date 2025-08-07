package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    totalPrice: Int,
    nfcTapped: Boolean,
    onNfcProcessed: () -> Unit,
    onPaymentSuccess: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isProcessing by remember { mutableStateOf(false) }

    // Mulai pemrosesan saat NFC di-tap
    LaunchedEffect(nfcTapped) {
        if (nfcTapped && !isProcessing) {
            isProcessing = true
            delay(2000) // Simulasi pemrosesan 2 detik
            onPaymentSuccess(totalPrice)
            onNfcProcessed()
            isProcessing = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Memproses pembayaran...", fontSize = 18.sp)
        } else {
            Text("Silakan tempelkan kartu Anda untuk membayar Rp $totalPrice", fontSize = 20.sp)
        }
    }
}