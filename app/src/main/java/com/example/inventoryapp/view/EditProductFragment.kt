package com.example.inventoryapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.databinding.FragmentEditProductBinding
import com.example.inventoryapp.model.Producto
import com.example.inventoryapp.viewmodel.EditProductViewModel

class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProductViewModel by viewModels {
        EditProductViewModel.Factory(requireActivity().application)
    }

    private var currentProduct: Producto? = null
    private var productId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar ID del producto desde el Bundle (sin SafeArgs)
        productId = arguments?.getInt("productId") ?: -1
        if (productId == -1) {
            Toast.makeText(requireContext(), "Error: producto no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // Botón volver atrás
        binding.arrowBackEdit.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observa el producto en la BD
        viewModel.getProduct(productId).observe(viewLifecycleOwner, Observer { producto ->
            producto?.let {
                currentProduct = it
                binding.tvProductId.text = it.codigo.toString()
                binding.etName.setText(it.nombre)
                binding.etPrice.setText(it.precio.toString())
                binding.etQuantity.setText(it.cantidad.toString())
                validateFields()
            }
        })

        // TextWatcher para validar campos dinámicamente
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)

        // Botón Editar
        binding.btnEdit.setOnClickListener {
            val prod = currentProduct ?: return@setOnClickListener
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val priceText = binding.etPrice.text?.toString()?.trim() ?: ""
            val quantityText = binding.etQuantity.text?.toString()?.trim() ?: ""

            val price = priceText.toDoubleOrNull()
            val qty = quantityText.toIntOrNull()

            if (name.isBlank() || price == null || qty == null) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crea el producto actualizado (mismo código)
            val updated = Producto(
                codigo = prod.codigo,
                nombre = name,
                precio = price,
                cantidad = qty
            )

            // Guarda (insert con REPLACE hace upsert)
            viewModel.updateProduct(updated)

            Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()

            // Regresa a la ventana anterior (detalle)
            findNavController().popBackStack()
        }
    }

    private fun validateFields() {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val priceText = binding.etPrice.text?.toString()?.trim() ?: ""
        val quantityText = binding.etQuantity.text?.toString()?.trim() ?: ""

        val priceValid = priceText.isNotEmpty() && priceText.toDoubleOrNull() != null
        val qtyValid = quantityText.isNotEmpty() && quantityText.toIntOrNull() != null

        binding.btnEdit.isEnabled = name.isNotBlank() && priceValid && qtyValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}