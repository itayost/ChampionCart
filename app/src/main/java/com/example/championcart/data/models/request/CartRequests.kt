package com.example.championcart.data.models.request

import com.google.gson.annotations.SerializedName


/**
 * Cheapest cart request matching server API
 * POST /cheapest-cart-all-chains
 */
data class CheapestCartRequest(
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItem>
)

/**
 * Cart item for API requests
 */
data class CartItem(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * Save cart request matching server API
 * POST /save-cart
 */
data class SaveCartRequest(
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItem>
)