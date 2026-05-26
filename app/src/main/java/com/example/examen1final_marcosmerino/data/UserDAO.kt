package com.example.examen1final_marcosmerino.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.examen1final_marcosmerino.model.User

@Dao
interface UserDAO {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsers(): List<User>
}