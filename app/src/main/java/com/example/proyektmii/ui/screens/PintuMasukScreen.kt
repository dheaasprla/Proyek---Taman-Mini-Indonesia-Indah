package com.example.proyektmii.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

@Composable
fun PintuMasukScreen(
    onBack: () -> Unit
) {
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
                    .height(300.dp) // Membatasi tinggi agar hanya di bagian atas
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth
            )

            // Background Ombak di bawah - lurus tidak miring
            Image(
                painter = painterResource(id = R.drawable.ombak),
                contentDescription = "Background Ombak",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth // Menggunakan FillWidth agar tidak miring
            )

            // Konten utama
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header dengan spacing yang lebih baik
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button dengan background putih bulat
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ColorBackground, shape = RoundedCornerShape(20.dp))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }

                    // Title di tengah
                    Text(
                        text = "Pintu Masuk",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Logo TMII
                    Image(
                        painter = painterResource(id = R.drawable.tmii_logo),
                        contentDescription = "TMII Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Spacer untuk memberikan ruang
                Spacer(modifier = Modifier.height(60.dp))

                // Konten tengah - Card Icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card image dengan ukuran yang lebih proporsional
                    Image(
                        painter = painterResource(id = R.drawable.kartu),
                        contentDescription = "Kartu",
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Text instruction
                    Text(
                        text = "Silahkan\nTap Kartu",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // NFC Tap area dengan styling yang lebih baik
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(
                                3.dp,
                                ColorWarning, // Menggunakan ColorWarning dari theme
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                ColorBackground, // Menggunakan ColorBackground dari theme
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nfc),
                            contentDescription = "Tap NFC",
                            modifier = Modifier.size(140.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Spacer untuk mendorong ombak ke bawah
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PintuMasukScreenPreview() {
    ProyekTMIITheme {
        PintuMasukScreen(onBack = {})
    }
}