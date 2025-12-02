package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import kotlinx.coroutines.launch

class AgregarProducto(private val repository: FirestoreInventoryRepository) : ViewModel() {

    fun guardarNuevoProducto(
        codigo: Int,
        nombre: String,
        precio: Double,
        cantidad: Int,
        onResult: (Boolean) -> Unit
    ) {

        val producto = Producto(
            codigo = codigo,
            nombre = nombre,
            precio = precio,
            cantidad = cantidad
        )

        repository.insert(producto) { success ->
            onResult(success)          // Notificamos al Fragment
        }

    }
}