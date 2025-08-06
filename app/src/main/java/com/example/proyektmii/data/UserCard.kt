package com.example.proyektmii.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_card")
data class UserCard(
    @PrimaryKey val cardId: String,
    val name: String? = null
)
