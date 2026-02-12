package com.example.navya.data.repository

import com.example.navya.data.local.dao.CartDao
import com.example.navya.data.local.entity.CartItemEntity
import com.example.navya.data.local.entity.PlantEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    val allCartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()

    fun getCartItemFlow(plantId: String): Flow<CartItemEntity?> = cartDao.getCartItem(plantId)

    suspend fun addToCart(plant: PlantEntity, quantity: Int = 1) {
        val cartItem = CartItemEntity(
            plantId = plant.id,
            name = plant.name,
            price = plant.price ?: 0.0,
            imageUrl = plant.image_url ?: "",
            quantity = quantity
        )
        cartDao.insertCartItem(cartItem)
    }

    suspend fun updateQuantity(plantId: String, quantity: Int) {

    }
    
    suspend fun saveCartItem(item: CartItemEntity) {
        if (item.quantity <= 0) {
            cartDao.deleteCartItem(item.plantId)
        } else {
            cartDao.insertCartItem(item)
        }
    }

    suspend fun removeFromCart(plantId: String) {
        cartDao.deleteCartItem(plantId)
    }
    
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
