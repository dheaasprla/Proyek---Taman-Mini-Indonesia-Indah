// destinationselectionscreen.kt
package com.example.proyektmii.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyektmii.R
import com.example.proyektmii.ui.theme.ColorBackground
import com.example.proyektmii.ui.theme.ColorPrimary

@Composable
fun DestinationSelectionScreen(
    isWahana: Boolean,
    onProceedToCart: (TicketItem) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val destinations = if (isWahana) {
        listOf(
            Destination("Kereta Gantung", 50000, R.drawable.keretagantung),
            Destination("Jagat Satwa Nusantara", 60000, R.drawable.jagat),
            Destination("Teater Keong Emas", 50000, R.drawable.keong),
            Destination("Skyworld Indonesia", 90000, R.drawable.sky),
            Destination("Desa Seni Ganara Art", 25000, R.drawable.ganara)
        )
    } else {
        listOf(
            Destination("Contemporary Art Gallery", 25000, R.drawable.cag),
            Destination("Bayt Al-Qur'an & Museum Istiqlal", 10000, R.drawable.istiqlal),
            Destination("Museum Prangko", 5000, R.drawable.prangko),
            Destination("Museum Keprajuritan", 5000, R.drawable.kepra),
            Destination("Museum Transportasi", 10000, R.drawable.transportasi),
            Destination("Museum Listrik & Energi Baru", 20000, R.drawable.le),
            Destination("Indonesia Science Center - PPIPTEK", 27500, R.drawable.science)
        )
    }

    var selectedTicket by remember { mutableStateOf<TicketItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColorBackground,
                        ColorPrimary
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
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
                        .clickable { onBack?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }

                // Title
                Text(
                    text = if (isWahana) "Wahana & Rekreasi" else "Museum",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer for balance
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Destinations Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(destinations) { destination ->
                    val isSelected = selectedTicket?.destination == destination
                    val currentQuantity = if (isSelected) selectedTicket?.quantity ?: 1 else 0

                    DestinationItemCard(
                        destination = destination,
                        quantity = currentQuantity,
                        onQuantityIncrease = {
                            selectedTicket = if (isSelected) {
                                selectedTicket?.copy(quantity = currentQuantity + 1)
                            } else {
                                TicketItem(destination, 1)
                            }
                        },
                        onQuantityDecrease = {
                            if (isSelected && currentQuantity > 1) {
                                selectedTicket = selectedTicket?.copy(quantity = currentQuantity - 1)
                            } else if (isSelected && currentQuantity == 1) {
                                selectedTicket = null
                            }
                        },
                        isSelected = isSelected
                    )
                }
            }

            // Proceed to cart button
            Button(
                onClick = {
                    selectedTicket?.let { onProceedToCart(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(48.dp),
                enabled = selectedTicket != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Lanjut ke Keranjang",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DestinationItemCard(
    destination: Destination,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                if (!isSelected) {
                    onQuantityIncrease()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF5F5F5) else ColorBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            Image(
                painter = painterResource(id = destination.imageRes ?: R.drawable.museum),
                contentDescription = destination.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item name
            Text(
                text = destination.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(0xFF1976D2) else Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Price
            Text(
                text = "Tiket: Rp ${String.format("%,d", destination.price)}",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color(0xFF1976D2),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "−",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onQuantityDecrease() }
                            .padding(4.dp)
                    )

                    Text(
                        text = quantity.toString(),
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    Text(
                        text = "+",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onQuantityIncrease() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}