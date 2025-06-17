package com.example.championcart.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _userPreferences = MutableStateFlow(loadUserPreferences())

    override suspend fun getUserStats(userId: String): Result<UserStats> {
        return try {
            // Load stats from local storage or calculate from app usage
            val stats = loadUserStatsFromPrefs(userId)
            Log.d("UserRepository", "Loaded user stats for: $userId")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("UserRepository", "Get user stats error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        return try {
            saveUserPreferencesToPrefs(preferences)
            _userPreferences.value = preferences
            Log.d("UserRepository", "Updated user preferences")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update user preferences error", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserPreferences(): Result<UserPreferences> {
        return try {
            val preferences = loadUserPreferences()
            Result.success(preferences)
        } catch (e: Exception) {
            Log.e("UserRepository", "Get user preferences error", e)
            Result.failure(e)
        }
    }

    override fun observeUserPreferences(): Flow<UserPreferences> {
        return _userPreferences.asStateFlow()
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            saveUserProfileToPrefs(user)
            Log.d("UserRepository", "Updated user profile: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update user profile error", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val user = loadUserProfileFromPrefs(userId)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Get user profile error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserStats(userId: String, stats: UserStats): Result<Unit> {
        return try {
            saveUserStatsToPrefs(userId, stats)
            Log.d("UserRepository", "Updated user stats for: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update user stats error", e)
            Result.failure(e)
        }
    }

    override suspend fun incrementComparison(userId: String): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(
                totalComparisons = currentStats.totalComparisons + 1,
                comparisonsThisMonth = currentStats.comparisonsThisMonth + 1
            )
            saveUserStatsToPrefs(userId, updatedStats)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Increment comparison error", e)
            Result.failure(e)
        }
    }

    override suspend fun addSavings(userId: String, amount: Double): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(
                totalSavings = currentStats.totalSavings + amount,
                savingsThisMonth = currentStats.savingsThisMonth + amount,
                savingsThisYear = currentStats.savingsThisYear + amount,
                averageSavingsPerCart = if (currentStats.totalComparisons > 0) {
                    (currentStats.totalSavings + amount) / currentStats.totalComparisons
                } else amount
            )
            saveUserStatsToPrefs(userId, updatedStats)
            Log.d("UserRepository", "Added savings: ₪$amount for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Add savings error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateFavoriteStore(userId: String, storeChain: String): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(favoriteStoreChain = storeChain)
            saveUserStatsToPrefs(userId, updatedStats)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update favorite store error", e)
            Result.failure(e)
        }
    }

    override suspend fun incrementSavedCarts(userId: String): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(
                totalCartsSaved = currentStats.totalCartsSaved + 1
            )
            saveUserStatsToPrefs(userId, updatedStats)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Increment saved carts error", e)
            Result.failure(e)
        }
    }

    override suspend fun updatePriceAlerts(userId: String, count: Int): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(activePriceAlerts = count)
            saveUserStatsToPrefs(userId, updatedStats)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Update price alerts error", e)
            Result.failure(e)
        }
    }

    override suspend fun resetMonthlyStats(userId: String): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(
                savingsThisMonth = 0.0,
                comparisonsThisMonth = 0
            )
            saveUserStatsToPrefs(userId, updatedStats)
            Log.d("UserRepository", "Reset monthly stats for: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Reset monthly stats error", e)
            Result.failure(e)
        }
    }

    override suspend fun resetYearlyStats(userId: String): Result<Unit> {
        return try {
            val currentStats = loadUserStatsFromPrefs(userId)
            val updatedStats = currentStats.copy(savingsThisYear = 0.0)
            saveUserStatsToPrefs(userId, updatedStats)
            Log.d("UserRepository", "Reset yearly stats for: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Reset yearly stats error", e)
            Result.failure(e)
        }
    }

    // Private helper functions
    private fun loadUserPreferences(): UserPreferences {
        return try {
            val json = prefs.getString("user_preferences", null)
            if (json != null) {
                gson.fromJson(json, UserPreferences::class.java)
            } else {
                createDefaultPreferences()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Load preferences error, using defaults", e)
            createDefaultPreferences()
        }
    }

    private fun saveUserPreferencesToPrefs(preferences: UserPreferences) {
        val json = gson.toJson(preferences)
        prefs.edit().putString("user_preferences", json).apply()
    }

    private fun createDefaultPreferences(): UserPreferences {
        return UserPreferences(
            defaultCity = "Tel Aviv",
            language = Language.HEBREW,
            currency = Currency.ILS,
            theme = ThemePreference.SYSTEM,
            notificationsEnabled = true,
            priceAlertsEnabled = true,
            marketingEmailsEnabled = false,
            preferredStoreChains = emptyList(),
            dietaryRestrictions = emptyList(),
            budgetAlerts = null
        )
    }

    private fun loadUserStatsFromPrefs(userId: String): UserStats {
        return try {
            val json = prefs.getString("user_stats_$userId", null)
            if (json != null) {
                gson.fromJson(json, UserStats::class.java)
            } else {
                createDefaultStats()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Load stats error, using defaults", e)
            createDefaultStats()
        }
    }

    private fun saveUserStatsToPrefs(userId: String, stats: UserStats) {
        val json = gson.toJson(stats)
        prefs.edit().putString("user_stats_$userId", json).apply()
    }

    private fun createDefaultStats(): UserStats {
        return UserStats(
            totalSavings = 0.0,
            savingsThisMonth = 0.0,
            savingsThisYear = 0.0,
            totalComparisons = 0,
            comparisonsThisMonth = 0,
            averageSavingsPerCart = 0.0,
            favoriteStoreChain = null,
            totalCartsSaved = 0,
            activePriceAlerts = 0
        )
    }

    private fun loadUserProfileFromPrefs(userId: String): User? {
        return try {
            val json = prefs.getString("user_profile_$userId", null)
            if (json != null) {
                gson.fromJson(json, User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Load profile error", e)
            null
        }
    }

    private fun saveUserProfileToPrefs(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString("user_profile_${user.id}", json).apply()
    }
}