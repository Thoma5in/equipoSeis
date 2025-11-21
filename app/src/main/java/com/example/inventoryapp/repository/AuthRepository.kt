package com.example.inventoryapp.repository


import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    // LOGIN
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // REGISTRO
    suspend fun register(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // VERIFICAR SI HAY SESIÓN ACTIVA
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // CERRAR SESIÓN
    fun logout() {
        auth.signOut()
    }

}