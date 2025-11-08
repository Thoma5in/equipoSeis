package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inventoryapp.repository.InventoryRepository


class InventoryFactory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarProducto::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarProducto(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}