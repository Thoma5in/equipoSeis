package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.launch

class AgregarProducto(private val repository: InventoryRepository) : ViewModel() {

    fun guardarNuevoProducto(codigo: Int, nombre: String, precio: Double, cantidad: Int) {
        viewModelScope.launch {
            val producto = Producto(
                codigo = codigo,
                nombre = nombre,
                precio = precio,
                cantidad = cantidad
            )
            repository.insert(producto)
            // Aquí podrías añadir un LiveData para notificar a la Activity
            // que la operación ha terminado con éxito.
        }
    }
}