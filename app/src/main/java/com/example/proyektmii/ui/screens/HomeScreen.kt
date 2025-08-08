package com.example.proyektmii.ui.screens // Pastikan ini 'screens' dengan huruf kecil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable // Impor ini untuk interaksi klik
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyektmii.R
import com.example.proyektmii.ui.theme.ColorBackground
import com.example.proyektmii.ui.theme.ColorPrimary
import com.example.proyektmii.ui.theme.ProyekTMIITheme

data class FeatureItem(
    val iconRes: Int,
    val text: String,
    val type: String // Tambahkan field 'type' untuk identifikasi fitur
)

@Composable
fun HomeScreen(onCardClick: (String) -> Unit) { // Tambahkan parameter onCardClick
    val featureItems = listOf(
        FeatureItem(R.drawable.pintumasuk, "Pintu Masuk", "Pintu Masuk"),
        FeatureItem(R.drawable.parkir, "Parkir", "Parkir"),
        FeatureItem(R.drawable.destinasi, "Destinasi", "Destinasi"),
        FeatureItem(R.drawable.kantin, "Kantin", "Kantin")
    )

    val gradientTextStyle = TextStyle(
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Red, Color.Black)
        )
    )

    ProyekTMIITheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tmii),
                contentDescription = "Header TMII",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(featureItems) { item ->
                    FeatureGridCard(
                        iconRes = item.iconRes,
                        text = item.text,
                        onClick = { onCardClick(item.type) } // Panggil callback saat diklik
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tmii_logo),
                        contentDescription = "Logo TMII Footer",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Taman Mini Indonesia Indah",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = gradientTextStyle
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.Red, shape = RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun FeatureGridCard(iconRes: Int, text: String, onClick: () -> Unit) { // Tambahkan parameter onClick
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(2.dp, Color.Red, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick), // Tambahkan modifier clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$text Icon",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ProyekTMIITheme {
        HomeScreen(onCardClick = {})
    }
}
