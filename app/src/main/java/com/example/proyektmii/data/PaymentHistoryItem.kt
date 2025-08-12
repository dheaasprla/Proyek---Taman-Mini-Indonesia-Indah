package com.example.proyektmii.data

data class PaymentHistoryItem(
    val cardId: String?,
    val userName: String?,
    val transactionType: String, // e.g., "Kantin", "Tiket", "Parkir"
    val items: List<String>, // List of items purchased (e.g., ["Nasi Goreng x2", "Mie Ayam x1"])
    val totalPrice: Int,
    val timestamp: Long
)