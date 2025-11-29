package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()  {
    private val _authEvents = MutableSharedFlow<AuthEvent?>(
        replay = 0,
        extraBufferCapacity = 1
    )
    // Exposed como SharedFlow inmutable
    val authEvents: SharedFlow<AuthEvent?> = _authEvents

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)

                if (result) {
                    _authEvents.emit(AuthEvent.LoginSuccess)
                } else {
                    _authEvents.emit(AuthEvent.LoginError("Login Incorrecto"))
                }
            } catch (e: Exception) {
                _authEvents.emit(AuthEvent.LoginError(e.message ?: "Error de red o desconocido"))
            }
        }
    }
    fun register(email: String, password: String){
        viewModelScope.launch {
            try {
                val result = authRepository.register(email, password)

                if (result) {
                    _authEvents.emit(AuthEvent.SignUpSuccess)
                } else {
                    _authEvents.emit(AuthEvent.SignUpError("Error de registro"))
                }
            } catch (e: Exception) {
                _authEvents.emit(AuthEvent.SignUpError(e.message ?: "Error de red o desconocido"))
            }
        }
    }
    sealed class AuthEvent {
        data object LoginSuccess : AuthEvent()
        data object SignUpSuccess : AuthEvent()
        data class LoginError(val message: String) : AuthEvent()
        data class SignUpError(val message: String) : AuthEvent()
    }
}