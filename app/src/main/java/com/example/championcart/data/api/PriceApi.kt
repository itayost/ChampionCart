package com.example.championcart.data.api

import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.data.models.response.CheapestCartResponse
import com.example.championcart.data.models.response.PriceItem
import retrofit2.Response
import retrofit2.http.*

interface PriceApi {
    // Search products - returns array of products with nested prices
    @GET("prices/by-item/{city}/{item_name}")
    suspend fun searchProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String,
        @Query("group_by_code") groupByCode: Boolean = true,
        @Query("limit") limit: Int = 50
    ): Response<List<PriceItem>>

    // Get identical products across chains
    @GET("prices/identical-products/{city}/{item_name}")
    suspend fun getIdenticalProducts(
        @Path("city") city: String,
        @Path("item_name") itemName: String
    ): Response<List<PriceItem>>

    // Find cheapest cart
    @POST("cheapest-cart-all-chains")
    suspend fun findCheapestCart(
        @Body request: CheapestCartRequest
    ): Response<CheapestCartResponse>

    // Get cities list
    @GET("cities-list")
    suspend fun getCitiesList(): Response<List<String>>

    // Get cities with store counts
    @GET("cities-list-with-stores")
    suspend fun getCitiesWithStores(): Response<List<String>>
}