package com.example.championcart.data.models.request

/**
 * Search request parameters
 * For GET /prices/by-item/{city}/{item_name}
 */
data class SearchRequest(
    val city: String,
    val itemName: String,
    val groupByCode: Boolean = true,
    val limit: Int? = 50
)

/**
 * Identical products request parameters
 * For GET /prices/identical-products/{city}/{item_name}
 */
data class IdenticalProductsRequest(
    val city: String,
    val itemName: String,
    val limit: Int? = 50
)

/**
 * Store products request parameters
 * For GET /prices/{db_name}/store/{snif_key}
 */
data class StoreProductsRequest(
    val dbName: String,    // "shufersal" or "victory"
    val snifKey: String    // Store identifier
)

/**
 * Product by item code request parameters
 * For GET /prices/{db_name}/item_code/{item_code}
 */
data class ProductByItemCodeRequest(
    val dbName: String,    // "shufersal" or "victory"
    val itemCode: String   // Product barcode
)