package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.AuthApi
import com.example.championcart.data.local.TokenManager
import com.example.championcart.data.models.auth.LoginRequest
import com.example.championcart.data.models.auth.RegisterRequest
import com.example.championcart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun login(email: String, password: String): Flow<Result<Boolean>> = flow {
        try {
            Log.d(TAG, "Attempting login for email: $email")

            val response = authApi.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )

            if (response.success && response.token != null) {
                // Save token and user info
                tokenManager.saveToken(response.token)
                tokenManager.saveUserEmail(email)
                tokenManager.setGuestMode(false)

                Log.d(TAG, "Login successful, token saved")
                emit(Result.success(true))
            } else {
                Log.e(TAG, "Login failed: ${response.message}")
                emit(Result.failure(Exception(response.message ?: "Login failed")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Flow<Result<Boolean>> = flow {
        try {
            Log.d(TAG, "Attempting registration for email: $email")

            val response = authApi.register(
                RegisterRequest(
                    email = email,
                    password = password,
                    name = name
                )
            )

            if (response.success && response.token != null) {
                // Save token and user info
                tokenManager.saveToken(response.token)
                tokenManager.saveUserEmail(email)
                tokenManager.setGuestMode(false)

                Log.d(TAG, "Registration successful, token saved")
                emit(Result.success(true))
            } else {
                Log.e(TAG, "Registration failed: ${response.message}")
                emit(Result.failure(Exception(response.message ?: "Registration failed")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun logout() {
        Log.d(TAG, "Logging out user")
        tokenManager.clearToken()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    override fun setGuestMode(isGuest: Boolean) {
        tokenManager.setGuestMode(isGuest)
    }
}