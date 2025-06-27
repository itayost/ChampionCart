package com.example.championcart.data.models.cart

import com.google.gson.annotations.SerializedName

// Request Models
data class SaveCartRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("items")
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("category")
    val category: String?
)

data class CheapestCartRequest(
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItemRequest>
)

// Response Models
data class SavedCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("cart_id")
    val cartId: String?
)

data class SavedCartsListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("carts")
    val carts: List<SavedCart>?
)

data class SavedCart(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("items")
    val items: List<CartItemResponse>,
    @SerializedName("created_at")
    val createdAt: String
)

data class CartItemResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("category")
    val category: String?
)

data class CheapestCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: CheapestCartData?,
    @SerializedName("message")
    val message: String?
)

data class CheapestCartData(
    @SerializedName("cheapest_store")
    val cheapestStore: String,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("store_totals")
    val storeTotals: Map<String, Double>,
    @SerializedName("missing_items")
    val missingItems: List<String>?
)