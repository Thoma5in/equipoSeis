package com.example.inventoryapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit
import com.example.inventoryapp.databinding.ActivityMainBinding // CLAVE: Importar la clase generada
import com.example.inventoryapp.R
import com.example.inventoryapp.view.AgregarProductoActivity
import com.example.inventoryapp.view.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imageButton.setOnClickListener {
            logout()
        }

        binding.fabAgregarProducto.setOnClickListener {

            val intent = Intent(this, AgregarProductoActivity::class.java)
            startActivity(intent)
        }

    }

    private fun logout() {
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        prefs.edit {
            clear() // Borra la sesión
            commit() // Guarda los cambios de forma síncrona
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Cierra MainActivity para que el usuario no pueda volver con el botón "Atrás"
    }
}