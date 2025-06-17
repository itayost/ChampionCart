package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.LoginRequest
import com.example.championcart.data.models.request.RegisterRequest
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.data.models.response.ApiErrorResponse
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.AuthState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ChampionCartApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            _authState.value = AuthState.Loading

            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    Log.d("AuthRepository", "Login successful for: $email")

                    // Create domain AuthResponse using server response
                    val domainAuthResponse = AuthResponse(
                        accessToken = authResponse.accessToken,
                        tokenType = authResponse.tokenType
                    )

                    // Save token and user info
                    saveAuthToken(domainAuthResponse)
                    tokenManager.saveUserEmail(email)

                    _authState.value = AuthState.Authenticated
                    Result.success(domainAuthResponse)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Login failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            _authState.value = AuthState.Unauthenticated
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<AuthResponse> {
        return try {
            _authState.value = AuthState.Loading

            val response = api.register(RegisterRequest(email, password))

            if (response.isSuccessful) {
                val registerResponse = response.body()
                if (registerResponse != null) {
                    Log.d("AuthRepository", "Registration successful for: $email")

                    // Auto-login after successful registration
                    return login(email, password)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Registration failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error", e)
            _authState.value = AuthState.Unauthenticated
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            clearAuthToken()
            _authState.value = AuthState.Unauthenticated
            Log.d("AuthRepository", "User logged out")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Logout error", e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val token = tokenManager.getToken()
            val email = tokenManager.getUserEmail()

            if (token != null && email != null) {
                // Create a basic user from stored info
                User(
                    id = "user_${email.hashCode()}",
                    email = email
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get current user error", e)
            null
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return tokenManager.getToken() != null
    }

    override suspend fun getAuthToken(): String? {
        return tokenManager.getToken()
    }

    override suspend fun saveAuthToken(token: AuthResponse) {
        tokenManager.saveToken(token.accessToken)
    }

    override suspend fun clearAuthToken() {
        tokenManager.clearToken()
        // Try to clear user email if method exists
        try {
            tokenManager.clearUserEmail()
        } catch (e: Exception) {
            // Method might not exist, just clear the token
            Log.d("AuthRepository", "clearUserEmail method not available, only token cleared")
        }
    }

    override fun observeAuthState(): Flow<AuthState> {
        return _authState.asStateFlow()
    }

    override suspend fun refreshToken(): Result<AuthResponse> {
        // The server API doesn't provide a refresh token endpoint
        return Result.failure(Exception("Token refresh not supported by server"))
    }

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                Result.success(currentUser)
            } else {
                Result.failure(Exception("No authenticated user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        // This would need a server endpoint to update user profile
        return Result.failure(Exception("User profile update not supported by server"))
    }

    override suspend fun getUserSavedCarts(): Result<List<SavedCart>> {
        return try {
            val email = tokenManager.getUserEmail()
            if (email != null) {
                val response = api.getSavedCarts(email)
                if (response.isSuccessful) {
                    val savedCartsResponse = response.body()
                    if (savedCartsResponse != null) {
                        val savedCarts = savedCartsResponse.savedCarts.map { cartResponse ->
                            SavedCart(
                                cartName = cartResponse.cartName,
                                city = cartResponse.city,
                                items = cartResponse.items.map { itemResponse ->
                                    SavedCartItem(
                                        itemName = itemResponse.itemName,
                                        quantity = itemResponse.quantity,
                                        price = itemResponse.price
                                    )
                                }
                            )
                        }
                        Result.success(savedCarts)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Get saved carts failed: ${parseErrorMessage(errorBody)}"))
                }
            } else {
                Result.failure(Exception("No authenticated user"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get saved carts error", e)
            Result.failure(e)
        }
    }

    override suspend fun saveUserCart(request: com.example.championcart.domain.models.SaveCartRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserCart(request: SaveCartRequest): Result<Unit> {
        return try {
            val response = api.saveCart(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Save cart failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Save cart error", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserStats(): Result<UserStats> {
        // This would need a server endpoint for user statistics
        return Result.success(
            UserStats(
                totalSavings = 0.0,
                savingsThisMonth = 0.0,
                savingsThisYear = 0.0,
                comparisonsCount = 0
            )
        )
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        // This would need a server endpoint to update preferences
        return Result.failure(Exception("User preferences update not supported by server"))
    }

    // ============ PRIVATE HELPER METHODS ============

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ApiErrorResponse::class.java)
                errorResponse.detail
            } else {
                "Unknown error"
            }
        } catch (e: Exception) {
            errorBody ?: "Unknown error"
        }
    }
}