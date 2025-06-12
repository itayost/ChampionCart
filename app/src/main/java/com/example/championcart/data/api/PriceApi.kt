package com.example.championcart.data.api

import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.data.models.response.CheapestCartResponse
import com.example.championcart.data.models.response.PriceItem
import retrofit2.Response
import retrofit2.http.*

interface PriceApi {
    @GET("prices/by-item/{city}/{item_name}")
    suspend fun searchProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("group_by_code") groupByCode: Boolean = true,
        @Query("limit") limit: Int = 50
    ): Response<List<PriceItem>>  // Changed from ProductSearchResponse

    @GET("prices/identical-products/{city}/{item_name}")
    suspend fun getIdenticalProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String
    ): Response<List<PriceItem>>

    @POST("cheapest-cart-all-chains")
    suspend fun findCheapestCart(
        @Body request: CheapestCartRequest
    ): Response<CheapestCartResponse>

    @GET("cities-list")
    suspend fun getCitiesList(): Response<List<String>>
}