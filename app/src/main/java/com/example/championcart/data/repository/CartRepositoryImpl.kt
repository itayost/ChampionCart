package com.example.championcart.data.repository

import com.example.championcart.data.local.CartItem
import com.example.championcart.data.local.CartManager
import com.example.championcart.domain.models.CartProduct
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartManager: CartManager,
    private val priceRepository: PriceRepository
) : CartRepository {

    override fun getCartItems(): Flow<List<CartProduct>> {
        return cartManager.cartItems.map { items ->
            items.map { cartItem ->
                CartProduct(
                    itemName = cartItem.itemName,
                    quantity = cartItem.quantity
                )
            }
        }
    }

    override suspend fun getCartItemsList(): List<CartProduct> {
        return cartManager.cartItems.value.map { cartItem ->
            CartProduct(
                itemName = cartItem.itemName,
                quantity = cartItem.quantity
            )
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        // Add to cart using CartManager
        for (i in 0 until quantity) {
            cartManager.addToCart(
                itemCode = product.itemCode,
                itemName = product.itemName,
                chain = product.chainName,
                price = product.price
            )
        }
    }

    override suspend fun updateCartItemQuantity(itemName: String, quantity: Int) {
        // Find the item by name
        val cartItem = cartManager.cartItems.value.find { it.itemName == itemName }
        cartItem?.let {
            cartManager.updateQuantity(it.itemCode, quantity)
        }
    }

    override suspend fun removeFromCart(itemName: String) {
        // Find the item by name and remove it
        val cartItem = cartManager.cartItems.value.find { it.itemName == itemName }
        cartItem?.let {
            cartManager.removeFromCart(it.itemCode)
        }
    }

    override suspend fun clearCart() {
        cartManager.clearCart()
    }

    override fun getCartItemsCount(): Flow<Int> {
        return cartManager.cartCount
    }

    override suspend fun getCartTotal(city: String): Double {
        val items = getCartItemsList()
        if (items.isEmpty()) return 0.0

        return try {
            val result = priceRepository.findCheapestCart(city, items)
            result.getOrNull()?.totalPrice ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    override suspend fun isProductInCart(itemName: String): Boolean {
        return cartManager.cartItems.value.any { it.itemName == itemName }
    }

    override suspend fun saveCart(items: List<CartProduct>) {
        // Clear and repopulate cart
        cartManager.clearCart()
        items.forEach { cartProduct ->
            // Since we don't have itemCode in CartProduct, we'll use empty string
            for (i in 0 until cartProduct.quantity) {
                cartManager.addToCart(
                    itemCode = "",
                    itemName = cartProduct.itemName,
                    chain = null,
                    price = null
                )
            }
        }
    }

    override suspend fun loadCart(): List<CartProduct> {
        // Cart is automatically loaded by CartManager
        return getCartItemsList()
    }
}