package com.example.inventoryapp.repository

import androidx.lifecycle.LiveData // Necesitas esta importación
import com.example.inventoryapp.data.ProductoDao
import com.example.inventoryapp.model.Producto

class InventoryRepository(private val productoDao: ProductoDao) {

    val allProducts: LiveData<List<Producto>> = productoDao.getAllProducts()

    suspend fun insert(producto: Producto) {
        productoDao.insert(producto)
    }
}