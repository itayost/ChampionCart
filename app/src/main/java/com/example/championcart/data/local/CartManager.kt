package com.example.championcart.data.local

import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Update quantity if item already exists
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(
                quantity = existingItem.quantity + quantity
            )
        } else {
            // Add new item
            currentItems.add(
                CartItem(
                    product = product,
                    quantity = quantity
                )
            )
        }

        _cartItems.value = currentItems
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { item ->
            item.product.bestPrice * item.quantity
        }
    }

    fun getItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}