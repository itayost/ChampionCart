package com.example.championcart.domain.repository

import com.example.championcart.domain.models.*
import com.example.championcart.data.models.request.SaveCartRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Login user with email and password
     * Returns: Result<AuthResponse> matching server API
     */
    suspend fun login(email: String, password: String): Result<AuthResponse>

    /**
     * Register new user with email and password
     * Returns: Result<AuthResponse> matching server API
     */
    suspend fun register(email: String, password: String): Result<AuthResponse>

    /**
     * Logout current user
     * Clears local token storage
     */
    suspend fun logout()

    /**
     * Get current authenticated user
     * Returns: User if logged in, null if guest/logged out
     */
    suspend fun getCurrentUser(): User?

    /**
     * Check if user is currently authenticated
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Get current auth token
     */
    suspend fun getAuthToken(): String?

    /**
     * Save auth token to local storage
     */
    suspend fun saveAuthToken(token: AuthResponse)

    /**
     * Clear auth token from local storage
     */
    suspend fun clearAuthToken()

    /**
     * Observe authentication state changes
     */
    fun observeAuthState(): Flow<AuthState>

    /**
     * Refresh auth token if needed
     */
    suspend fun refreshToken(): Result<AuthResponse>

    /**
     * Get user profile information
     */
    suspend fun getUserProfile(): Result<User>

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(user: User): Result<User>

    /**
     * Get user saved carts
     * Matches: GET /savedcarts/{email}
     */
    suspend fun getUserSavedCarts(): Result<List<SavedCart>>

    /**
     * Save user cart
     * Matches: POST /save-cart
     * Uses data layer request model directly
     */
    suspend fun saveUserCart(request: SaveCartRequest): Result<Unit>

    // REMOVED: getUserStats() - server doesn't provide user statistics
    // REMOVED: updateUserPreferences() - server doesn't store preferences
}

/**
 * Authentication state
 */
sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Guest : AuthState()
    object Loading : AuthState()
}