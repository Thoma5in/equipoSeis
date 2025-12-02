package com.example.inventoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val repository: FirestoreInventoryRepository
) : ViewModel() {

    fun getProduct(id: Int): LiveData<Producto?> {
        val liveData = MutableLiveData<Producto?>()
        repository.getProductById(id) { product ->
            liveData.value = product
        }
        return liveData
    }

    fun updateProduct(producto: Producto) {
        viewModelScope.launch {
            repository.update(producto)
        }
    }
}