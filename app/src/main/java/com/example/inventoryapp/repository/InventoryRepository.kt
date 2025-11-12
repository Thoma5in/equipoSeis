package com.example.inventoryapp.repository

import androidx.lifecycle.LiveData
import com.example.inventoryapp.data.ProductoDao
import com.example.inventoryapp.model.Producto

class InventoryRepository(private val productoDao: ProductoDao) {

    val allProducts: LiveData<List<Producto>> = productoDao.getAllProducts()

    suspend fun insert(producto: Producto) {
        productoDao.insert(producto)
    }

    // 🔹 Nuevo método para eliminar producto por ID
    suspend fun deleteProductById(id: Long) {
        productoDao.deleteById(id)
    }

    fun getProductById(id: Int): LiveData<Producto?> {
        return productoDao.getProductById(id)
    }
}
