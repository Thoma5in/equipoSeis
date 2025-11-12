package com.example.inventoryapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.databinding.ItemProductoBinding
import com.example.inventoryapp.model.Producto
import java.util.Locale

class ProductAdapter(private val onItemClicked: (Producto) -> Unit) : ListAdapter<Producto, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    class ProductViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre

            // Muestro la cantidad como un detalle del ID.
            binding.tvIdProducto.text = "Id: ${producto.codigo} | Cantidad: ${producto.cantidad}"

            // Formato de precio con separador de miles
            val formattedPrice = String.Companion.format(Locale.getDefault(), "$ %,.2f", producto.precio)
            binding.tvPrecioProducto.text = formattedPrice
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductoBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

    // DiffUtil: para optimizar cambios en la lista
    class ProductDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.codigo == newItem.codigo
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem // Verifica si los datos internos son iguales
        }
    }

    // Métodos del Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentProduct)
        }
        holder.bind(currentProduct)
    }
}