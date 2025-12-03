package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgregarProductoViewModel @Inject constructor(private val repository: FirestoreInventoryRepository) : ViewModel() {

    fun guardarNuevoProducto(
        codigo: Int,
        nombre: String,
        precio: Double,
        cantidad: Int,
        onResult: (Int) -> Unit
    ) {

        val producto = Producto(
            codigo = codigo,
            nombre = nombre,
            precio = precio,
            cantidad = cantidad
        )

        viewModelScope.launch {
            when (val result = repository.insertIfNotExists(producto)) {

                is FirestoreInventoryRepository.InsertResult.Success -> {
                    onResult(1)
                }

                is FirestoreInventoryRepository.InsertResult.AlreadyExists -> {
                    onResult(0)
                }

                is FirestoreInventoryRepository.InsertResult.Error -> {
                    onResult(-1)
                }
            }
        }

    }
}