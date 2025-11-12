package com.example.inventoryapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.lifecycle.LiveData
import com.example.inventoryapp.model.Producto

@Dao
interface ProductoDao {
    //crear producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProducts(): LiveData<List<Producto>>

    // eliminar producto por ID
    @Query("DELETE FROM productos WHERE codigo = :id")
    suspend fun deleteById(id: Long)

    // obtener producto por id
    @Query("SELECT * FROM productos WHERE codigo = :id LIMIT 1")
    fun getProductById(id: Int): LiveData<Producto?>

    // Calcular el valor total del inventario
    @Query("SELECT SUM(precio * cantidad) FROM productos")
    suspend fun getInventoryTotalValue(): Double?

}
