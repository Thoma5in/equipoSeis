package com.example.inventoryapp.repository

import com.example.inventoryapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreInventoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("productos")

    // ---------------------------------------------------------------------------------------------
    // 1) Validar si existe un producto por el ID
    // ---------------------------------------------------------------------------------------------
    suspend fun exists(codigo: Int): Boolean {
        return try {
            val snapshot = collection.document(codigo.toString()).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false  // Si falla la lectura, asumimos que NO existe
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 2) Insertar producto SIN validación
    // ---------------------------------------------------------------------------------------------
    fun insert(producto: Producto, onResult: (Boolean) -> Unit) {
        collection
            .document(producto.codigo.toString())
            .set(producto)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // ---------------------------------------------------------------------------------------------
    // 3) Insertar producto SOLO si NO existe
    // ---------------------------------------------------------------------------------------------
    suspend fun insertIfNotExists(producto: Producto): InsertResult {
        return try {
            val existe = exists(producto.codigo)
            if (existe) {
                InsertResult.AlreadyExists(producto.codigo)
            } else {
                collection.document(producto.codigo.toString()).set(producto).await()
                InsertResult.Success
            }
        } catch (e: Exception) {
            InsertResult.Error(e)
        }
    }

    // Resultado tipado para el ViewModel
    sealed class InsertResult {
        object Success : InsertResult()
        data class AlreadyExists(val codigo: Int) : InsertResult()
        data class Error(val exception: Exception) : InsertResult()
    }

    // ---------------------------------------------------------------------------------------------
    // 4) Eliminar un producto
    // ---------------------------------------------------------------------------------------------
    suspend fun delete(codigo: Int) {
        collection.document(codigo.toString()).delete().await()
    }

    // ---------------------------------------------------------------------------------------------
    // 5) Obtener producto por ID (tiempo real)
    // ---------------------------------------------------------------------------------------------
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

    // ---------------------------------------------------------------------------------------------
    // 6) Obtener TODOS los productos una vez
    // ---------------------------------------------------------------------------------------------
    suspend fun getAllProductsOnce(): List<Producto> {
        return try {
            collection.get().await().toObjects(Producto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 7) Escuchar productos en tiempo real
    // ---------------------------------------------------------------------------------------------
    fun getAllProductos(onResult: (List<Producto>) -> Unit): ListenerRegistration {
        return collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
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

    // ---------------------------------------------------------------------------------------------
    // 8) Actualizar producto
    // ---------------------------------------------------------------------------------------------
    suspend fun update(producto: Producto) {
        collection.document(producto.codigo.toString()).set(producto).await()
    }
}
