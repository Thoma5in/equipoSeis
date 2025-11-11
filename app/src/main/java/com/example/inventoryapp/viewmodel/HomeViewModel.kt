package com.example.inventoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    val allProducts: LiveData<List<Producto>>

    init {
        val productDao = AppDatabase.getDatabase(application).productoDao()
        repository = InventoryRepository(productDao)
        allProducts = repository.allProducts
    }

    // 🔹 Método para eliminar producto por ID
    fun deleteProductById(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProductById(productId)
        }
    }
}
