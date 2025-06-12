package com.example.championcart.data.api

import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.data.models.response.SaveCartResponse
import com.example.championcart.data.models.response.SavedCartsResponse
import retrofit2.Response
import retrofit2.http.*

interface CartApi {
    @POST("save-cart")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): Response<SaveCartResponse>

    @GET("savedcarts/{email}")
    suspend fun getSavedCarts(
        @Path("email") email: String,
        @Query("city") city: String? = null
    ): Response<SavedCartsResponse>
}