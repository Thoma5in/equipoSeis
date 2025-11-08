package com.example.inventoryapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.lifecycle.LiveData // Necesitas esta importación
import com.example.inventoryapp.model.Producto

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProducts(): LiveData<List<Producto>>
}