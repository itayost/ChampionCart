package com.example.championcart.data.models.price

import com.google.gson.annotations.SerializedName

// Response Models
data class PriceSearchResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<GroupedProductResponse>?,
    @SerializedName("message")
    val message: String?
)

data class GroupedProductResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("category")
    val category: String?,
    @SerializedName("prices")
    val prices: List<StorePriceResponse>
)

data class StorePriceResponse(
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("last_updated")
    val lastUpdated: String?
)

// City Response
data class CitiesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("cities")
    val cities: List<String>?
)