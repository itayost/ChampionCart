package com.example.championcart.data.api

import com.example.championcart.data.models.request.*
import com.example.championcart.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface ChampionCartApi {

    // Auth endpoints
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/validate")
    suspend fun validateToken(): Response<AuthResponse>

    // Search endpoints
    @GET("search/products")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("city") city: String? = null,
        @Query("limit") limit: Int = 20
    ): List<GroupedProductResponse>

    @GET("search/products-v2")
    suspend fun searchProductsV2(
        @Query("query") query: String,
        @Query("city") city: String? = null,
        @Query("store") store: String? = null,
        @Query("exact_match") exactMatch: Boolean = false
    ): List<GroupedProductResponse>

    // Cart endpoints
    @GET("carts")
    suspend fun getSavedCarts(): Response<List<CartResponse>>

    @POST("carts")
    suspend fun saveCart(@Body request: CartRequest): Response<Unit>

    @DELETE("carts/{name}")
    suspend fun deleteCart(@Path("name") name: String)

    @POST("carts/cheapest")
    suspend fun getCheapestCart(@Body request: CheapestCartRequest): CheapestCartResponse

    // City endpoints
    @GET("cities")
    suspend fun getCities(): List<String>
}