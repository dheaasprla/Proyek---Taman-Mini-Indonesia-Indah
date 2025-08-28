package com.example.proyektmii.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardData(
    val id: String,
    val balance: Int
) : Parcelable