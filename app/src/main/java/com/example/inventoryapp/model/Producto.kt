package com.example.inventoryapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val codigo: Int = 0,
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0
)