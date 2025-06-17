package com.example.championcart.domain.repository

import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.CartProduct
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    // Get all items in the cart as Flow
    fun getCartItems(): Flow<List<CartProduct>>

    // Get cart items as list (one-time fetch)
    suspend fun getCartItemsList(): List<CartProduct>

    // Add a product to cart
    suspend fun addToCart(product: Product, quantity: Int = 1)

    // Update quantity of an item in cart
    suspend fun updateCartItemQuantity(itemName: String, quantity: Int)

    // Remove an item from cart
    suspend fun removeFromCart(itemName: String)

    // Clear all items from cart
    suspend fun clearCart()

    // Get total items count in cart
    fun getCartItemsCount(): Flow<Int>

    // Get cart total price (requires prices from API)
    suspend fun getCartTotal(city: String): Double

    // Check if a product is in cart
    suspend fun isProductInCart(itemName: String): Boolean

    // Save cart to local storage
    suspend fun saveCart(items: List<CartProduct>)

    // Load cart from local storage
    suspend fun loadCart(): List<CartProduct>
}