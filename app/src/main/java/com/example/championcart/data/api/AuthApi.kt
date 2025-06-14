package com.example.championcart.data.api

import com.example.championcart.data.models.request.LoginRequest
import com.example.championcart.data.models.request.RegisterRequest
import com.example.championcart.data.models.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}