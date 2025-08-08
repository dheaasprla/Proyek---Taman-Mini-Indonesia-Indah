package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TicketPaymentScreen(
    ticketItem: TicketItem,
    onProceedToNfc: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPrice = ticketItem.destination.price * ticketItem.quantity

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pembayaran Tiket", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("${ticketItem.destination.name} x ${ticketItem.quantity}", fontSize = 18.sp)
        Text("Total: Rp $totalPrice", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        androidx.compose.material3.Button(onClick = onProceedToNfc) {
            Text("Lanjut ke Tempelkan Kartu")
        }
    }
}