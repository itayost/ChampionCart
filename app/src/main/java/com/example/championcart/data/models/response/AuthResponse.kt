package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String = "Bearer",
    @SerializedName("user_email")
    val userEmail: String
)