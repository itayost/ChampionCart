package com.example.championcart.domain.repository

import com.example.championcart.domain.models.*

interface PriceRepository {
    /**
     * Search products by name and city
     * Matches: GET /prices/by-item/{city}/{item_name}
     */
    suspend fun searchProducts(
        city: String,
        productName: String,
        groupByCode: Boolean = true,
        limit: Int? = 50
    ): Result<List<GroupedProduct>>

    /**
     * Get identical products across chains
     * Matches: GET /prices/identical-products/{city}/{item_name}
     */
    suspend fun getIdenticalProducts(
        city: String,
        productName: String,
        limit: Int? = 50
    ): Result<List<GroupedProduct>>

    /**
     * Find cheapest cart across all chains
     * Matches: POST /cheapest-cart-all-chains
     */
    suspend fun findCheapestCart(
        city: String,
        items: List<CartProduct>
    ): Result<CheapestCartResult>

    /**
     * Get list of all available cities
     * Matches: GET /cities-list
     */
    suspend fun getCitiesList(): Result<List<String>>

    /**
     * Get cities with store counts
     * Matches: GET /cities-list-with-stores
     */
    suspend fun getCitiesWithStores(): Result<List<String>>

    /**
     * Get all prices for a specific store
     * Matches: GET /prices/{db_name}/store/{snif_key}
     */
    suspend fun getStoreProducts(
        dbName: String,  // "shufersal" or "victory"
        snifKey: String  // Store identifier
    ): Result<List<Product>>

    /**
     * Get all prices for a specific item code
     * Matches: GET /prices/{db_name}/item_code/{item_code}
     */
    suspend fun getProductByItemCode(
        dbName: String,  // "shufersal" or "victory"
        itemCode: String // Product barcode
    ): Result<List<Product>>

    /**
     * Advanced search with filters
     */
    suspend fun searchProductsWithFilters(
        query: SearchQuery,
        filters: SearchFilters
    ): Result<SearchResult>

    /**
     * Get product recommendations based on search history
     */
    suspend fun getRecommendations(
        city: String,
        limit: Int = 10
    ): Result<List<GroupedProduct>>

    /**
     * Get trending products in a city
     */
    suspend fun getTrendingProducts(
        city: String,
        limit: Int = 20
    ): Result<List<GroupedProduct>>

    /**
     * Get products on sale in a city
     */
    suspend fun getProductsOnSale(
        city: String,
        limit: Int = 50
    ): Result<List<GroupedProduct>>

    /**
     * Track product price over time
     */
    suspend fun getProductPriceHistory(
        itemCode: String,
        city: String,
        days: Int = 30
    ): Result<List<PriceHistoryPoint>>

    /**
     * Set up price alert for a product
     */
    suspend fun createPriceAlert(
        itemCode: String,
        city: String,
        targetPrice: Double,
        userEmail: String
    ): Result<PriceAlert>

    /**
     * Get user's price alerts
     */
    suspend fun getUserPriceAlerts(userEmail: String): Result<List<PriceAlert>>

    /**
     * Delete price alert
     */
    suspend fun deletePriceAlert(alertId: String): Result<Unit>
}

/**
 * Price history point for trends
 */
data class PriceHistoryPoint(
    val date: String,
    val price: Double,
    val store: Store
)

/**
 * Price alert model
 */
data class PriceAlert(
    val id: String,
    val itemCode: String,
    val itemName: String,
    val city: String,
    val targetPrice: Double,
    val currentPrice: Double,
    val isActive: Boolean,
    val createdAt: String,
    val userEmail: String
)

/**
 * API health check
 */
data class ApiHealth(
    val status: String,
    val chainsAvailable: Map<String, Boolean>
)

/**
 * Extended PriceRepository for additional features
 */
interface ExtendedPriceRepository : PriceRepository {
    /**
     * Check API health
     * Matches: GET /health
     */
    suspend fun checkApiHealth(): Result<ApiHealth>

    /**
     * Get API information
     * Matches: GET /
     */
    suspend fun getApiInfo(): Result<ApiInfo>

    /**
     * Batch search multiple products
     */
    suspend fun batchSearchProducts(
        city: String,
        productNames: List<String>
    ): Result<Map<String, List<GroupedProduct>>>

    /**
     * Compare product across specific stores
     */
    suspend fun compareProductAcrossStores(
        city: String,
        productName: String,
        storeChains: List<String>
    ): Result<GroupedProduct>
}

/**
 * API information model
 */
data class ApiInfo(
    val message: String,
    val version: String,
    val improvements: List<String>
)