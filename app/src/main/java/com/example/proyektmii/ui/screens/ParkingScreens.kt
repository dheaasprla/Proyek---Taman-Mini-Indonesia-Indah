package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun ParkingScreen(
    nfcTapped: Boolean,
    parkingEntryTime: Long?,
    isCheckedIn: Boolean,
    onNfcProcessed: () -> Unit,
    onCheckIn: (Long) -> Unit,
    onCheckOut: (Int) -> Unit
) {
    var message by remember { mutableStateOf(if (isCheckedIn) "Silahkan tap untuk check-out" else "Tap kartu untuk check-in") }

    LaunchedEffect(nfcTapped) {
        if (nfcTapped) {
            if (!isCheckedIn) {
                val currentTime = System.currentTimeMillis()
                onCheckIn(currentTime)
                message = "Check-in berhasil. Silahkan tap lagi untuk check-out saat keluar."
            } else {
                parkingEntryTime?.let { entryTime ->
                    val durationHours = ((System.currentTimeMillis() - entryTime) / 3600000).toInt() + 1
                    val cost = durationHours * 5000
                    onCheckOut(cost)
                    message = "Check-out berhasil. Silahkan tap untuk bayar Rp.$cost"
                }
            }
            onNfcProcessed()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}