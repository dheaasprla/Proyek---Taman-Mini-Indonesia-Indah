package com.example.proyektmii.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Data class dan fungsi lainnya
@Parcelize
data class Destination(
    val name: String,
    val price: Int,
    val imageRes: Int? = null
) : Parcelable

@Parcelize
data class TicketItem(
    val destination: Destination,
    var quantity: Int = 1
) : Parcelable