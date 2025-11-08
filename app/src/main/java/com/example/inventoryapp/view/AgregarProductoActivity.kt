package com.example.inventoryapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.inventoryapp.R
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.repository.InventoryRepository
import com.example.inventoryapp.viewmodel.AgregarProducto
import com.example.inventoryapp.viewmodel.InventoryFactory
import com.google.android.material.textfield.TextInputEditText

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var etCodigo: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etPrecio: TextInputEditText
    private lateinit var etCantidad: TextInputEditText
    private lateinit var btnGuardar: Button

    private lateinit var viewModel: AgregarProducto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto) // Asumiendo que el layout se llama así

        // 1. Inicialización de Room y ViewModel
        val dao = AppDatabase.getDatabase(applicationContext).productoDao()
        val repository = InventoryRepository(dao)
        val factory = InventoryFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AgregarProducto::class.java]

        // Inicializar vistas
        inicializarVistas()

        // Criterio 1: Configurar la Toolbar para volver
        configurarToolbar()

        // Criterios 6 y 7: Validación de campos
        observarCampos()

        // Criterio 8: Manejar el clic del botón Guardar
        btnGuardar.setOnClickListener {
            if (btnGuardar.isEnabled) {
                guardarProducto()
            }
        }
    }

    private fun inicializarVistas() {
        etCodigo = findViewById(R.id.et_codigo_producto)
        etNombre = findViewById(R.id.et_nombre_articulo)
        etPrecio = findViewById(R.id.et_precio)
        etCantidad = findViewById(R.id.et_cantidad)
        btnGuardar = findViewById(R.id.btn_guardar_producto)
    }

    // Criterio 1: Configurar el botón de retorno
    private fun configurarToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_agregar_producto)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // Cierra esta Activity y regresa a la anterior (Home Inventario)
        }
    }

    // Criterios 6 y 7: Lógica para habilitar/deshabilitar el botón "Guardar"
    private fun observarCampos() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isCodigoLleno = etCodigo.text.toString().isNotBlank()
                val isNombreLleno = etNombre.text.toString().isNotBlank()
                val isPrecioLleno = etPrecio.text.toString().isNotBlank()
                val isCantidadLleno = etCantidad.text.toString().isNotBlank()

                // Habilitar el botón si todos los campos tienen texto
                btnGuardar.isEnabled = isCodigoLleno && isNombreLleno && isPrecioLleno && isCantidadLleno
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etCodigo.addTextChangedListener(watcher)
        etNombre.addTextChangedListener(watcher)
        etPrecio.addTextChangedListener(watcher)
        etCantidad.addTextChangedListener(watcher)
    }

    // Criterio 8: Lógica para guardar el producto
    private fun guardarProducto() {
        // Se valida aquí para asegurar la conversión a números
        val codigo = etCodigo.text.toString().toIntOrNull()
        val nombre = etNombre.text.toString()
        val precio = etPrecio.text.toString().toDoubleOrNull()
        val cantidad = etCantidad.text.toString().toIntOrNull()

        // El botón está habilitado, pero revisamos conversiones por seguridad
        if (codigo != null && precio != null && cantidad != null) {
            viewModel.guardarNuevoProducto(codigo, nombre, precio, cantidad)

            Toast.makeText(this, "Producto guardado con éxito!", Toast.LENGTH_SHORT).show()
            finish() // Cierra la Activity después de guardar (vuelve a Home Inventario)
        } else {
            Toast.makeText(this, "Verifica los valores numéricos.", Toast.LENGTH_SHORT).show()
        }
    }
}