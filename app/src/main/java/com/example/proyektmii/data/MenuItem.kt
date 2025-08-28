package com.example.proyektmii.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(
    val name: String,
    val price: Int,
    val imageRes: Int? = null
) : Parcelable