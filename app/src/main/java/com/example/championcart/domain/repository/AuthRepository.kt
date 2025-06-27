package com.example.championcart.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Result<Boolean>>
    suspend fun register(email: String, password: String, name: String): Flow<Result<Boolean>>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun setGuestMode(isGuest: Boolean)
}