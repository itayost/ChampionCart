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
}