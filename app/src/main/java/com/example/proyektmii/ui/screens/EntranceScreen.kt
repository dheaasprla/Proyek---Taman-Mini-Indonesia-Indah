package com.example.proyektmii.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EntranceScreen(
    viewModel: (Any) -> Unit = viewModel()
) {
    // ③ collect LiveData scannedId
    val scannedId by viewModel.scannedId.observeAsState(initial = null)

    Crossfade(targetState = scannedId != null) { success ->
        if (!success) {
            // Tampilan menunggu scan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFFFE047), Color.White))
                    )
                    .clickable {
                        // Hanya untuk testing: simulasi scan
                        viewModel.onCardScanned("SIM-123456")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Silakan Tempelkan Kartu Anda",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Tampilan sukses
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFD32F2F)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE047)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✔", fontSize = 48.sp, color = Color(0xFFD32F2F))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "SUKSES!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        "Silahkan Masuk",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
