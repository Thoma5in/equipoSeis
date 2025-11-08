package com.example.inventoryapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        setContentView(R.layout.activity_agregar_producto)

        // 1. Inicialización de Room y ViewModel
        val dao = AppDatabase.getDatabase(applicationContext).productoDao()
        val repository = InventoryRepository(dao)
        val factory = InventoryFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AgregarProducto::class.java]

        inicializarVistas()

        configurarToolbar()

        observarCampos()

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

    private fun configurarToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_agregar_producto)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observarCampos() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isCodigoLleno = etCodigo.text.toString().isNotBlank()
                val isNombreLleno = etNombre.text.toString().isNotBlank()
                val isPrecioLleno = etPrecio.text.toString().isNotBlank()
                val isCantidadLleno = etCantidad.text.toString().isNotBlank()

                val estaHabilitado = isCodigoLleno && isNombreLleno && isPrecioLleno && isCantidadLleno
                btnGuardar.isEnabled = estaHabilitado

                val colorId = if (estaHabilitado) {
                    // Usar color blanco Activo
                    android.R.color.white
                } else {
                    // Usar color gris Inactivo
                    R.color.gray
                }


                val color = ContextCompat.getColor(this@AgregarProductoActivity, colorId)
                btnGuardar.setTextColor(color)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etCodigo.addTextChangedListener(watcher)
        etNombre.addTextChangedListener(watcher)
        etPrecio.addTextChangedListener(watcher)
        etCantidad.addTextChangedListener(watcher)

        watcher.afterTextChanged(null)
    }

    private fun guardarProducto() {

        val codigo = etCodigo.text.toString().toIntOrNull()
        val nombre = etNombre.text.toString()
        val precio = etPrecio.text.toString().toDoubleOrNull()
        val cantidad = etCantidad.text.toString().toIntOrNull()


        if (codigo != null && precio != null && cantidad != null) {
            viewModel.guardarNuevoProducto(codigo, nombre, precio, cantidad)

            Toast.makeText(this, "Producto guardado con éxito!", Toast.LENGTH_SHORT).show()
            finish() // Cierra la Activity después de guardar (vuelve a Home Inventario)
        } else {
            Toast.makeText(this, "Verifica los valores numéricos.", Toast.LENGTH_SHORT).show()
        }
    }
}