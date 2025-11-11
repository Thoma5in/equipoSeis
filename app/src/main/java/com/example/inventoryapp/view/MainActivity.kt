package com.example.inventoryapp.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventoryapp.R
// NOTA: Se eliminaron las importaciones de binding, Intent, LoginActivity, y los listeners de las vistas
//       porque esa lógica ahora está en HomeFragment.kt

class MainActivity : AppCompatActivity() {

    // Se elimina 'private lateinit var binding: ActivityMainBinding'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Infla el activity_main.xml, que ahora solo tiene el NavHostFragment.
        setContentView(R.layout.activity_main)

        // 2. Mantiene el manejo de ViewCompat (ajuste de insets) si es necesario.
        //    (Nota: Esto podría ser simplificado o movido a un Fragment más adelante, pero lo dejamos por compatibilidad.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Se eliminan: binding = ActivityMainBinding.inflate(layoutInflater)
        // 4. Se eliminan: setContentView(binding.root)
        // 5. Se eliminan: binding.imageButton.setOnClickListener { logout() }
        // 6. Se eliminan: binding.fabAgregarProducto.setOnClickListener { ... }
    }

    // Se elimina la función 'private fun logout()' porque ahora está en HomeFragment.kt
}


