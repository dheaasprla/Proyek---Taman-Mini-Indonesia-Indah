package com.example.proyektmii.data

// Data class dan fungsi lainnya
data class Destination(
    val name: String,
    val price: Int,
    val imageRes: Int? = null
)

data class TicketItem(
    val destination: Destination,
    var quantity: Int = 1
)