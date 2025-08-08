package com.example.proyektmii.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.proyektmii.ui.theme.ColorBackground
import com.example.proyektmii.ui.theme.ColorPrimary

@Composable
fun ParkingCheckinScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProyekTMIITheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ColorBackground, // Putih di atas
                            ColorPrimary     // Merah di bawah
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
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
                // Header dengan back button
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
                        text = "Parkir",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Spacer untuk balance
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Spacer untuk memberikan ruang
                Spacer(modifier = Modifier.weight(0.8f))

                // Konten tengah - Logo mobil dengan circle background
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circle background dengan logo mobil
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                Color.White.copy(alpha = 0.9f),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.Gray.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Icon mobil dengan signal - gunakan image mobil.jpg
                            Image(
                                painter = painterResource(id = R.drawable.mobil), // Sesuaikan dengan nama file mobil.jpg
                                contentDescription = "Mobil",
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Parkir",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Center
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
fun ParkingCheckinScreenPreview() {
    ProyekTMIITheme {
        ParkingCheckinScreen(onBack = {})
    }
}