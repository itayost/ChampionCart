package com.example.championcart.domain.repository

import com.example.championcart.domain.models.User
import com.example.championcart.domain.models.UserPreferences
import com.example.championcart.domain.models.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * Get user statistics for analytics and profile display
     */
    suspend fun getUserStats(userId: String): Result<UserStats>

    /**
     * Update user preferences (theme, language, notifications, etc.)
     */
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit>

    /**
     * Get current user preferences
     */
    suspend fun getUserPreferences(): Result<UserPreferences>

    /**
     * Observe user preferences changes
     */
    fun observeUserPreferences(): Flow<UserPreferences>

    /**
     * Update user profile information
     */
    suspend fun updateUserProfile(user: User): Result<User>

    /**
     * Get user profile by ID
     */
    suspend fun getUserProfile(userId: String): Result<User>

    /**
     * Update user statistics
     */
    suspend fun updateUserStats(userId: String, stats: UserStats): Result<Unit>

    /**
     * Track user actions for analytics
     */
    suspend fun incrementComparison(userId: String): Result<Unit>

    /**
     * Add savings amount to user's total
     */
    suspend fun addSavings(userId: String, amount: Double): Result<Unit>

    /**
     * Update user's favorite store based on usage patterns
     */
    suspend fun updateFavoriteStore(userId: String, storeChain: String): Result<Unit>

    /**
     * Increment saved carts count
     */
    suspend fun incrementSavedCarts(userId: String): Result<Unit>

    /**
     * Update active price alerts count
     */
    suspend fun updatePriceAlerts(userId: String, count: Int): Result<Unit>

    /**
     * Reset monthly statistics (called at month start)
     */
    suspend fun resetMonthlyStats(userId: String): Result<Unit>

    /**
     * Reset yearly statistics (called at year start)
     */
    suspend fun resetYearlyStats(userId: String): Result<Unit>
}