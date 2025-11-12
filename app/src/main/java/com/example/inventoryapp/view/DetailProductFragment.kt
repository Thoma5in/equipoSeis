package com.example.inventoryapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentDetailProductBinding
import com.example.inventoryapp.viewmodel.HomeViewModel
import java.util.Locale

class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private var currentProductId: Long? = null

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

        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(HomeViewModel::class.java)

        // Flecha atrás (toolbar y botón)
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Obtener el ID del producto desde los argumentos
        currentProductId = arguments?.getLong("productId")

        // Observar los productos y mostrar el que coincida
        currentProductId?.let { productId ->
            homeViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                products.find { it.codigo.toLong() == productId }?.let { product ->
                    binding.tvProductName.text = product.nombre
                    binding.tvUnitPrice.text =
                        String.format(Locale.getDefault(), "$ %,.2f", product.precio)
                    binding.tvQuantity.text = product.cantidad.toString()
                    val total = product.precio * product.cantidad
                    binding.tvTotal.text =
                        String.format(Locale.getDefault(), "$ %,.2f", total)
                }
            }
        }

        // 🔹 Botón eliminar
        binding.btnDelete.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Estás seguro de que deseas eliminar este producto?")

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setPositiveButton("Sí") { dialog, _ ->
                currentProductId?.let { productId ->
                    homeViewModel.deleteProductById(productId)
                    findNavController().navigateUp()
                }
                dialog.dismiss()
            }

            builder.create().show()
        }

        // 🔸 Nuevo: Botón flotante Editar
        binding.fabEditProduct.setOnClickListener {
            currentProductId?.let { productId ->
                val bundle = Bundle().apply {
                    putInt("productId", productId.toInt())
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