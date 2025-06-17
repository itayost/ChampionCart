package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Login response - matches POST /login exactly
 * Server returns: {"access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", "token_type": "bearer"}
 */
data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String
)

/**
 * Register response - matches POST /register exactly
 * Server returns: {"message": "User registered successfully"}
 */
data class RegisterResponse(
    @SerializedName("message")
    val message: String
)

/**
 * Generic API error response - matches all error responses exactly
 * Server always returns: {"detail": "Error message describing what went wrong"}
 */
data class ApiErrorResponse(
    @SerializedName("detail")
    val detail: String
)