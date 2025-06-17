package com.example.championcart.domain.repository

interface UserRepository {
    suspend fun getUserStats(userId: String): Result<UserStats>
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit>
}

data class UserStats(
    val totalSavings: Double,
    val savingsThisMonth: Double,
    val savingsThisYear: Double,
    val comparisonsCount: Int
)

data class UserPreferences(
    val defaultCity: String,
    val language: String,
    val theme: String,
    val notificationsEnabled: Boolean
)