package com.example.proyektmii.ui.screens

data class Destination(
    val name: String,
    val price: Int
)

data class TicketItem(
    val destination: Destination,
    var quantity: Int = 1
)