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
                username = email,  // Server expects "username" field
                password = password
            )

            // Login successful if we got an access token
            if (response.accessToken.isNotEmpty()) {
                // Save token and user info
                tokenManager.saveToken(response.accessToken)
                tokenManager.saveUserEmail(email)
                tokenManager.setGuestMode(false)

                Log.d(TAG, "Login successful, token saved")
                emit(Result.success(true))
            } else {
                Log.e(TAG, "Login failed: No access token received")
                emit(Result.failure(Exception("Login failed: No access token")))
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

            // Registration successful if we got a user_id
            if (response.userId > 0) {
                // After successful registration, we need to login to get the token
                Log.d(TAG, "Registration successful, now logging in to get token")

                try {
                    // Call login to get the token
                    val loginResponse = authApi.login(
                        username = email,  // Server expects "username" field
                        password = password
                    )

                    if (loginResponse.accessToken.isNotEmpty()) {
                        // Save token and user info
                        tokenManager.saveToken(loginResponse.accessToken)
                        tokenManager.saveUserEmail(email)
                        tokenManager.setGuestMode(false)

                        Log.d(TAG, "Auto-login after registration successful")
                        emit(Result.success(true))
                    } else {
                        Log.e(TAG, "Auto-login failed after registration: No token received")
                        emit(Result.failure(Exception("Registration successful but login failed. Please login manually.")))
                    }
                } catch (loginError: Exception) {
                    Log.e(TAG, "Auto-login error after registration", loginError)
                    emit(Result.failure(Exception("Registration successful but login failed. Please login manually.")))
                }
            } else {
                Log.e(TAG, "Registration failed: Invalid response")
                emit(Result.failure(Exception("Registration failed")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            val errorMessage = when {
                e.message?.contains("409") == true -> "User already exists"
                e.message?.contains("400") == true -> "Invalid registration data"
                else -> "Registration failed: ${e.message}"
            }
            emit(Result.failure(Exception(errorMessage)))
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