package com.example.inventoryapp.viewmodel

import com.example.inventoryapp.MainDispatcherRule
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*

@ExtendWith(MainDispatcherRule::class)
class AgregarProductoViewModelTest {

    private val repository: FirestoreInventoryRepository = mock()

    @Test
    fun `guardarNuevoProducto - producto ya existe retorna 0`() = runTest {
        whenever(repository.insertIfNotExists(any())).thenReturn(
            FirestoreInventoryRepository.InsertResult.AlreadyExists(999)
        )

        val vm = AgregarProductoViewModel(repository)

        var result = -99
        vm.guardarNuevoProducto(1, "p1", 1000.0, 2) {
            result = it
        }

        advanceUntilIdle()
        assertEquals(0, result)
    }

    @Test
    fun `guardarNuevoProducto - insercion exitosa retorna 1`() = runTest {
        whenever(repository.insertIfNotExists(any())).thenReturn(
            FirestoreInventoryRepository.InsertResult.Success
        )

        val vm = AgregarProductoViewModel(repository)

        var result = -99
        vm.guardarNuevoProducto(2, "p2", 2000.0, 3) {
            result = it
        }

        advanceUntilIdle()
        assertEquals(1, result)
    }

    @Test
    fun `guardarNuevoProducto - error retorna -1`() = runTest {
        whenever(repository.insertIfNotExists(any())).thenReturn(
            FirestoreInventoryRepository.InsertResult.Error(Exception("fallo"))
        )

        val vm = AgregarProductoViewModel(repository)

        var result = -99
        vm.guardarNuevoProducto(3, "p3", 3000.0, 4) {
            result = it
        }

        advanceUntilIdle()
        assertEquals(-1, result)
    }
}
