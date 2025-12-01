package com.example.inventoryapp.repository

import android.content.Context
import android.content.Intent
import com.example.inventoryapp.view.widget.InventoryWidgetProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
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
        val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "com.example.inventoryapp.ACTION_UPDATE_WIDGET"
        }
        context.sendBroadcast(intent)
    }
}