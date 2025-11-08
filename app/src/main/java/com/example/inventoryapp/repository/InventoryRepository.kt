package com.example.inventoryapp.repository

import com.example.inventoryapp.data.ProductoDao
import com.example.inventoryapp.model.Producto

class InventoryRepository(private val productoDao: ProductoDao) {

    suspend fun insert(producto: Producto) {
        productoDao.insert(producto)
    }
}