// data/models/request/AuthRequests.kt
package com.example.championcart.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * Login request matching server API
 * POST /login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

/**
 * Register request matching server API
 * POST /register
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)