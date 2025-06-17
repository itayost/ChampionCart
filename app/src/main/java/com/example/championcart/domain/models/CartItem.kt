package com.example.championcart.domain.models

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null,
    val selectedStore: Store? = null
)

/**
 * Simple cart product for API requests
 * Matches: POST /cheapest-cart-all-chains request format
 */
data class CartProduct(
    val itemName: String,
    val quantity: Int
)

/**
 * Cheapest cart result from server
 * Matches: POST /cheapest-cart-all-chains response
 */
data class CheapestCartResult(
    val bestStore: Store,
    val totalPrice: Double,
    val worstPrice: Double,
    val savingsAmount: Double,
    val savingsPercent: Double,
    val city: String,
    val items: List<CartProduct>,
    val itemPrices: Map<String, Double>,
    val allStores: List<StoreOption>
) {
    // Helper properties for UI
    val savingsPercentage: Double
        get() = savingsPercent / 100.0

    val itemsBreakdown: List<CartItemBreakdown>
        get() = items.map { cartItem ->
            val unitPrice = itemPrices[cartItem.itemName] ?: 0.0
            CartItemBreakdown(
                itemName = cartItem.itemName,
                quantity = cartItem.quantity,
                price = unitPrice,
                totalPrice = unitPrice * cartItem.quantity
            )
        }
}

/**
 * Store option from cheapest cart API
 */
data class StoreOption(
    val chain: String,
    val storeId: String,
    val totalPrice: Double
) {
    fun toStore(name: String = Store.getDefaultStoreName(chain, storeId)): Store {
        return Store.fromChainAndStoreId(chain, storeId, name)
    }
}

/**
 * Cart item breakdown for UI display
 */
data class CartItemBreakdown(
    val itemName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)

/**
 * Saved cart from server
 * Matches: GET /savedcarts/{email} response
 */
data class SavedCart(
    val cartName: String,
    val city: String,
    val items: List<SavedCartItem>
)

/**
 * Saved cart item with price
 */
data class SavedCartItem(
    val itemName: String,
    val quantity: Int,
    val price: Double
)

/**
 * Save cart request
 * Matches: POST /save-cart request format
 */
data class SaveCartRequest(
    val cartName: String,
    val email: String,
    val city: String,
    val items: List<CartProduct>
)