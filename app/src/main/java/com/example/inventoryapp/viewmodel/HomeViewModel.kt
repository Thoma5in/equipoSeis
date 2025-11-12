package com.example.inventoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository

    // variable para LiveData para el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    val allProducts: LiveData<List<Producto>>

    init {
        val productDao = AppDatabase.getDatabase(application).productoDao()
        repository = InventoryRepository(productDao)
        allProducts = repository.allProducts

        // estado de carga inicial
        _isLoading.value = true

        // Ejecución asíncrona: Mueve la observación dentro del launch después del delay
        viewModelScope.launch {

            delay(2000) // Simulación: Espera 3 segundos

            // Observador para actualizar el estado de la lista después de que el delay termine
            allProducts.observeForever(object : androidx.lifecycle.Observer<List<Producto>> {
                override fun onChanged(productos: List<Producto>) {
                    // Establece false y removemos el Observer después del primer valor recibido
                    _isLoading.postValue(false)
                    allProducts.removeObserver(this)
                }
            })
        }
    }

    //Metodo para eliminar producto por ID
    suspend fun deleteProductById(productId: Long) {
        withContext(Dispatchers.IO){
            repository.deleteProductById(productId)
        }
    }
}