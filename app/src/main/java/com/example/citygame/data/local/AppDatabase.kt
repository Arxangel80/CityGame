package com.example.citygame.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.citygame.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}