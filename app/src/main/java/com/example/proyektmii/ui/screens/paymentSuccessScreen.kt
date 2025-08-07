package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentSuccessScreen(
    totalPrice: Int,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pembayaran Berhasil", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total yang dibayarkan: Rp $totalPrice", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackToHome) {
            Text("Kembali ke Beranda")
        }
    }
}