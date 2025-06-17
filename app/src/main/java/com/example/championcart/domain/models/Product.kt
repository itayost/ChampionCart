package com.example.championcart.domain.models

/**
 * Product model matching server API response
 * From: GET /prices/by-item/{city}/{item_name} (without grouping)
 * From: GET /prices/{db_name}/store/{snif_key}
 * From: GET /prices/{db_name}/item_code/{item_code}
 */
data class Product(
    val itemCode: String,
    val itemName: String,
    val chain: String,
    val storeId: String,
    val price: Double?,
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
 * Cart item for UI with complete product information
 * This model includes UI-specific fields not provided by server
 */
data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null,        // UI-only field
    val selectedStore: Store? = null     // UI-only field
)