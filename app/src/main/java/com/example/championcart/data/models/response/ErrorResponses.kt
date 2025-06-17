package com.example.championcart.data.models.response

/**
 * Network error wrapper for client-side errors
 */
data class NetworkErrorResponse(
    val message: String,
    val code: Int? = null
)