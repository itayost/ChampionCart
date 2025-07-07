package com.example.championcart.data.models.product

import com.google.gson.annotations.SerializedName

// Product Search Response - matches /api/products/search
data class ProductSearchResponse(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("prices_by_store")
    val pricesByStore: List<StorePriceInfo>,
    @SerializedName("price_stats")
    val priceStats: PriceStats
)

data class StorePriceInfo(
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("branch_address")
    val branchAddress: String,
    @SerializedName("chain_id")
    val chainId: Int,
    @SerializedName("chain_name")
    val chainName: String,
    @SerializedName("chain_display_name")
    val chainDisplayName: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("is_cheapest")
    val isCheapest: Boolean
)

data class PriceStats(
    @SerializedName("min_price")
    val minPrice: Double,
    @SerializedName("max_price")
    val maxPrice: Double,
    @SerializedName("avg_price")
    val avgPrice: Double,
    @SerializedName("price_range")
    val priceRange: Double,
    @SerializedName("available_in_stores")
    val availableInStores: Int
)