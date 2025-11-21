package com.example.inventoryapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.inventoryapp.databinding.ActivityLoginBinding
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        // Primero verifica si hay una sesión activa
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Si no hay sesión se muestra la pantalla de login
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fingerprintAnimation.setOnClickListener {
            showBiometricPrompt()
        }

        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener {
                Log.d("FIREBASE_TEST", "Firebase conectado correctamente")
                Toast.makeText(this, "Firebase OK!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("FIREBASE_TEST", "Error de conexión: ${it.message}")
                Toast.makeText(this, "Error Firebase", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    goToMainActivity()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con huella")
            .setSubtitle("Usa tu huella para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun goToMainActivity() {
//        // Guardar la sesión
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        prefs.edit {
            putBoolean("is_logged_in", true)
            commit()
        }

        // Lanzar la MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Cerrar el LoginActivity
        finish()
    }
}
