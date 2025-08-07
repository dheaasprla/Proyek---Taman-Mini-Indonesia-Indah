package com.example.proyektmii.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyektmii.R

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1
)

@Composable
fun CanteenMenuScreen(
    onProceedToCart: (List<CartItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItem("Nasi Goreng", 20000),
        MenuItem("Mie Ayam", 15000),
        MenuItem("Es Teh", 5000),
        MenuItem("Es Jeruk", 7000)
    )
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Menu Kantin", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(menuItems) { item ->
                var quantity by remember { mutableStateOf(1) }
                val existingCartItem = cartItems.find { it.menuItem == item }
                if (existingCartItem != null) {
                    quantity = existingCartItem.quantity // Sinkronisasi kuantitas dari cartItems
                }

                MenuItemCard(
                    item = item,
                    quantity = quantity,
                    onQuantityIncrease = { newQuantity ->
                        quantity = newQuantity
                        cartItems = if (existingCartItem != null) {
                            cartItems.map { if (it.menuItem == item) it.copy(quantity = quantity) else it }
                        } else {
                            cartItems + CartItem(item, quantity)
                        }
                    },
                    onQuantityDecrease = { newQuantity ->
                        quantity = newQuantity
                        if (quantity > 0) {
                            cartItems = if (existingCartItem != null) {
                                cartItems.map { if (it.menuItem == item) it.copy(quantity = quantity) else it }
                            } else {
                                cartItems + CartItem(item, quantity)
                            }
                        } else {
                            cartItems = cartItems.filter { it.menuItem != item }
                        }
                    },
                    onItemSelected = { selected ->
                        if (!selected && existingCartItem != null) {
                            cartItems = cartItems.filter { it.menuItem != item }
                        } else if (selected && existingCartItem == null) {
                            cartItems = cartItems + CartItem(item, quantity)
                        }
                    },
                    isSelected = existingCartItem != null
                )
            }
        }
        Button(
            onClick = { onProceedToCart(cartItems) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = cartItems.isNotEmpty()
        ) {
            Text("Lanjut ke Keranjang")
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    quantity: Int,
    onQuantityIncrease: (Int) -> Unit,
    onQuantityDecrease: (Int) -> Unit,
    onItemSelected: (Boolean) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemSelected(!isSelected) }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = "${item.name} - Rp ${item.price}",
                fontSize = 16.sp,
                color = if (isSelected) Color.Green else Color.Black
            )
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = "Decrease Quantity",
                    modifier = Modifier.clickable { onQuantityDecrease(quantity - 1) }
                )
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 16.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Increase Quantity",
                    modifier = Modifier.clickable { onQuantityIncrease(quantity + 1) }
                )
            }
        }
    }
}