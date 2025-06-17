package com.example.championcart.domain.models

/**
 * Product model matching server API response
 * From: GET /prices/by-item/{city}/{item_name}
 */
data class Product(
    val itemCode: String,
    val itemName: String,
    val chain: String,
    val storeId: String,
    val price: Double,
    val timestamp: String,
    val relevanceScore: Double? = null,
    val weight: Double? = null,
    val unit: String? = null,
    val pricePerUnit: Double? = null
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
 * Store model matching server API structure
 */
data class Store(
    val id: String,           // Generated: "chain-storeId"
    val chain: String,        // "shufersal", "victory"
    val storeId: String,      // "001", "052", etc.
    val name: String,         // Display name for UI
    val address: String       // Store address
) {
    companion object {
        /**
         * Create store from API chain/storeId response
         */
        fun fromChainAndStoreId(
            chain: String,
            storeId: String,
            name: String? = null,
            address: String = "Store Address"
        ): Store {
            return Store(
                id = "${chain}-${storeId}",
                chain = chain,
                storeId = storeId,
                name = name ?: getDefaultStoreName(chain, storeId),
                address = address
            )
        }

        private fun getDefaultStoreName(chain: String, storeId: String): String {
            return when (chain.lowercase()) {
                "shufersal" -> "Shufersal $storeId"
                "victory" -> "Victory $storeId"
                else -> "${chain.replaceFirstChar { it.uppercase() }} $storeId"
            }
        }
    }
}

/**
 * Cart item for UI with complete product information
 */
data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null,
    val selectedStore: Store? = null
) {
    fun getTotalPrice(): Double = price * quantity
}

/**
 * Cheapest cart result from server
 * Matches: POST /cheapest-cart-all-chains response EXACTLY
 */
data class CheapestCartResult(
    val chain: String,                    // Best store chain
    val storeId: String,                  // Best store ID
    val totalPrice: Double,               // Best total price
    val worstPrice: Double,               // Worst total price
    val savings: Double,                  // Amount saved (worst - best)
    val savingsPercent: Double,           // Percentage saved
    val city: String,                     // Search city
    val items: List<CartProduct>,         // Original items
    val itemPrices: Map<String, Double>,  // Individual item prices
    val allStores: List<StoreOption>      // All store options
) {
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

// Extension functions
fun Product.toCartItem(quantity: Int = 1): CartItem {
    return CartItem(
        id = "${itemCode}-${chain}-${storeId}",
        productId = itemCode,
        productName = itemName,
        price = price,
        quantity = quantity,
        selectedStore = Store.fromChainAndStoreId(chain, storeId)
    )
}

fun CartItem.toCartProduct(): CartProduct {
    return CartProduct(
        itemName = productName,
        quantity = quantity
    )
}