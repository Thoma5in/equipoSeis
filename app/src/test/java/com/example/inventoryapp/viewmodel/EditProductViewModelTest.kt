package com.example.inventoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import com.google.firebase.firestore.ListenerRegistration
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class EditProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: FirestoreInventoryRepository = mock()
    private lateinit var viewModel: EditProductViewModel

    private val listenerRegistration: ListenerRegistration = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        viewModel = EditProductViewModel(repository)
    }

    @Test
    fun `getProduct retorna el producto esperado`() = runTest {

        val producto = Producto(1, "Café", 10.00)

        whenever(repository.getProductById(eq(1), any())).thenAnswer {
            val callback = it.arguments[1] as (Producto?) -> Unit
            callback(producto)
            listenerRegistration
        }

        val result = viewModel.getProduct(1).getOrAwaitValue()

        assertEquals(producto, result)
    }

    @Test
    fun `updateProduct llama repository update correctamente`() = runTest(testDispatcher) {
        // Arrange
        val producto = Producto(
            codigo = 1,
            nombre = "Mouse Gamer",
            precio = 150000.0,
            cantidad = 5
        )

        // Act
        viewModel.updateProduct(producto)
        advanceUntilIdle()

        // Assert
        verify(repository).update(producto)
    }
}
