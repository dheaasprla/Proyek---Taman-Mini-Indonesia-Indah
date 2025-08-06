package com.example.proyektmii.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: UserCard)

    @Query("SELECT * FROM user_card WHERE cardId = :cardId LIMIT 1")
    fun getCardById(cardId: String): Flow<UserCard?>

    @Query("SELECT * FROM user_card")
    fun getAllCards(): Flow<List<UserCard>>
}
