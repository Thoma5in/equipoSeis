package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgregarProducto @Inject constructor(private val repository: FirestoreInventoryRepository) : ViewModel() {

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