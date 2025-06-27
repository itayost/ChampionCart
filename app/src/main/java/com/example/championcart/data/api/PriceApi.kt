package com.example.championcart.data.api

import com.example.championcart.data.models.price.PriceSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PriceApi {

    @GET("api/prices/by-item/{city}/{item_name}")
    suspend fun searchProductPrices(
        @Path("city") city: String,
        @Path("item_name") itemName: String
    ): PriceSearchResponse
}