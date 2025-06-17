package com.example.championcart.domain.repository

import com.example.championcart.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // REMOVED: All methods that require server endpoints that don't exist
    //
    // The Champion Cart server only provides:
    // 1. Authentication (login/register) - handled by AuthRepository
    // 2. Cart management (save/get saved carts) - handled by AuthRepository
    // 3. Product search and pricing - handled by PriceRepository
    //
    // The server does NOT provide:
    // - User statistics/analytics endpoints
    // - User preferences storage endpoints
    // - Profile management beyond email/password
    // - Savings tracking, favorite stores, price alerts
    //
    // If these features are needed, they should be:
    // 1. Implemented locally using SharedPreferences/Room
    // 2. Added to the server by the backend team
    // 3. This interface updated once server endpoints are available

    /**
     * Placeholder method - UserRepository currently has no server-backed features
     * All user management is handled by AuthRepository or should be local-only
     */
    suspend fun isServerBacked(): Boolean = false
}

/**
 * NOTE: This minimal interface reflects the actual server capabilities.
 *
 * The Champion Cart server does not provide user management endpoints
 * beyond basic authentication. All user features should be either:
 *
 * 1. Handled by AuthRepository (login, register, saved carts)
 * 2. Implemented locally in the app (preferences, stats, profile data)
 * 3. Added to the server in future updates
 */