package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Single product response (without grouping)
 * GET /prices/by-item/{city}/{item_name} without group_by_code
 */
data class ProductResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("chain")
    val chain: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("relevance_score")
    val relevanceScore: Double? = null,
    @SerializedName("weight")
    val weight: Double? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("price_per_unit")
    val pricePerUnit: Double? = null
)

/**
 * Grouped product response (with grouping)
 * GET /prices/by-item/{city}/{item_name}?group_by_code=true
 */
data class GroupedProductResponse(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("prices")
    val prices: List<StorePriceResponse>,
    @SerializedName("cross_chain")
    val crossChain: Boolean,
    @SerializedName("relevance_score")
    val relevanceScore: Double? = null,
    @SerializedName("price_comparison")
    val priceComparison: PriceComparisonResponse? = null,
    @SerializedName("weight")
    val weight: Double? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("price_per_unit")
    val pricePerUnit: Double? = null
)

/**
 * Store price within grouped product
 */
data class StorePriceResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("original_name")
    val originalName: String,
    @SerializedName("timestamp")
    val timestamp: String
)

/**
 * Price comparison information
 */
data class PriceComparisonResponse(
    @SerializedName("best_deal")
    val bestDeal: PriceDealResponse,
    @SerializedName("worst_deal")
    val worstDeal: PriceDealResponse,
    @SerializedName("savings")
    val savings: Double,
    @SerializedName("savings_percent")
    val savingsPercent: Double,
    @SerializedName("identical_product")
    val identicalProduct: Boolean
)

/**
 * Individual price deal
 */
data class PriceDealResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("store_id")
    val storeId: String
)