package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Cart response from API
 */
data class CartResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("items")
    val items: List<CartItemResponse>,
    @SerializedName("total")
    val total: Double? = null,
    @SerializedName("store")
    val store: String? = null,
    @SerializedName("city")
    val city: String? = null
)

/**
 * Cart item in response
 */
data class CartItemResponse(
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double? = null
)

/**
 * Cheapest cart calculation response
 */
data class CheapestCartResponse(
    @SerializedName("store")
    val store: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("total")
    val total: Double,
    @SerializedName("items")
    val items: List<CheapestCartItemResponse>
)

/**
 * Item in cheapest cart response
 */
data class CheapestCartItemResponse(
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("requested_quantity")
    val requestedQuantity: Int,
    @SerializedName("available_quantity")
    val availableQuantity: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("total_price")
    val totalPrice: Double
)