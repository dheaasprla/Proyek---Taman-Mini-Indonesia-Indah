package com.example.proyektmii.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.proyektmii.R
import com.example.proyektmii.ui.theme.ProyekTMIITheme
import com.example.proyektmii.ui.theme.ColorPrimary
import com.example.proyektmii.ui.theme.ColorBackground

@Composable
fun PaymentSuccessScreen(
    totalPrice: Int,
    onBackToHome: () -> Unit,
    successMessage: String = "Pembayaran Berhasil", // Parameter opsional untuk pesan kustom
    modifier: Modifier = Modifier
) {
    ProyekTMIITheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPrimary)
        ) {
            // Background Awan Kuning
            Image(
                painter = painterResource(id = R.drawable.awan),
                contentDescription = "Background Pattern Top",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            // Background Awan di bawah
            Image(
                painter = painterResource(id = R.drawable.awan),
                contentDescription = "Background Pattern Bottom",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp),
                contentScale = ContentScale.Crop
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Header dengan tombol kembali
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ColorBackground, shape = RoundedCornerShape(20.dp))
                            .clickable { onBackToHome() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Konten tengah: Ikon dan Teks
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ikon sukses
                    Image(
                        painter = painterResource(id = R.drawable.berhasil), // Centang
                        contentDescription = "Sukses",
                        modifier = Modifier.size(200.dp), // Ukuran diperbesar
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "SUKSES!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Selamat Menikmati",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Tampilkan total yang dibayarkan
                    Text(
                        text = "Total: Rp $totalPrice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentSuccessScreenPreview() {
    ProyekTMIITheme {
        PaymentSuccessScreen(
            totalPrice = 50000,
            onBackToHome = {}
        )
    }
}