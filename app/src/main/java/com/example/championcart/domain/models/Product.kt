package com.example.championcart.domain.models

/**
 * Domain model for grouped product
 */
data class GroupedProduct(
    val itemCode: String,
    val itemName: String,
    val prices: List<StorePrice>,
    val quantity: String? = null,
    val unitOfMeasure: String? = null,
    val manufacturer: String? = null,
    val priceComparison: PriceComparison? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val weight: String? = null,
    val unit: String? = null,
    val pricePerUnit: Double? = null,
    val relevanceScore: Double = 1.0,
    val lowestPrice: Double? = null
) {
    // Computed property for backward compatibility
    val savings: Double
        get() = priceComparison?.savings ?: 0.0
}

/**
 * Single product (for backward compatibility and cart items)
 */
data class Product(
    val itemCode: String,
    val itemName: String,
    val chain: String,
    val storeId: String,
    val price: Double,
    val city: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val weight: String? = null,
    val unit: String? = null,
    val pricePerUnit: Double? = null,
    val relevanceScore: Double = 1.0,
    val lowestPrice: Double? = null
)

/**
 * Store price information
 */
data class StorePrice(
    val chain: String,
    val storeId: String,
    val price: Double,
    val city: String? = null,
    val storeName: String? = null,
    val lastUpdated: String? = null
)

/**
 * Price comparison between stores
 */
data class PriceComparison(
    val bestDeal: PriceDeal,
    val worstDeal: PriceDeal,
    val savings: Double,
    val savingsPercent: Double,
    val identicalProduct: Boolean = false,
    val priceRange: PriceRange? = null
)

/**
 * Price deal information
 */
data class PriceDeal(
    val store: String,
    val price: Double,
    val city: String? = null
)

/**
 * Price range information
 */
data class PriceRange(
    val min: Double,
    val max: Double,
    val avg: Double
)

/**
 * Shopping cart
 */
data class Cart(
    val name: String,
    val items: List<CartItem>,
    val total: Double = 0.0,
    val store: String? = null,
    val city: String? = null
) {
    fun toApiFormat(): List<CartProduct> {
        return items.map { item ->
            CartProduct(
                itemName = item.productName,
                quantity = item.quantity
            )
        }
    }
}

/**
 * Item in a cart
 */
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val productName: String,
    val quantity: Int,
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val selectedStore: StoreInfo? = null
)

/**
 * Store information for cart items
 */
data class StoreInfo(
    val chain: String,
    val storeId: String,
    val storeName: String? = null,
    val city: String? = null
)

/**
 * Cart product for API format
 */
data class CartProduct(
    val itemName: String,
    val quantity: Int
)

/**
 * Result of cheapest cart calculation
 */
data class CheapestCart(
    val store: String,
    val city: String,
    val total: Double,
    val items: List<CheapestCartItem>
)

/**
 * Item in cheapest cart result
 */
data class CheapestCartItem(
    val itemCode: String,
    val itemName: String,
    val requestedQuantity: Int,
    val availableQuantity: Int,
    val price: Double,
    val totalPrice: Double
)