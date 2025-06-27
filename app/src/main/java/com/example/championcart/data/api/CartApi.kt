package com.example.championcart.data.api

import com.example.championcart.data.models.cart.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CartApi {

    @POST("api/carts/save")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): SavedCartResponse

    @GET("api/carts/saved")
    suspend fun getSavedCarts(): SavedCartsListResponse

    @POST("api/cheapest-cart")
    suspend fun calculateCheapestCart(
        @Body request: CheapestCartRequest
    ): CheapestCartResponse
}