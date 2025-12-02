package com.example.inventoryapp.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentAgregarProductoBinding
import com.example.inventoryapp.viewmodel.AgregarProducto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgregarProductoFragment : Fragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgregarProducto by viewModels()

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

        inicializarVistas()
        configurarToolbar()
        observarCampos()

        binding.btnGuardarProducto.setOnClickListener {
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
                    R.color.gray
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
        val nombre = binding.etNombreArticulo.text.toString().trim()
        val precio = binding.etPrecio.text.toString().toDoubleOrNull()
        val cantidad = binding.etCantidad.text.toString().toIntOrNull()

        if (codigo == null || codigo <= 0) {
            Toast.makeText(requireContext(), "El código debe ser un número válido mayor que 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (nombre.isBlank()) {
            Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (precio == null || precio <= 0) {
            Toast.makeText(requireContext(), "El precio debe ser un número mayor que 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidad == null || cantidad < 0) {
            Toast.makeText(requireContext(), "La cantidad debe ser un número mayor o igual a 0", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.guardarNuevoProducto(codigo, nombre, precio, cantidad) { result ->
            when (result) {

                1 -> {  // Guardado exitoso
                    Toast.makeText(requireContext(), "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                0 -> {  // Ya existe
                    Toast.makeText(requireContext(), "El producto con el código $codigo ya existe", Toast.LENGTH_SHORT).show()
                }

                -1 -> { // Error inesperado
                    Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}