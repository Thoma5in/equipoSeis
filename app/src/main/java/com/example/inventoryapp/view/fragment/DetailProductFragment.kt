package com.example.inventoryapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentDetailProductBinding
import com.example.inventoryapp.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private var currentProductId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Flecha atrás (toolbar y botón)
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Obtener el ID del producto desde los argumentos
        currentProductId = arguments?.getInt("productId")

        // Observar los productos y mostrar el que coincida
        currentProductId?.let { productId ->
            homeViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                products.find { it.codigo == productId }?.let { product ->
                    binding.tvProductName.text = product.nombre
                    binding.tvUnitPrice.text =
                        String.Companion.format(Locale.getDefault(), "$ %,.2f", product.precio)
                    binding.tvQuantity.text = product.cantidad.toString()
                    val total = product.precio * product.cantidad
                    binding.tvTotal.text =
                        String.Companion.format(Locale.getDefault(), "$ %,.2f", total)
                }
            }
        }

        // 🔹 Botón eliminar
        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Estás seguro de que deseas eliminar este producto?")

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setPositiveButton("Sí") { dialog, _ ->
                currentProductId?.let { productId ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        homeViewModel.deleteProductById(productId)
                        val updateIntent = Intent("com.example.inventoryapp.ACTION_UPDATE_WIDGET")
                        updateIntent.setPackage(requireContext().packageName)
                        requireContext().sendBroadcast(updateIntent)

                        findNavController().navigateUp()
                    }
                }
                dialog.dismiss()
            }

            builder.create().show()
        }

        // Botón flotante Editar
        binding.fabEditProduct.setOnClickListener {
            currentProductId?.let { productId ->
                val bundle = Bundle().apply {
                    putInt("productId", productId)
                }
                findNavController().navigate(
                    R.id.action_productDetailFragment_to_editProductFragment,
                    bundle
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}