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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.example.proyektmii.R
import com.example.proyektmii.data.CardData
import com.example.proyektmii.ui.theme.ColorPrimary
import com.example.proyektmii.ui.theme.ColorBackground
import com.example.proyektmii.ui.theme.ColorWarning
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckBalanceScreen(
    onBack: () -> Unit,
    cardData: CardData?,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    // Yellow gradient background matching the Figma design
    val yellowGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFC107),
            Color(0xFFFFFFFF)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(yellowGradient)
    ) {
        // Background Awan Kuning di atas
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
            contentDescription = "Background Awan Bottom",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, shape = RoundedCornerShape(20.dp))
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

                Text(
                    text = "Cek Saldo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Instructions text
            Text(
                text = "Silahkan Tap Kartu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                style = androidx.compose.ui.text.TextStyle(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // NFC tap area with red border
            Box(
                modifier = Modifier
                    .size(280.dp, 180.dp)
                    .border(
                        width = 3.dp,
                        color = ColorPrimary, // Red border
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = ColorPrimary,
                        strokeWidth = 4.dp
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.nfc),
                        contentDescription = "Tap NFC",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Card information display area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.95f),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = ColorPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    // Card ID Section
                    Text(
                        text = "ID Kartu",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                Color(0xFFFFF8E1), // Light yellow background
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = ColorPrimary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = cardData?.id ?: "xxxxxxxxxxxxxx",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Balance Section
                    Text(
                        text = "Saldo",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                Color(0xFFFFF8E1), // Light yellow background
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = ColorPrimary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (cardData != null) {
                                "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(cardData.balance)}"
                            } else {
                                "Rp ..."
                            },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}