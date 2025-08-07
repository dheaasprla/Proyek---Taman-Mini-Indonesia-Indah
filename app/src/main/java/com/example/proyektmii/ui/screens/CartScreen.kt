package com.example.proyektmii.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onProceedToPayment: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Keranjang Belanja", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
        if (cartItems.isEmpty()) {
            Text("Keranjang kosong", fontSize = 16.sp)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(text = "${item.menuItem.name} - Rp ${item.menuItem.price} x ${item.quantity}", fontSize = 16.sp)
                    }
                }
            }
            Text("Total: Rp $totalPrice", fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
            Button(
                onClick = {
                    println("Proceeding to payment with total: $totalPrice")
                    onProceedToPayment(totalPrice)
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Lanjut ke Pembayaran")
            }
        }
    }
}