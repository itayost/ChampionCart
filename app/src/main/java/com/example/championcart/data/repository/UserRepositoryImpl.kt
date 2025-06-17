package com.example.championcart.data.repository

import com.example.championcart.domain.repository.UserRepository
import com.example.championcart.domain.repository.UserStats
import com.example.championcart.domain.repository.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    override suspend fun getUserStats(userId: String): Result<UserStats> {
        // Mock implementation for now
        return Result.success(
            UserStats(
                totalSavings = 1234.56,
                savingsThisMonth = 89.12,
                savingsThisYear = 567.89,
                comparisonsCount = 45
            )
        )
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        // Mock implementation for now
        return Result.success(Unit)
    }
}