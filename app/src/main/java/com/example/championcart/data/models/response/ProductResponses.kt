package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Single product response - matches server responses for:
 * - GET /prices/{db_name}/store/{snif_key}
 * - GET /prices/{db_name}/item_code/{item_code}
 * - GET /prices/by-item/{city}/{item_name} (without grouping)
 */
data class ProductResponse(
    // Fields from store/item_code endpoints
    @SerializedName("snif_key")
    val snifKey: String? = null,
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("item_price")
    val itemPrice: Double? = null,
    @SerializedName("timestamp")
    val timestamp: String,

    // Fields from search endpoints (without grouping)
    @SerializedName("chain")
    val chain: String? = null,
    @SerializedName("store_id")
    val storeId: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("relevance_score")
    val relevanceScore: Double? = null,
    @SerializedName("weight")
    val weight: Double? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("price_per_unit")
    val pricePerUnit: Double? = null
) {
    // Helper to get actual price regardless of field name
    fun getActualPrice(): Double = price ?: itemPrice ?: 0.0

    // Helper to get actual store ID
    fun getActualStoreId(): String = storeId ?: snifKey ?: ""
}

/**
 * Grouped product response - matches server response for:
 * - GET /prices/by-item/{city}/{item_name}?group_by_code=true
 * - GET /prices/identical-products/{city}/{item_name}
 */
data class GroupedProductResponse(
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("item_name")
    val itemName: String,

    // For grouped products:
    @SerializedName("prices")
    val prices: List<StorePriceResponse>? = null,
    @SerializedName("price_comparison")
    val priceComparison: PriceComparisonResponse? = null,

    // For single products (direct fields):
    @SerializedName("chain")
    val chain: String? = null,
    @SerializedName("store_id")
    val storeId: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null,

    // Common fields:
    @SerializedName("cross_chain")
    val crossChain: Boolean? = false,
    @SerializedName("relevance_score")
    val relevanceScore: Double? = 0.0,
    @SerializedName("weight")
    val weight: Double? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("price_per_unit")
    val pricePerUnit: Double? = null,
    @SerializedName("multi_store")
    val multiStore: Boolean? = false,
    @SerializedName("store_count")
    val storeCount: Int? = 1,
    @SerializedName("chain_count")
    val chainCount: Int? = 1
)

/**
 * Store price within grouped product - matches server structure exactly
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
 * Price comparison information - matches server structure exactly
 */
data class PriceComparisonResponse(
    @SerializedName("best_deal")
    val bestDeal: PriceDealResponse?,
    @SerializedName("worst_deal")
    val worstDeal: PriceDealResponse?,
    @SerializedName("savings")
    val savings: Double?,
    @SerializedName("savings_percent")
    val savingsPercent: Double?,
    @SerializedName("identical_product")
    val identicalProduct: Boolean? = false,
    //@SerializedName("price_range")
    //val priceRange: PriceRangeResponse? = null
)

/**
 * Individual price deal - matches server structure exactly
 */
data class PriceDealResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("store_id")
    val storeId: String
)