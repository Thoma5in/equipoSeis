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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryapp.databinding.FragmentHomeBinding
import com.example.inventoryapp.view.AgregarProductoActivity
import com.example.inventoryapp.view.LoginActivity
import com.example.inventoryapp.view.ProductAdapter
import com.example.inventoryapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private val productAdapter = ProductAdapter()

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

        homeViewModel.allProducts.observe(viewLifecycleOwner, Observer { products ->
            products?.let { productAdapter.submitList(it) }
        })

        binding.imageButton.setOnClickListener { logout() }

        binding.fabAgregarProducto.setOnClickListener {
            val intent = Intent(requireContext(), AgregarProductoActivity::class.java)
            startActivity(intent)
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