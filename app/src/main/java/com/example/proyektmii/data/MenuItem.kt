package com.example.proyektmii.ui.screens

data class MenuItem(
    val name: String,
    val price: Int,
    val imageRes: Int? = null // Opsional untuk gambar, bisa null jika belum ada
)