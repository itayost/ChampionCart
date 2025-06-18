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
import com.example.championcart.utils.Constants
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

            Log.d("AuthRepository", "Starting login request for email: $email")
            Log.d("AuthRepository", "API Base URL: ${Constants.BASE_URL}")

            val response = api.login(LoginRequest(email, password))

            Log.d("AuthRepository", "Response received - Code: ${response.code()}")
            Log.d("AuthRepository", "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    Log.d("AuthRepository", "Login successful - Token: ${authResponse.accessToken.take(20)}...")

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
                    Log.e("AuthRepository", "Empty response body")
                    _authState.value = AuthState.Unauthenticated
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Login failed - Code: ${response.code()}")
                Log.e("AuthRepository", "Error body: $errorBody")

                _authState.value = AuthState.Unauthenticated

                // Parse specific error messages
                val errorMessage = when (response.code()) {
                    401 -> "Invalid email or password"
                    404 -> "User not found"
                    500 -> "Server error. Please try again later"
                    else -> parseErrorMessage(errorBody)
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.message}", e)
            Log.e("AuthRepository", "Exception type: ${e.javaClass.simpleName}")

            _authState.value = AuthState.Unauthenticated

            // Better error messages for common issues
            val errorMessage = when {
                e is java.net.UnknownHostException -> "No internet connection"
                e is java.net.SocketTimeoutException -> "Connection timeout. Please try again"
                e is javax.net.ssl.SSLException -> "Secure connection failed"
                else -> e.message ?: "Unknown error occurred"
            }

            Result.failure(Exception(errorMessage))
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
        // Clear user email if available (TokenManager might not have this method)
        try {
            // If TokenManager has clearUserEmail method, use it
            val clearMethod = tokenManager::class.java.getMethod("clearUserEmail")
            clearMethod.invoke(tokenManager)
        } catch (e: Exception) {
            // Method might not exist, that's okay
            Log.d("AuthRepository", "TokenManager doesn't have clearUserEmail method")
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

    // ============ PRIVATE HELPER METHODS ============

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody.isNullOrEmpty()) {
                "An error occurred"
            } else {
                val errorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                errorResponse?.detail ?: "An error occurred"
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error parsing error message: ${e.message}")
            "An error occurred"
        }
    }
}