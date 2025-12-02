package com.example.inventoryapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentHomeBinding
import com.example.inventoryapp.view.LoginActivity
import com.example.inventoryapp.view.adapter.ProductAdapter
import com.example.inventoryapp.view.widget.InventoryWidgetProvider
import com.example.inventoryapp.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private val homeViewModel: HomeViewModel by viewModels()
    private val productAdapter = ProductAdapter { product ->
        val bundle = Bundle().apply {
            // Nav graph declara productId como long, así que pasamos un Long
            putLong("productId", product.codigo.toLong())
        }
        findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        // ⭐ LÓGICA DE VISIBILIDAD: CONTROLADA POR EL ESTADO DE CARGA
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // Muestra el Progress Bar y oculta la lista
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerViewProductos.visibility = View.GONE
            } else {
                // Oculta el Progress Bar y muestra la lista (después de 3 segundos)
                binding.progressCircular.visibility = View.GONE
                binding.recyclerViewProductos.visibility = View.VISIBLE
            }
        }

        // Listado desde Firestore
        homeViewModel.allProducts.observe(viewLifecycleOwner) { productos ->
            productAdapter.submitList(productos)
        }

        binding.imageButton.setOnClickListener { logout() }

        binding.fabAgregarProducto.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
        }
    }
    private fun logout() {
        firebaseAuth.signOut()
        val prefs = requireActivity().getSharedPreferences("user_session", 0)
        prefs.edit {
            clear()
            commit()
        }
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

        val updateIntent = Intent(requireContext(), InventoryWidgetProvider::class.java).apply {
            action = "com.example.inventoryapp.ACTION_UPDATE_WIDGET"
        }
        requireContext().sendBroadcast(updateIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}