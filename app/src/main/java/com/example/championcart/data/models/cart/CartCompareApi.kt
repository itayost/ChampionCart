// In CartCompareModels.kt
package com.example.championcart.data.models.cart

import com.google.gson.annotations.SerializedName

data class CartCompareRequest(
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartCompareItem>
)

data class CartCompareItem(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("name")
    val name: String
)

data class CartCompareResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("city")
    val city: String,
    @SerializedName("cheapest_store")
    val cheapestStore: StoreComparison,
    @SerializedName("all_stores")
    val allStores: List<StoreComparison>,
    @SerializedName("comparison_time")
    val comparisonTime: String
)

data class StoreComparison(
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("branch_address")
    val branchAddress: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("chain_name")
    val chainName: String,
    @SerializedName("chain_display_name")
    val chainDisplayName: String,
    @SerializedName("available_items")
    val availableItems: Int,
    @SerializedName("missing_items")
    val missingItems: Int,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("items_detail")
    val itemsDetail: List<ItemDetail>
)

data class ItemDetail(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("available")
    val available: Boolean
)