package com.example.inventoryapp.viewmodel

import app.cash.turbine.test
import com.example.inventoryapp.repository.AuthRepository
import com.example.inventoryapp.viewmodel.LoginViewModel.AuthEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val repository: AuthRepository = mock()

    @BeforeEach
    fun setup() {
        viewModel = LoginViewModel(repository)
    }

    // ---------------------------------------------------------
    // LOGIN SUCCESS
    // ---------------------------------------------------------
    @Test
    fun `login exitoso emite LoginSuccess`() = runTest {
        whenever(repository.login(any(), any())).thenReturn(true)

        viewModel.authEvents.test {
            viewModel.login("test@mail.com", "1234")

            val event = awaitItem()
            assertTrue(event is AuthEvent.LoginSuccess)
        }
    }

    // ---------------------------------------------------------
    // LOGIN ERROR - CREDENCIALES INVÁLIDAS
    // ---------------------------------------------------------
    @Test
    fun `login incorrecto emite LoginError`() = runTest {
        whenever(repository.login(any(), any())).thenReturn(false)

        viewModel.authEvents.test {
            viewModel.login("test@mail.com", "wrong")

            val event = awaitItem()
            assertTrue(event is AuthEvent.LoginError)
            assertEquals("Login Incorrecto", (event as AuthEvent.LoginError).message)
        }
    }

    // ---------------------------------------------------------
    // LOGIN ERROR - EXCEPCIÓN
    // ---------------------------------------------------------
    @Test
    fun `login lanza excepción y emite LoginError`() = runTest {
        whenever(repository.login(any(), any()))
            .thenThrow(RuntimeException("Fallo de red"))

        viewModel.authEvents.test {
            viewModel.login("email@mail.com", "123")

            val event = awaitItem()
            assertTrue(event is AuthEvent.LoginError)
            assertTrue((event as AuthEvent.LoginError).message.contains("Fallo de red"))
        }
    }

    // ---------------------------------------------------------
    // REGISTER SUCCESS
    // ---------------------------------------------------------
    @Test
    fun `register exitoso emite SignUpSuccess`() = runTest {
        whenever(repository.register(any(), any())).thenReturn(true)

        viewModel.authEvents.test {
            viewModel.register("test@mail.com", "1234")

            val event = awaitItem()
            assertTrue(event is AuthEvent.SignUpSuccess)
        }
    }

    // ---------------------------------------------------------
    // REGISTER ERROR
    // ---------------------------------------------------------
    @Test
    fun `register incorrecto emite SignUpError`() = runTest {
        whenever(repository.register(any(), any())).thenReturn(false)

        viewModel.authEvents.test {
            viewModel.register("test@mail.com", "1234")

            val event = awaitItem()
            assertTrue(event is AuthEvent.SignUpError)
        }
    }

    // ---------------------------------------------------------
    // REGISTER EXCEPTION
    // ---------------------------------------------------------
    @Test
    fun `register lanza excepción y emite SignUpError`() = runTest {
        whenever(repository.register(any(), any()))
            .thenThrow(RuntimeException("Error desconocido"))

        viewModel.authEvents.test {
            viewModel.register("test@mail.com", "123")

            val event = awaitItem()
            assertTrue(event is AuthEvent.SignUpError)
            assertTrue((event as AuthEvent.SignUpError).message.contains("Error desconocido"))
        }
    }
}
