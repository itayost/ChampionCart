package com.example.championcart.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val selectedChain: String? = null,
    val selectedPrice: Double? = null
)

class CartManager private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _cartItems = MutableStateFlow<List<CartItem>>(loadCart())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartCount = MutableStateFlow(calculateCartCount())
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: CartManager? = null

        fun getInstance(context: Context): CartManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CartManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun addToCart(itemCode: String, itemName: String, chain: String? = null, price: Double? = null) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.itemCode == itemCode }

        if (existingItem != null) {
            // Update quantity
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(
                quantity = existingItem.quantity + 1,
                selectedChain = chain ?: existingItem.selectedChain,
                selectedPrice = price ?: existingItem.selectedPrice
            )
        } else {
            // Add new item
            currentItems.add(
                CartItem(
                    itemCode = itemCode,
                    itemName = itemName,
                    quantity = 1,
                    selectedChain = chain,
                    selectedPrice = price
                )
            )
        }

        updateCart(currentItems)
    }

    fun removeFromCart(itemCode: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.itemCode == itemCode }
        updateCart(currentItems)
    }

    fun updateQuantity(itemCode: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(itemCode)
            return
        }

        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.itemCode == itemCode }
        if (index != -1) {
            currentItems[index] = currentItems[index].copy(quantity = quantity)
            updateCart(currentItems)
        }
    }

    fun clearCart() {
        updateCart(emptyList())
    }

    fun isInCart(itemCode: String): Boolean {
        return _cartItems.value.any { it.itemCode == itemCode }
    }

    fun getItemQuantity(itemCode: String): Int {
        return _cartItems.value.find { it.itemCode == itemCode }?.quantity ?: 0
    }

    private fun updateCart(items: List<CartItem>) {
        _cartItems.value = items
        _cartCount.value = calculateCartCount()
        saveCart(items)
    }

    private fun calculateCartCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    private fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString("cart_items", json).apply()
    }

    private fun loadCart(): List<CartItem> {
        val json = prefs.getString("cart_items", null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}