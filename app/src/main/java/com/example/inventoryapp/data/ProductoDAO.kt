package com.example.inventoryapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.lifecycle.LiveData
import com.example.inventoryapp.model.Producto

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProducts(): LiveData<List<Producto>>

    // 🔹 Nuevo método para eliminar producto por ID
    @Query("DELETE FROM productos WHERE codigo = :id")
    suspend fun deleteById(id: Long)

    // NUEVO: obtener producto por id
    @Query("SELECT * FROM productos WHERE codigo = :id LIMIT 1")
    fun getProductById(id: Int): LiveData<Producto?>


}
