package com.example.championcart.data.api

import com.example.championcart.data.models.auth.AuthResponse
import com.example.championcart.data.models.auth.LoginRequest
import com.example.championcart.data.models.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse
}