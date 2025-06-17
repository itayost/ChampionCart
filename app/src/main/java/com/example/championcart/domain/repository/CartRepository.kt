package com.example.championcart.domain.repository

import com.example.championcart.domain.models.*
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    /**
     * Get all cart items as Flow for reactive UI
     */
    fun getCartItems(): Flow<List<CartItem>>

    /**
     * Get cart items as list (one-time fetch)
     */
    suspend fun getCartItemsList(): List<CartItem>

    /**
     * Add product to cart
     */
    suspend fun addToCart(product: Product, quantity: Int = 1): Result<Unit>

    /**
     * Add cart item directly
     */
    suspend fun addCartItem(cartItem: CartItem): Result<Unit>

    /**
     * Update cart item quantity
     */
    suspend fun updateCartItemQuantity(itemId: String, quantity: Int): Result<Unit>

    /**
     * Remove item from cart by ID
     */
    suspend fun removeFromCart(itemId: String): Result<Unit>

    /**
     * Remove item from cart by product name
     */
    suspend fun removeFromCartByName(itemName: String): Result<Unit>

    /**
     * Clear all items from cart
     */
    suspend fun clearCart(): Result<Unit>

    /**
     * Get total items count in cart
     */
    fun getCartItemsCount(): Flow<Int>

    /**
     * Get current cart total count (one-time)
     */
    suspend fun getCurrentCartCount(): Int

    /**
     * Get cart total price using best prices
     */
    suspend fun getCartTotalPrice(): Double

    /**
     * Get cart total price for specific city
     */
    suspend fun getCartTotal(city: String): Double

    /**
     * Check if product is in cart
     */
    suspend fun isProductInCart(itemCode: String): Boolean

    /**
     * Check if product is in cart by name
     */
    suspend fun isProductInCartByName(itemName: String): Boolean

    /**
     * Get quantity of specific product in cart
     */
    suspend fun getProductQuantity(itemCode: String): Int

    /**
     * Save current cart state to local storage
     */
    suspend fun saveCartState(): Result<Unit>

    /**
     * Load cart state from local storage
     */
    suspend fun loadCartState(): Result<List<CartItem>>

    /**
     * Convert cart to API format for cheapest cart search
     */
    suspend fun getCartAsApiFormat(): List<CartProduct>

    /**
     * Apply cheapest cart result to current cart
     */
    suspend fun applyCheapestCartResult(result: CheapestCartResult): Result<Unit>

    /**
     * Get cart summary for display
     */
    suspend fun getCartSummary(): CartSummary

    /**
     * Observe cart changes for analytics
     */
    fun observeCartChanges(): Flow<CartChangeEvent>

    /**
     * Get cart items grouped by store
     */
    suspend fun getCartItemsByStore(): Map<Store, List<CartItem>>

    /**
     * Calculate potential savings with cheapest cart
     */
    suspend fun calculatePotentialSavings(city: String): Result<CartSavings>

    /**
     * Merge cart items with same product
     */
    suspend fun mergeCartItems(): Result<Unit>

    /**
     * Validate cart items (check if products still exist)
     */
    suspend fun validateCartItems(city: String): Result<CartValidationResult>

    /**
     * Create shopping list from cart
     */
    suspend fun createShoppingList(name: String): Result<SavedCart>

    /**
     * Load shopping list into cart
     */
    suspend fun loadShoppingList(savedCart: SavedCart): Result<Unit>
}

/**
 * Cart summary for UI display
 */
data class CartSummary(
    val totalItems: Int,
    val uniqueProducts: Int,
    val totalPrice: Double,
    val hasIncompleteItems: Boolean,
    val estimatedSavings: Double? = null
)

/**
 * Cart change events for analytics
 */
sealed class CartChangeEvent {
    data class ItemAdded(val item: CartItem) : CartChangeEvent()
    data class ItemRemoved(val itemId: String) : CartChangeEvent()
    data class ItemUpdated(val item: CartItem, val oldQuantity: Int) : CartChangeEvent()
    object CartCleared : CartChangeEvent()
    data class StoreSelected(val store: Store) : CartChangeEvent()
}

/**
 * Cart savings calculation
 */
data class CartSavings(
    val currentTotal: Double,
    val bestTotal: Double,
    val potentialSavings: Double,
    val savingsPercentage: Double,
    val bestStore: Store
)

/**
 * Cart validation result
 */
data class CartValidationResult(
    val validItems: List<CartItem>,
    val invalidItems: List<CartItem>,
    val updatedPrices: List<CartItem>,
    val unavailableItems: List<CartItem>
)

/**
 * Shopping cart state for persistence
 */
data class CartState(
    val items: List<CartItem>,
    val selectedCity: String,
    val lastUpdated: Long = System.currentTimeMillis()
)