package com.example.inventoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()  {

    //Canal para enviar eventos a la UI (Login Correcto o Incorrecto)
    private val _loginEvents = Channel<LoginEvent>()
    val loginEvents = _loginEvents.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)

            if (result) {
                _loginEvents.send(LoginEvent.Success)
            } else {
                _loginEvents.send(LoginEvent.Error("Login incorrecto"))
            }
        }
    }

    sealed class LoginEvent {
        object Success : LoginEvent()
        data class Error(val message: String) : LoginEvent()

    }

}