package com.example.inventoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.launch

class EditProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao = AppDatabase.getDatabase(application).productoDao()
    private val repository = InventoryRepository(productoDao)

    fun getProduct(id: Int): LiveData<Producto?> {
        return repository.getProductById(id)
    }

    fun updateProduct(producto: Producto) {
        viewModelScope.launch {
            repository.insert(producto) // REPLACE actúa como update
        }
    }

    //  Fábrica actualizada — usa la nueva firma correcta
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditProductViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}