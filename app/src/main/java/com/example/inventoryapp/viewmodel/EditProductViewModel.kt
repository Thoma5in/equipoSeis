package com.example.inventoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProductViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    fun getProduct(id: Int): LiveData<Producto?> {
       val liveData = MutableLiveData<Producto?>()

        firestore.collection("productos")
            .document(id.toString())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    liveData.value = snapshot.toObject(Producto::class.java)
                }
            }

        return liveData

    }

    fun updateProduct(producto: Producto) {
        viewModelScope.launch {
        try {
            firestore.collection("productos")
                .document(producto.codigo.toString())
                .set(producto)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()

            }
        }
    }

    //  Fábrica actualizada — usa la nueva firma correcta
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditProductViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}