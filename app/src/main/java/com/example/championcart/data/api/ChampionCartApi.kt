package com.example.championcart.data.api

import com.example.championcart.data.models.request.*
import com.example.championcart.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface ChampionCartApi {

    // ============ AUTHENTICATION ENDPOINTS ============

    /**
     * Login user
     * POST /login
     */
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /**
     * Register new user
     * POST /register
     */
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // ============ PRICE QUERY ENDPOINTS ============

    /**
     * Get all prices for a specific store
     * GET /prices/{db_name}/store/{snif_key}
     */
    @GET("prices/{db_name}/store/{snif_key}")
    suspend fun getStoreProducts(
        @Path("db_name") dbName: String,      // "shufersal" or "victory"
        @Path("snif_key") snifKey: String     // Store identifier
    ): Response<List<ProductResponse>>

    /**
     * Get all prices for a specific item code
     * GET /prices/{db_name}/item_code/{item_code}
     */
    @GET("prices/{db_name}/item_code/{item_code}")
    suspend fun getProductByItemCode(
        @Path("db_name") dbName: String,      // "shufersal" or "victory"
        @Path("item_code") itemCode: String   // Product barcode
    ): Response<List<ProductResponse>>

    /**
     * Search products by name and city
     * GET /prices/by-item/{city}/{item_name}
     */
    @GET("prices/by-item/{city}/{item_name}")
    suspend fun searchProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("group_by_code") groupByCode: Boolean = true,
        @Query("limit") limit: Int? = null
    ): Response<List<GroupedProductResponse>>

    /**
     * Search products without grouping (returns individual products)
     * GET /prices/by-item/{city}/{item_name}?group_by_code=false
     */
    @GET("prices/by-item/{city}/{item_name}")
    suspend fun searchProductsUngrouped(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("group_by_code") groupByCode: Boolean = false,
        @Query("limit") limit: Int? = null
    ): Response<List<ProductResponse>>

    /**
     * Get identical products across chains
     * GET /prices/identical-products/{city}/{item_name}
     */
    @GET("prices/identical-products/{city}/{item_name}")
    suspend fun getIdenticalProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("limit") limit: Int? = null
    ): Response<List<GroupedProductResponse>>

    // ============ CART MANAGEMENT ENDPOINTS ============

    /**
     * Find cheapest cart across all chains
     * POST /cheapest-cart-all-chains
     */
    @POST("cheapest-cart-all-chains")
    suspend fun findCheapestCart(
        @Body request: CheapestCartRequest
    ): Response<CheapestCartResponse>

    /**
     * Save cart for user
     * POST /save-cart
     */
    @POST("save-cart")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): Response<SaveCartResponse>

    /**
     * Get saved carts for user
     * GET /savedcarts/{email}
     */
    @GET("savedcarts/{email}")
    suspend fun getSavedCarts(
        @Path("email") email: String,
        @Query("city") city: String? = null
    ): Response<SavedCartsResponse>

    // ============ UTILITY ENDPOINTS ============

    /**
     * Get list of all cities
     * GET /cities-list
     */
    @GET("cities-list")
    suspend fun getCitiesList(): Response<List<String>>

    /**
     * Get cities with store counts
     * GET /cities-list-with-stores
     */
    @GET("cities-list-with-stores")
    suspend fun getCitiesWithStores(): Response<List<String>>

    /**
     * Check API health
     * GET /health
     */
    @GET("health")
    suspend fun checkHealth(): Response<ApiHealthResponse>

    /**
     * Get API information
     * GET /
     */
    @GET("/")
    suspend fun getApiInfo(): Response<ApiInfoResponse>
}