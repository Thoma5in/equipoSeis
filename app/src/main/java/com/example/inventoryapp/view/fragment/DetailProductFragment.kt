package com.example.inventoryapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentDetailProductBinding
import com.example.inventoryapp.viewmodel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    // ahora almacenamos el id como Long porque el nav-arg está declarado como long
    private var currentProductId: Long? = null

    // Firestore instance
    private val firestore by lazy { FirebaseFirestore.getInstance() }

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

        // Obtener el ID del producto desde los argumentos (long en nav-graph)
        currentProductId = arguments?.getLong("productId")

        // Cargar el producto desde Firestore
        currentProductId?.let { productId ->
            loadProductFromFirestore(productId)

            // También mantenemos la observación local por compatibilidad (si existe)
            homeViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                // product.codigo es Int en el modelo local, por eso convertimos a Long para comparar
                products.find { it.codigo.toLong() == productId }?.let { product ->
                    // Si el producto local difiere, preferimos la data de Firestore cargada
                    // pero dejamos esto por compatibilidad de la app
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
                    // Llamamos al ViewModel para eliminar desde el repositorio/Firestore canonical
                    lifecycleScope.launch {
                        try {
                            // Validación segura al convertir Long a Int
                            if (productId > Int.MAX_VALUE) {
                                Log.e("DetailProductFragment", "productId demasiado grande para convertir a Int: $productId")
                                Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            homeViewModel.deleteProductById(productId.toInt())

                            // Notificar y navegar tras eliminación
                            onProductDeleted()
                        } catch (e: Exception) {
                            Log.e("DetailProductFragment", "Error al eliminar producto via ViewModel: ", e)
                            Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                        }
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
                    // EditProductFragment espera un argumento integer, así que convertimos a Int de forma segura
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

    // Función para cargar el producto desde Firestore (usa Long)
    private fun loadProductFromFirestore(productId: Long) {
        val productsColl = firestore.collection("productos")

        // Primero intentamos buscar por campo 'codigo'
        productsColl.whereEqualTo("codigo", productId).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents.first()
                    updateUIFromDocument(doc.data, doc.id)
                } else {
                    // Si no hay coincidencias por campo, intentamos por id del documento
                    productsColl.document(productId.toString()).get()
                        .addOnSuccessListener { docSnap ->
                            if (docSnap.exists()) {
                                updateUIFromDocument(docSnap.data, docSnap.id)
                            } else {
                                // Antes se mostraba un Toast; ahora lo registramos en la consola
                                Log.w("DetailProductFragment", "Producto no encontrado en Firestore: id=${productId}")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DetailProductFragment", "Error al obtener documento por id: ", e)
                            Toast.makeText(requireContext(), "Error al cargar producto", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("DetailProductFragment", "Error al consultar Firestore: ", e)
                Toast.makeText(requireContext(), "Error al cargar producto", Toast.LENGTH_SHORT).show()
            }
    }

    // Actualiza la UI tomando un Map (datos del documento)
    private fun updateUIFromDocument(data: Map<String, Any>?, documentId: String) {
        if (data == null) return

        val nombre = data["nombre"] as? String ?: data["Nombre"] as? String ?: ""
        val precio = when (val p = data["precio"] ?: data["Precio"]) {
            is Double -> p
            is Float -> p.toDouble()
            is Long -> p.toDouble()
            is Int -> p.toDouble()
            else -> 0.0
        }
        val cantidad = when (val q = data["cantidad"] ?: data["Cantidad"]) {
            is Long -> q.toInt()
            is Int -> q
            is Double -> q.toInt()
            else -> 0
        }

        binding.tvProductName.text = nombre
        binding.tvUnitPrice.text = String.format(Locale.getDefault(), "$ %,.2f", precio)
        binding.tvQuantity.text = cantidad.toString()
        val total = precio * cantidad
        binding.tvTotal.text = String.format(Locale.getDefault(), "$ %,.2f", total)

        // Guardar el id del documento si queremos usarlo en ediciones/eliminaciones futuras
        // (opcional): podemos guardar documentId en una variable si hace falta
        Log.d("DetailProductFragment", "Producto cargado desde Firestore: id=$documentId")
    }


    private fun onProductDeleted() {
        // Notificar widget y navegar hacia atrás
        val updateIntent = Intent("com.example.inventoryapp.ACTION_UPDATE_WIDGET")
        updateIntent.setPackage(requireContext().packageName)
        requireContext().sendBroadcast(updateIntent)

        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
}