package com.example.inventoryapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.databinding.ItemProductoBinding
import com.example.inventoryapp.model.Producto // ¡Importación correcta!
import java.util.Locale // Necesario para el formato de moneda

class ProductAdapter(private val onItemClicked: (Producto) -> Unit) : ListAdapter<Producto, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // 1. ViewHolder
    class ProductViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre

            // Formato de ID y Cantidad. Muestro la cantidad como un detalle del ID.
            binding.tvIdProducto.text = "Id: ${producto.codigo} | Cantidad: ${producto.cantidad}"

            // Formato de precio con separador de miles (ajustado para Colombia/Latam)
            val formattedPrice = String.format(Locale.getDefault(), "$ %,.2f", producto.precio)
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

    // 2. DiffUtil: Usa Producto en lugar de Product
    class ProductDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.codigo == newItem.codigo // Usa el ID único (código)
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem // Verifica si los datos internos son iguales
        }
    }

    // 3. Métodos del Adapter
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