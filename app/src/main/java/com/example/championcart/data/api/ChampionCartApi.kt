package com.example.championcart.data.api

import com.example.championcart.data.models.request.*
import com.example.championcart.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface ChampionCartApi {

    // ============ AUTHENTICATION ENDPOINTS ============

    /**
     * Register new user
     * POST /register
     * Request: {"email": "user@example.com", "password": "securepassword123"}
     * Response: {"message": "User registered successfully"}
     */
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    /**
     * Login user
     * POST /login
     * Request: {"email": "user@example.com", "password": "securepassword123"}
     * Response: {"access_token": "...", "token_type": "bearer"}
     */
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // ============ PRICE QUERY ENDPOINTS ============

    /**
     * Get all prices for a specific store
     * GET /prices/{db_name}/store/{snif_key}
     * Response: Array of products with snif_key, item_price fields
     */
    @GET("prices/{db_name}/store/{snif_key}")
    suspend fun getStoreProducts(
        @Path("db_name") dbName: String,      // "shufersal" or "victory"
        @Path("snif_key") snifKey: String     // Store identifier like "7290027600007-001-001"
    ): Response<List<ProductResponse>>

    /**
     * Get all prices for a specific item code
     * GET /prices/{db_name}/item_code/{item_code}
     * Response: Array of products with snif_key, item_price fields
     */
    @GET("prices/{db_name}/item_code/{item_code}")
    suspend fun getProductByItemCode(
        @Path("db_name") dbName: String,      // "shufersal" or "victory"
        @Path("item_code") itemCode: String   // Product barcode like "7290000042435"
    ): Response<List<ProductResponse>>

    /**
     * Search products by name and city
     * GET /prices/by-item/{city}/{item_name}
     * With grouping: Returns grouped products with nested prices array
     * Without grouping: Returns individual products with chain, store_id, price fields
     */
    @GET("prices/by-item/{city}/{item_name}")
    suspend fun searchProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("group_by_code") groupByCode: Boolean = true,
        @Query("limit") limit: Int? = null
    ): Response<List<GroupedProductResponse>>

    /**
     * Get identical products across chains
     * GET /prices/identical-products/{city}/{item_name}
     * Response: Grouped products that exist in multiple chains
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
     * Request: {"city": "Tel Aviv", "items": [{"item_name": "חלב", "quantity": 2}]}
     */
    @POST("cheapest-cart-all-chains")
    suspend fun findCheapestCart(
        @Body request: CheapestCartRequest
    ): Response<CheapestCartResponse>

    /**
     * Save cart for user
     * POST /save-cart
     * Request: {"cart_name": "Weekly Shopping", "email": "user@example.com", "city": "Tel Aviv", "items": [...]}
     */
    @POST("save-cart")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): Response<SaveCartResponse>

    /**
     * Get saved carts for user
     * GET /savedcarts/{email}?city={city}
     * Response: {"email": "...", "saved_carts": [...]}
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
     * Response: ["Afula", "Akko", "Arad", ...]
     */
    @GET("cities-list")
    suspend fun getCitiesList(): Response<List<String>>

    /**
     * Get cities with store counts
     * GET /cities-list-with-stores
     * Response: ["Tel Aviv: 45 shufersal, 12 victory", ...]
     */
    @GET("cities-list-with-stores")
    suspend fun getCitiesWithStores(): Response<List<String>>

    /**
     * Check API health
     * GET /health
     * Response: {"status": "healthy", "chains_available": {"shufersal": true, "victory": true}}
     */
    @GET("health")
    suspend fun checkHealth(): Response<ApiHealthResponse>

    /**
     * Get API information
     * GET /
     * Response: {"message": "Welcome to the Champion Cart API", "version": "1.1", "improvements": [...]}
     */
    @GET("/")
    suspend fun getApiInfo(): Response<ApiInfoResponse>
}