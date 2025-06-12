package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

data class SaveCartResponse(
    @SerializedName("message")
    val message: String
)

data class SavedCartsResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("saved_carts")
    val savedCarts: List<SavedCart>
)

data class SavedCart(
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<SavedCartItem>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("current_total")
    val currentTotal: Map<String, Double>? = null
)

data class SavedCartItem(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double = 0.0
)