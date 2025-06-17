package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Cheapest cart response - matches POST /cheapest-cart-all-chains exactly
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
 * Cart item in cheapest cart response - matches server structure exactly
 */
data class CartItemResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * Store option in cheapest cart response - matches server structure exactly
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
 * Save cart response - matches POST /save-cart exactly
 * Server returns: {"message": "Cart saved successfully"}
 */
data class SaveCartResponse(
    @SerializedName("message")
    val message: String
)

/**
 * Saved carts response - matches GET /savedcarts/{email} exactly
 */
data class SavedCartsResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("saved_carts")
    val savedCarts: List<SavedCartResponse>
)

/**
 * Individual saved cart in the response - matches server structure exactly
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
 * Item in saved cart - includes price, matches server structure exactly
 */
data class SavedCartItemResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)