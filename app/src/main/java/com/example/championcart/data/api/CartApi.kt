package com.example.championcart.data.api

import com.example.championcart.data.models.cart.*
import retrofit2.http.*

interface CartApi {

    // ===== SAVED CARTS ENDPOINTS =====

    @POST("api/saved-carts/save")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): SaveCartResponse

    @GET("api/saved-carts/list")
    suspend fun getSavedCarts(): SavedCartsListResponse

    @GET("api/saved-carts/{cart_id}")
    suspend fun getCartDetails(
        @Path("cart_id") cartId: Int
    ): CartDetailsResponse

    @GET("api/saved-carts/{cart_id}/compare")
    suspend fun compareSavedCart(  // Renamed from compareCart to avoid conflict
        @Path("cart_id") cartId: Int
    ): CompareCartResponse

    @DELETE("api/saved-carts/{cart_id}")
    suspend fun deleteCart(
        @Path("cart_id") cartId: Int
    ): DeleteCartResponse

    // ===== PRICE COMPARISON ENDPOINTS =====

    @POST("api/cart/compare")
    suspend fun compareCart(
        @Body request: CartCompareRequest
    ): CartCompareResponse

    // ===== CART SEARCH ENDPOINTS =====

    @GET("api/cart/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): CartSearchResponse

    @GET("api/cart/sample")
    suspend fun getSampleCart(): SampleCartResponse
}