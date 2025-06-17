package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Cities list response
 * GET /cities-list response
 */
data class CitiesListResponse(
    val cities: List<String>
)

/**
 * Cities with stores response
 * GET /cities-list-with-stores response
 */
data class CitiesWithStoresResponse(
    val citiesWithStores: List<String>
)

/**
 * API health response
 * GET /health response
 */
data class ApiHealthResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("chains_available")
    val chainsAvailable: Map<String, Boolean>
)

/**
 * API info response
 * GET / response
 */
data class ApiInfoResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("version")
    val version: String,
    @SerializedName("improvements")
    val improvements: List<String>
)