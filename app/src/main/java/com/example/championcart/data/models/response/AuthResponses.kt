package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Login response matching server API
 * POST /login response
 */
data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String
)

/**
 * Register response matching server API
 * POST /register response
 */
data class RegisterResponse(
    @SerializedName("message")
    val message: String
)