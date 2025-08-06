package com.example.proyektmii.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val userCardDao: UserCardDao) {

    // Panggil insertCard dari DAO
    suspend fun insert(card: UserCard) {
        userCardDao.insertCard(card)
    }

    // Teruskan Flow dari DAO
    fun getAllCards(): Flow<List<UserCard>> =
        userCardDao.getAllCards()
}