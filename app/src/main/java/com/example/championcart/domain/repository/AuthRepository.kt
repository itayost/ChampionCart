package com.example.championcart.domain.repository

import com.example.championcart.domain.models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
    fun logout()
    fun getCurrentUser(): User?
}