package com.example.championcart.data.models.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)