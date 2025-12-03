package com.example.inventoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventoryapp.FakeListenerRegistration
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.repository.FirestoreInventoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*



class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository: FirestoreInventoryRepository = mock()

    @Test
    fun `init loads products and updates LiveData`() {
        var callback: ((List<Producto>) -> Unit)? = null

        whenever(repository.getAllProductos(any())).thenAnswer {
            callback = it.arguments[0] as (List<Producto>) -> Unit
            FakeListenerRegistration()
        }

        val vm = HomeViewModel(repository)

        val listaFake = listOf(Producto(1, "p1", 1000.0, 2))
        callback!!.invoke(listaFake)

        assertFalse(vm.isLoading.value!!)
        assertEquals(1, vm.allProducts.value!!.size)
    }

    @Test
    fun `deleteProductById calls repository delete`() = runTest {
        val vm = HomeViewModel(repository)
        vm.deleteProductById(10)
        verify(repository).delete(10)
    }
}
