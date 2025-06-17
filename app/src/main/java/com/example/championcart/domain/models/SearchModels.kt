package com.example.championcart.domain.models

/**
 * Grouped product with cross-chain pricing
 * From: GET /prices/by-item/{city}/{item_name}?group_by_code=true
 */
data class GroupedProduct(
    val itemCode: String,
    val itemName: String,
    val prices: List<StorePrice>,
    val crossChain: Boolean,
    val relevanceScore: Double? = null,
    val priceComparison: PriceComparison? = null,
    val weight: Double? = null,
    val unit: String? = null,
    val pricePerUnit: Double? = null
) {
    val lowestPrice: Double?
        get() = prices.minOfOrNull { it.price }

    val highestPrice: Double?
        get() = prices.maxOfOrNull { it.price }

    val savings: Double
        get() = priceComparison?.savings ?: 0.0
}

/**
 * Individual store price within a grouped product
 * Matches server API response format
 */
data class StorePrice(
    val chain: String,
    val storeId: String,
    val price: Double,
    val originalName: String,
    val timestamp: String
)

/**
 * Price comparison details
 * From server API grouped product response
 */
data class PriceComparison(
    val bestDeal: PriceDeal,
    val worstDeal: PriceDeal,
    val savings: Double,
    val savingsPercent: Double,
    val identicalProduct: Boolean
)

/**
 * Individual price deal
 */
data class PriceDeal(
    val chain: String,
    val price: Double,
    val storeId: String
)

/**
 * Search query parameters - only server-supported fields
 */
data class SearchQuery(
    val query: String,
    val city: String,
    val groupByCode: Boolean = true,
    val limit: Int? = 50
)