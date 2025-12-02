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