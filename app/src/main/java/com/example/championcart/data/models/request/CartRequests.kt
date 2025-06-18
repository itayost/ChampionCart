package com.example.championcart.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * Request to save a cart
 */
data class CartRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("items")
    val items: List<CartItem>
)

/**
 * Request to save a cart (alias for backward compatibility)
 */
typealias SaveCartRequest = CartRequest

/**
 * Item in a cart (for requests)
 */
data class CartItem(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * Request for cheapest cart calculation
 */
data class CheapestCartRequest(
    @SerializedName("items")
    val items: List<CartItem>,
    @SerializedName("city")
    val city: String
)