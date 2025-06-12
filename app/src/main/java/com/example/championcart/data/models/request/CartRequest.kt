package com.example.championcart.data.models.request

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("item_name")
    val itemName: String,
    val quantity: Int
)

data class CheapestCartRequest(
    val city: String,
    val items: List<CartItem>
)

data class SaveCartRequest(
    @SerializedName("cart_name")
    val cartName: String,
    val email: String,
    val city: String,
    val items: List<CartItem>
)