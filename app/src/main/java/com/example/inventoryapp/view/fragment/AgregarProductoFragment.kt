package com.example.inventoryapp.view.fragment

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.databinding.FragmentAgregarProductoBinding
import com.example.inventoryapp.repository.InventoryRepository
import com.example.inventoryapp.viewmodel.AgregarProducto
import com.example.inventoryapp.viewmodel.InventoryFactory

class AgregarProductoFragment : Fragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AgregarProducto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)
        return binding.root // Retorna la vista raíz del binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de Room y ViewModel
        val dao = AppDatabase.Companion.getDatabase(requireContext()).productoDao()
        val repository = InventoryRepository(dao)
        val factory = InventoryFactory(repository)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this, factory)[AgregarProducto::class.java]

        inicializarVistas()
        configurarToolbar()
        observarCampos()

        binding.btnGuardarProducto.setOnClickListener { // Usar binding para el botón
            if (binding.btnGuardarProducto.isEnabled) {
                guardarProducto()
            }
        }
    }

    private fun inicializarVistas() {}

    private fun configurarToolbar() {

        binding.toolbarAgregarProducto.setNavigationOnClickListener {
            findNavController().popBackStack() // Usar Navigation para volver
        }
    }

    private fun observarCampos() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Usar binding para acceder a las vistas
                val isCodigoLleno = binding.etCodigoProducto.text.toString().isNotBlank()
                val isNombreLleno = binding.etNombreArticulo.text.toString().isNotBlank()
                val isPrecioLleno = binding.etPrecio.text.toString().isNotBlank()
                val isCantidadLleno = binding.etCantidad.text.toString().isNotBlank()

                val estaHabilitado = isCodigoLleno && isNombreLleno && isPrecioLleno && isCantidadLleno
                binding.btnGuardarProducto.isEnabled = estaHabilitado

                val colorId = if (estaHabilitado) {
                    R.color.white
                } else {
                    com.example.inventoryapp.R.color.gray
                }

                val color = ContextCompat.getColor(requireContext(), colorId)
                binding.btnGuardarProducto.setTextColor(color)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etCodigoProducto.addTextChangedListener(watcher)
        binding.etNombreArticulo.addTextChangedListener(watcher)
        binding.etPrecio.addTextChangedListener(watcher)
        binding.etCantidad.addTextChangedListener(watcher)

        watcher.afterTextChanged(null)
    }

    private fun guardarProducto() {

        val codigo = binding.etCodigoProducto.text.toString().toIntOrNull()
        val nombre = binding.etNombreArticulo.text.toString()
        val precio = binding.etPrecio.text.toString().toDoubleOrNull()
        val cantidad = binding.etCantidad.text.toString().toIntOrNull()


        if (codigo != null && precio != null && cantidad != null) {
            viewModel.guardarNuevoProducto(codigo, nombre, precio, cantidad)
            val updateIntent = Intent("com.example.inventoryapp.ACTION_UPDATE_WIDGET")
            updateIntent.setPackage(requireContext().packageName)
            requireContext().sendBroadcast(updateIntent)

            Toast.makeText(requireContext(), "Producto guardado con éxito!", Toast.LENGTH_SHORT).show()

            findNavController().popBackStack() // Vuelve al HomeFragment
        } else {
            Toast.makeText(requireContext(), "Verifica los valores numéricos.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}