package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Response for grouped products from search endpoint
 * Can represent either a grouped product (with prices array) or a single product (with direct price fields)
 */
data class GroupedProductResponse(
    @SerializedName("item_code")
    val itemCode: String,
    @SerializedName("item_name")
    val itemName: String,

    // For grouped products:
    @SerializedName("prices")
    val prices: List<StorePriceResponse>? = null,
    @SerializedName("quantity")
    val quantity: String? = null,
    @SerializedName("unit_of_measure")
    val unitOfMeasure: String? = null,
    @SerializedName("manufacturer")
    val manufacturer: String? = null,
    @SerializedName("price_comparison")
    val priceComparison: PriceComparisonResponse? = null,

    // For single products (when returned directly from search):
    @SerializedName("chain")
    val chain: String? = null,
    @SerializedName("store_id")
    val storeId: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("store_name")
    val storeName: String? = null
)

/**
 * Store price information
 */
data class StorePriceResponse(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("store_name")
    val storeName: String? = null,
    @SerializedName("last_updated")
    val lastUpdated: String? = null
)

/**
 * Price comparison information
 */
data class PriceComparisonResponse(
    @SerializedName("best_deal")
    val bestDeal: PriceDealResponse? = null,
    @SerializedName("worst_deal")
    val worstDeal: PriceDealResponse? = null,
    @SerializedName("savings")
    val savings: Double? = null,
    @SerializedName("savings_percent")
    val savingsPercent: Double? = null,
    @SerializedName("identical_product")
    val identicalProduct: Boolean? = false,
    @SerializedName("price_range")
    val priceRange: PriceRangeResponse? = null
)

/**
 * Price deal information (best/worst)
 */
data class PriceDealResponse(
    @SerializedName("store")
    val store: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("city")
    val city: String? = null
)

/**
 * Price range information
 */
data class PriceRangeResponse(
    @SerializedName("min")
    val min: Double?,
    @SerializedName("max")
    val max: Double?,
    @SerializedName("avg")
    val avg: Double?
)

// Cart-related response classes moved to CartResponses.kt