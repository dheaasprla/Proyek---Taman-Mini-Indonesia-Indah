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

@Composable
fun DestinationSelectionScreen(
    isWahana: Boolean,
    onProceedToPayment: (TicketItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val destinations = if (isWahana) {
        listOf(
            Destination("Bianglala", 50000),
            Destination("Kora-Kora", 30000),
            Destination("Kereta Gantung", 40000)
        )
    } else {
        listOf(
            Destination("Museum Indonesia", 25000),
            Destination("Museum Purna Bhakti", 20000),
            Destination("Museum Transportasi", 30000)
        )
    }
    var selectedItem by remember { mutableStateOf<TicketItem?>(null) }
    var quantity by remember { mutableStateOf(1) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Pilih Destinasi", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(destinations) { destination ->
                val isSelected = selectedItem?.destination == destination
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (isSelected) {
                                selectedItem = null
                            } else {
                                selectedItem = TicketItem(destination, quantity)
                            }
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(
                            text = "${destination.name} - Rp ${destination.price}",
                            fontSize = 16.sp,
                            color = if (isSelected) Color.Green else Color.Black
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_minus),
                                contentDescription = "Decrease Quantity",
                                modifier = Modifier.clickable {
                                    quantity = (quantity - 1).coerceAtLeast(1)
                                    selectedItem = selectedItem?.copy(quantity = quantity)
                                }
                            )
                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                fontSize = 16.sp
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_plus),
                                contentDescription = "Increase Quantity",
                                modifier = Modifier.clickable {
                                    quantity = quantity + 1
                                    selectedItem = selectedItem?.copy(quantity = quantity)
                                }
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = { selectedItem?.let { onProceedToPayment(it) } },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = selectedItem != null
        ) {
            Text("Lanjut ke Pembayaran")
        }
    }
}