package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FirestoreInventoryRepository
) : ViewModel() {

    // variable para LiveData para el estado de carga
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _allProducts = MutableLiveData<List<Producto>>()
    val allProducts: LiveData<List<Producto>> get() = _allProducts

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        //Firestore listener en tiempo real
        repository.getAllProductos { lista ->
            _allProducts.value = lista
            _isLoading.value = false
        }
    }

    //Metodo para eliminar producto por ID
    suspend fun deleteProductById(productId: Int) {
        repository.delete(productId)
    }
}