package com.example.citygame.data.local

import androidx.room.*
import com.example.citygame.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
