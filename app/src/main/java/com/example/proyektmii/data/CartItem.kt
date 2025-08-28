package com.example.proyektmii.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int
) : Parcelable