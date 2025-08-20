package com.example.proyektmii.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun InsufficientBalanceDialog(
    message: String,
    currentBalance: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon atau gambar error
                Text(
                    text = "⚠️",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Title
                Text(
                    text = "Saldo Tidak Cukup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53E3E),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Message
                Text(
                    text = message,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Current balance info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Saldo Saat Ini: Rp ${String.format("%,d", currentBalance)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182CE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}