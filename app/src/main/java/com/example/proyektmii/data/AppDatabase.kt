package com.example.proyektmii.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userCardDao(): UserCardDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tmii_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
