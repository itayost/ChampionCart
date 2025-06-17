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
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
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

                    // Save token and user info
                    saveAuthToken(AuthResponse(authResponse.accessToken, authResponse.tokenType))
                    tokenManager.saveUserEmail(email)

                    _authState.value = AuthState.Authenticated
                    Result.success(AuthResponse(authResponse.accessToken, authResponse.tokenType))
                } else {
                    _authState.value = AuthState.Unauthenticated
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.e("AuthRepository", "Login failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
            Log.e("AuthRepository", "Login error", e)
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
                val errorMessage = parseErrorMessage(errorBody)
                Log.e("AuthRepository", "Registration failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
            Log.e("AuthRepository", "Registration error", e)
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
            val email = tokenManager.getUserEmail()
            val token = tokenManager.getToken()

            if (email != null && token != null) {
                // Create user from stored information
                User(
                    id = generateUserId(email),
                    email = email,
                    isGuest = false,
                    preferences = loadUserPreferences(),
                    createdAt = LocalDateTime.now(), // Would be loaded from server in real app
                    lastLoginAt = LocalDateTime.now()
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
        val token = getAuthToken()
        return !token.isNullOrBlank()
    }

    override suspend fun getAuthToken(): String? {
        return tokenManager.getToken()
    }

    override suspend fun saveAuthToken(token: AuthResponse) {
        tokenManager.saveToken(token.accessToken)
    }

    override suspend fun clearAuthToken() {
        tokenManager.clearToken()
    }

    override fun observeAuthState(): Flow<AuthState> {
        return _authState.asStateFlow()
    }

    override suspend fun refreshToken(): Result<AuthResponse> {
        // Server doesn't have refresh token endpoint, so we'll return current token
        val currentToken = getAuthToken()
        return if (currentToken != null) {
            Result.success(AuthResponse(currentToken, "bearer"))
        } else {
            Result.failure(Exception("No token to refresh"))
        }
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
            Log.e("AuthRepository", "Get user profile error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            // Save user preferences locally (server doesn't have user profile endpoint)
            saveUserPreferences(user.preferences)

            // Save user email if changed
            if (user.email != tokenManager.getUserEmail()) {
                tokenManager.saveUserEmail(user.email)
            }

            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update user profile error", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserSavedCarts(): Result<List<SavedCart>> {
        return try {
            val userEmail = tokenManager.getUserEmail()
            if (userEmail.isNullOrBlank()) {
                return Result.failure(Exception("No authenticated user"))
            }

            val response = api.getSavedCarts(userEmail)

            if (response.isSuccessful) {
                val savedCartsResponse = response.body()
                if (savedCartsResponse != null) {
                    val savedCarts = savedCartsResponse.savedCarts.map { cartResponse ->
                        SavedCart(
                            cartName = cartResponse.cartName,
                            city = cartResponse.city,
                            items = cartResponse.items.map { item ->
                                SavedCartItem(
                                    itemName = item.itemName,
                                    quantity = item.quantity,
                                    price = item.price
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
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage))
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
                Log.d("AuthRepository", "Cart saved successfully: ${request.cartName}")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Save cart error", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserStats(): Result<UserStats> {
        return try {
            // Mock implementation since server doesn't have user stats endpoint
            val mockStats = UserStats(
                totalSavings = 1234.56,
                savingsThisMonth = 89.12,
                savingsThisYear = 567.89,
                totalComparisons = 45,
                comparisonsThisMonth = 12,
                averageSavingsPerCart = 27.65,
                favoriteStoreChain = "Shufersal",
                totalCartsSaved = 8,
                activePriceAlerts = 3
            )
            Result.success(mockStats)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get user stats error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        return try {
            saveUserPreferences(preferences)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update user preferences error", e)
            Result.failure(e)
        }
    }

    // Helper functions
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

    private fun generateUserId(email: String): String {
        return "user-${email.hashCode().toString().replace("-", "")}"
    }

    private fun loadUserPreferences(): UserPreferences {
        // Load from local storage (SharedPreferences)
        val selectedCity = tokenManager.getSelectedCity()

        return UserPreferences(
            defaultCity = selectedCity,
            language = Language.HEBREW, // Default to Hebrew
            currency = Currency.ILS,    // Default to ILS
            theme = ThemePreference.SYSTEM,
            notificationsEnabled = true,
            priceAlertsEnabled = true,
            marketingEmailsEnabled = false,
            preferredStoreChains = emptyList(),
            dietaryRestrictions = emptyList(),
            budgetAlerts = null
        )
    }

    private fun saveUserPreferences(preferences: UserPreferences) {
        // Save to local storage (SharedPreferences)
        tokenManager.saveSelectedCity(preferences.defaultCity)
        // Other preferences would be saved to a dedicated PreferencesManager
    }
}