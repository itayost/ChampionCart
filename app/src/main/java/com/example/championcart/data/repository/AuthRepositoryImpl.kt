package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.LoginRequest
import com.example.championcart.data.models.request.RegisterRequest
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.data.models.response.AuthResponse
import com.example.championcart.domain.models.Cart
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.User
import com.example.championcart.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ChampionCartApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    // Add isAuthenticated StateFlow
    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting registration for email: $email")
            val request = RegisterRequest(email = email, password = password)
            val response = api.register(request)

            Log.d(TAG, "Registration response - Code: ${response.code()}")
            Log.d(TAG, "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    Log.d(TAG, "Registration successful - Token: ${authResponse.accessToken?.take(20)}...")

                    // Save token
                    tokenManager.saveToken(authResponse.accessToken ?: "")
                    tokenManager.saveTokenType(authResponse.tokenType ?: "Bearer")

                    val user = User(
                        id = email, // Use email as ID for now
                        email = email,
                        token = authResponse.accessToken ?: "",
                        tokenType = authResponse.tokenType ?: "Bearer"
                    )

                    _isAuthenticated.value = true

                    Result.success(user)
                } else {
                    Log.e(TAG, "Registration response body is null")
                    Result.failure(Exception("Registration failed: Empty response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Registration failed - Code: ${response.code()}, Error: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Invalid email or password format"
                    409 -> "Email already registered"
                    else -> "Registration failed: ${response.code()}"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting login for email: $email")
            val request = LoginRequest(email = email, password = password)
            val response = api.login(request)

            Log.d(TAG, "Login response received")

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.accessToken != null) {
                    Log.d(TAG, "Login successful - Token received")

                    // Save token
                    tokenManager.saveToken(authResponse.accessToken)
                    tokenManager.saveTokenType(authResponse.tokenType ?: "Bearer")

                    val user = User(
                        id = email, // Use email as ID for now
                        email = email,
                        token = authResponse.accessToken,
                        tokenType = authResponse.tokenType ?: "Bearer"
                    )

                    _isAuthenticated.value = true

                    Result.success(user)
                } else {
                    Log.e(TAG, "Login response missing token")
                    Result.failure(Exception("Login failed: No token received"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Login failed - Error: $errorBody")

                val errorMessage = when {
                    errorBody?.contains("Invalid credentials") == true -> "Invalid email or password"
                    errorBody?.contains("User not found") == true -> "Account not found"
                    else -> "Login failed"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            Log.d(TAG, "Logging out")
            tokenManager.clearToken()
            _isAuthenticated.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout error", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                Log.d(TAG, "No token found")
                Result.success(null)
            } else {
                Log.d(TAG, "Validating token")
                val response = api.validateToken()

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val email = extractEmailFromToken(token)
                    val user = User(
                        id = email ?: "user",
                        email = email ?: "user@example.com",
                        token = token,
                        tokenType = tokenManager.getTokenType() ?: "Bearer"
                    )
                    _isAuthenticated.value = true
                    Result.success(user)
                } else {
                    Log.d(TAG, "Token validation failed")
                    tokenManager.clearToken()
                    _isAuthenticated.value = false
                    Result.success(null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get current user error", e)
            tokenManager.clearToken()
            _isAuthenticated.value = false
            Result.success(null)
        }
    }

    override suspend fun getAuthToken(): String? {
        return tokenManager.getToken()
    }

    private fun extractEmailFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                val gson = Gson()
                val claims = gson.fromJson(payload, Map::class.java)
                claims["sub"] as? String
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting email from token", e)
            null
        }
    }

    override suspend fun updateProfile(name: String?, phoneNumber: String?): Result<User> {
        // Not implemented in current API
        return Result.failure(Exception("Profile update not available"))
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        // Not implemented in current API
        return Result.failure(Exception("Password change not available"))
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        // Not implemented in current API
        return Result.failure(Exception("Password reset not available"))
    }

    override suspend fun deleteAccount(): Result<Unit> {
        // Not implemented in current API
        return Result.failure(Exception("Account deletion not available"))
    }

    override suspend fun getSavedCarts(): Result<List<Cart>> {
        return try {
            Log.d(TAG, "Fetching saved carts")
            val response = api.getSavedCarts()

            if (response.isSuccessful) {
                val cartResponses = response.body() ?: emptyList()
                Log.d(TAG, "Received ${cartResponses.size} saved carts")

                val carts = cartResponses.map { cartResponse ->
                    Cart(
                        name = cartResponse.name,
                        city = cartResponse.city,
                        items = cartResponse.items.map { item ->
                            CartItem(
                                productName = item.productName,
                                quantity = item.quantity,
                                price = item.price ?: 0.0
                            )
                        }
                    )
                }

                Result.success(carts)
            } else {
                Log.e(TAG, "Failed to fetch saved carts: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch saved carts"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching saved carts", e)
            Result.failure(e)
        }
    }

    override suspend fun saveCart(request: SaveCartRequest): Result<Unit> {
        return try {
            Log.d(TAG, "Saving cart: ${request.name}")
            val response = api.saveCart(request)

            if (response.isSuccessful) {
                Log.d(TAG, "Cart saved successfully")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to save cart: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to save cart"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cart", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCart(cartName: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting cart: $cartName")
            api.deleteCart(cartName)
            Log.d(TAG, "Cart deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting cart", e)
            Result.failure(e)
        }
    }
}