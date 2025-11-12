package com.example.inventoryapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.inventoryapp.databinding.ActivityLoginBinding
import androidx.core.content.edit

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
    }


    private fun showBiometricPrompt() {
        
            val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(
                    this,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                            prefs.edit {
                                putBoolean("is_logged_in", true)
                                apply()
                            }
                            // ✅ Abrir la siguiente actividad sólo si todo salió bien
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticación con huella")
                    .setSubtitle("Usa tu huella para iniciar sesión")
                    .setNegativeButtonText("Cancelar")
                    .build()

                try {
                    biometricPrompt.authenticate(promptInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "No se pudo iniciar el lector biométrico", Toast.LENGTH_SHORT).show()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "Configura al menos una huella en tu dispositivo", Toast.LENGTH_LONG).show()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "Tu dispositivo no tiene sensor biométrico", Toast.LENGTH_LONG).show()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "El sensor biométrico no está disponible", Toast.LENGTH_LONG).show()
            }

            else -> {
                Toast.makeText(this, "Autenticación biométrica no disponible", Toast.LENGTH_LONG).show()
            }
        }
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
