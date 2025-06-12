package com.example.championcart.data.repository

import com.example.championcart.data.api.AuthApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.LoginRequest
import com.example.championcart.data.models.request.RegisterRequest
import com.example.championcart.domain.models.User
import com.example.championcart.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Save token and email
                    tokenManager.saveToken(authResponse.accessToken)
                    tokenManager.saveUserEmail(email)

                    val user = User(email = email, token = authResponse.accessToken)
                    Result.success(user)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            val response = authApi.register(RegisterRequest(email, password))
            if (response.isSuccessful) {
                // Auto-login after registration
                login(email, password)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        tokenManager.clearToken()
    }

    override fun getCurrentUser(): User? {
        val email = tokenManager.getUserEmail()
        val token = tokenManager.getToken()
        return if (email != null && token != null) {
            User(email = email, token = token)
        } else {
            null
        }
    }
}