package com.example.inventoryapp.repository

import com.example.inventoryapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await


class FirestoreInventoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("productos")

     fun insert(producto: Producto, onResult: (Boolean) -> Unit) {
        db.collection("productos")
            .document(producto.codigo.toString())
            .set(producto)
            .addOnSuccessListener {onResult(true)}
            .addOnFailureListener {onResult(false)}

    }

    suspend fun delete(codigo: Int) {
        collection.document(codigo.toString()).delete().await()

    }

    fun getProductById(productId: Int, onResult: (Producto?) -> Unit): ListenerRegistration {
        return collection.document(productId.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    onResult(snapshot.toObject(Producto::class.java))
                } else {
                    onResult(null)
                }
            }
    }

    suspend fun getAllProductsOnce(): List<Producto> {
        return try {
            collection.get().await().toObjects(Producto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }


    // ESCUCHAR TODOS LOS PRODUCTOS EN TIEMPO REAL
    fun getAllProductos(onResult: (List<Producto>) -> Unit): ListenerRegistration {
        return collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Si hay error, devolvemos lista vacía
                onResult(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val lista = snapshot.toObjects(Producto::class.java)
                onResult(lista)
            } else {
                onResult(emptyList())
            }
        }
    }

    suspend fun update (producto: Producto) {
        collection.document(producto.codigo.toString()).set(producto).await()
    }
}