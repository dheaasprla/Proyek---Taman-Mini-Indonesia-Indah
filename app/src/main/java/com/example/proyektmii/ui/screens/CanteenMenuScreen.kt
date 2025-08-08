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

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1
)

@Composable
fun CanteenMenuScreen(
    onProceedToCart: (List<CartItem>) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItem("Nasi Goreng", 20000),
        MenuItem("Mie Ayam", 15000),
        MenuItem("Es Jeruk", 8000),
        MenuItem("Teh manis", 5000)
    )
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }

    // Function to get image resource based on menu item name
    fun getImageResource(menuName: String): Int {
        return when (menuName) {
            "Nasi Goreng" -> R.drawable.nasgor
            "Mie Ayam" -> R.drawable.mieayam
            "Es Jeruk" -> R.drawable.esjeruk
            "Teh manis" -> R.drawable.esteh
            else -> R.drawable.nasgor // default fallback
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColorBackground, // Putih di atas
                        ColorPrimary    // Merah di bawah
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
                    text = "Kantin",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer for balance (same width as back button)
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Menu Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    val existingCartItem = cartItems.find { it.menuItem == item }
                    val currentQuantity = existingCartItem?.quantity ?: 0

                    MenuItemCard(
                        item = item,
                        imageRes = getImageResource(item.name),
                        quantity = currentQuantity,
                        onQuantityIncrease = {
                            cartItems = if (existingCartItem != null) {
                                cartItems.map {
                                    if (it.menuItem == item) it.copy(quantity = it.quantity + 1)
                                    else it
                                }
                            } else {
                                cartItems + CartItem(item, 1)
                            }
                        },
                        onQuantityDecrease = {
                            if (existingCartItem != null) {
                                if (existingCartItem.quantity > 1) {
                                    cartItems = cartItems.map {
                                        if (it.menuItem == item) it.copy(quantity = it.quantity - 1)
                                        else it
                                    }
                                } else {
                                    cartItems = cartItems.filter { it.menuItem != item }
                                }
                            }
                        },
                        isSelected = existingCartItem != null
                    )
                }
            }

            // Proceed to cart button
            Button(
                onClick = { onProceedToCart(cartItems) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(48.dp),
                enabled = cartItems.isNotEmpty(),
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
fun MenuItemCard(
    item: MenuItem,
    imageRes: Int,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
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
                painter = painterResource(id = imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item name
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(0xFF1976D2) else Color.Black,
                textAlign = TextAlign.Center
            )

            // Price
            Text(
                text = "Harga: Rp ${String.format("%,d", item.price)}",
                fontSize = 12.sp,
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onQuantityDecrease() }
                            .padding(4.dp)
                    )

                    Text(
                        text = quantity.toString(),
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Text(
                        text = "+",
                        fontSize = 16.sp,
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