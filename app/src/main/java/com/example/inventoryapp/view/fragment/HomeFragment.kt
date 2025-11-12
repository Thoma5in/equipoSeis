package com.example.inventoryapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentHomeBinding
import com.example.inventoryapp.view.LoginActivity
import com.example.inventoryapp.view.ProductAdapter
import com.example.inventoryapp.viewmodel.HomeViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private val productAdapter = ProductAdapter { product ->
        val bundle = Bundle().apply {
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

        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(HomeViewModel::class.java)

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        // Mostrar loader antes de cargar los datos
        binding.progressCircular.visibility = View.VISIBLE

        homeViewModel.allProducts.observe(viewLifecycleOwner, Observer { Productos ->
            binding.progressCircular.visibility = View.GONE
            productAdapter.submitList(emptyList())
            if (Productos.isNullOrEmpty()) {
                    // Opcional: mostrar mensaje o vista vacía
            } else {
                productAdapter.submitList(Productos.toList())
            }
            })

        binding.imageButton.setOnClickListener { logout() }

        binding.fabAgregarProducto.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
        }
    }
    private fun logout() {
        val prefs = requireActivity().getSharedPreferences("user_session", 0)
        prefs.edit {
            clear()
            commit()
        }
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}