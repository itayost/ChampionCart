package com.example.championcart.data.api

import com.example.championcart.data.models.price.CitiesResponse
import retrofit2.http.GET

interface CityApi {

    @GET("api/cities")
    suspend fun getCities(): CitiesResponse
}