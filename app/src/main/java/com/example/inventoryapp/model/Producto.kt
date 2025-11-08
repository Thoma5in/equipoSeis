package com.example.inventoryapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val codigo: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int
)