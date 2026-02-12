package com.example.navya.data.local.dao

import androidx.room.*
import com.example.navya.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE plantId = :plantId")
    fun getCartItem(plantId: String): Flow<CartItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE plantId = :plantId")
    suspend fun deleteCartItem(plantId: String)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
