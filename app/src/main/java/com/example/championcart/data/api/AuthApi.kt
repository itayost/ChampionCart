package com.example.championcart.data.api

import com.example.championcart.data.models.auth.LoginResponse
import com.example.championcart.data.models.auth.RegisterRequest
import com.example.championcart.data.models.auth.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,  // Note: server expects "username" not "email"
        @Field("password") password: String
    ): LoginResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
}