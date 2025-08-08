package com.example.proyektmii.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.example.proyektmii.R
import com.example.proyektmii.ui.theme.ProyekTMIITheme
import com.example.proyektmii.ui.theme.ColorPrimary
import com.example.proyektmii.ui.theme.ColorBackground
import com.example.proyektmii.ui.theme.ColorWarning
import com.example.proyektmii.ui.theme.ColorPositive
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ParkingCheckoutScreen(
    entryTime: Long,
    totalPrice: Int,
    nfcTapped: Boolean,
    onNfcProcessed: () -> Unit,
    onPaymentSuccess: (Int) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isProcessing by remember { mutableStateOf(false) }

    // Format waktu
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = System.currentTimeMillis()
    val entryTimeFormatted = dateFormat.format(Date(entryTime))
    val exitTimeFormatted = dateFormat.format(Date(currentTime))

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

    ProyekTMIITheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ColorPositive, // Kuning di atas
                            ColorBackground // Putih di bawah
                        )
                    )
                )
        ) {
            // Background Pattern Awan - hanya di atas
            Image(
                painter = painterResource(id = R.drawable.awan),
                contentDescription = "Background Pattern",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth
            )

            // Background Ombak di bawah
            Image(
                painter = painterResource(id = R.drawable.ombak),
                contentDescription = "Background Ombak",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth
            )

            // Konten utama
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ColorBackground, shape = RoundedCornerShape(20.dp))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }

                    // Title
                    Text(
                        text = "Pembayaran Parkir",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Logo TMII
                    Image(
                        painter = painterResource(id = R.drawable.tmii_logo),
                        contentDescription = "TMII Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Informasi waktu parkir
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorBackground, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rincian Parkir",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu Masuk:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = entryTimeFormatted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu Keluar:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = exitTimeFormatted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total: Rp $totalPrice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Konten tengah - Card Icon atau Processing
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isProcessing) {
                        // Show processing state
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp),
                            color = ColorPrimary,
                            strokeWidth = 6.dp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Memproses\nPembayaran...",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    } else {
                        // Card image
                        Image(
                            painter = painterResource(id = R.drawable.kartu),
                            contentDescription = "Kartu",
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        // Text instruction
                        Text(
                            text = "Silahkan\nTap Kartu",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        // NFC Tap area
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .border(
                                    3.dp,
                                    ColorWarning,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    ColorBackground,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.nfc),
                                contentDescription = "Tap NFC",
                                modifier = Modifier.size(120.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingCheckoutScreenPreview() {
    ProyekTMIITheme {
        ParkingCheckoutScreen(
            entryTime = System.currentTimeMillis() - 3600000, // 1 jam yang lalu
            totalPrice = 10000,
            nfcTapped = false,
            onNfcProcessed = {},
            onPaymentSuccess = {},
            onBack = {}
        )
    }
}