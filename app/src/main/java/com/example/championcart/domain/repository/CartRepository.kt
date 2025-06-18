package com.example.championcart.domain.repository

import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.Product

interface CartRepository {
    suspend fun getCartItems(): Result<List<CartItem>>
    suspend fun addToCart(product: Product): Result<Unit>
    suspend fun removeFromCart(productId: String): Result<Unit>
    suspend fun updateQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun getCartItemCount(): Result<Int>
}