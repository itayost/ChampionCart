package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Generic API error response
 * Used for all error responses
 */
data class ApiErrorResponse(
    @SerializedName("detail")
    val detail: String
)

/**
 * Network error wrapper
 */
data class NetworkErrorResponse(
    val message: String,
    val code: Int? = null
)