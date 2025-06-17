package com.example.championcart.domain.models

/**
 * Cheapest cart result from server
 * Matches: POST /cheapest-cart-all-chains response
 */
data class CheapestCartResult(
    val chain: String,
    val storeId: String,
    val totalPrice: Double,
    val worstPrice: Double,
    val savings: Double,
    val savingsPercent: Double,
    val city: String,
    val items: List<CartProduct>,
    val itemPrices: Map<String, Double>,
    val allStores: List<StoreOption>
) {
    // Helper properties for UI
    val bestStore: Store
        get() = Store.fromChainAndStoreId(chain, storeId)

    val savingsAmount: Double
        get() = savings

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
    fun toStore(name: String? = null): Store {
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