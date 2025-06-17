package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Cheapest cart response matching server API exactly
 * POST /cheapest-cart-all-chains response
 */
data class CheapestCartResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("worst_price")
    val worstPrice: Double,
    @SerializedName("savings")
    val savings: Double,
    @SerializedName("savings_percent")
    val savingsPercent: Double,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItemResponse>,
    @SerializedName("item_prices")
    val itemPrices: Map<String, Double>,
    @SerializedName("all_stores")
    val allStores: List<StoreOptionResponse>
)

/**
 * Cart item in response
 */
data class CartItemResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * Store option in cheapest cart response
 */
data class StoreOptionResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("total_price")
    val totalPrice: Double
)

/**
 * Save cart response
 * POST /save-cart response
 */
data class SaveCartResponse(
    @SerializedName("message")
    val message: String
)

/**
 * Saved carts response
 * GET /savedcarts/{email} response
 */
data class SavedCartsResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("saved_carts")
    val savedCarts: List<SavedCartResponse>
)

/**
 * Individual saved cart
 */
data class SavedCartResponse(
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<SavedCartItemResponse>
)

/**
 * Saved cart item with price
 */
data class SavedCartItemResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)