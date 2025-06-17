package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * API health response - matches GET /health exactly
 * Server returns: {"status": "healthy", "chains_available": {"shufersal": true, "victory": true}}
 */
data class ApiHealthResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("chains_available")
    val chainsAvailable: Map<String, Boolean>
)

/**
 * API info response - matches GET / exactly
 * Server returns: {"message": "Welcome to the Champion Cart API", "version": "1.1", "improvements": [...]}
 */
data class ApiInfoResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("version")
    val version: String,
    @SerializedName("improvements")
    val improvements: List<String>
)