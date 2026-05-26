package com.example.examen1final_marcosmerino.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val rol: String,
    val password: String,
    val fotoUri: String,
    val latitud: Double? = null,
    val longitud: Double? = null
)
