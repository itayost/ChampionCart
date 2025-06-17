package com.example.championcart.domain.models

import java.time.LocalDateTime

/**
 * User model matching auth API responses
 * From: POST /login and POST /register
 */
data class User(
    val id: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val preferences: UserPreferences = UserPreferences(),
    val isGuest: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null
) {
    val displayName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            !email.isBlank() -> email.substringBefore("@")
            else -> "User"
        }
}

/**
 * User preferences and settings
 */
data class UserPreferences(
    val defaultCity: String = "Tel Aviv",
    val language: Language = Language.HEBREW,
    val currency: Currency = Currency.ILS,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val priceAlertsEnabled: Boolean = true,
    val marketingEmailsEnabled: Boolean = false,
    val preferredStoreChains: List<String> = emptyList(),
    val dietaryRestrictions: List<DietaryRestriction> = emptyList(),
    val budgetAlerts: BudgetAlerts? = null
)

/**
 * Budget alert settings
 */
data class BudgetAlerts(
    val monthlyBudget: Double? = null,
    val weeklyBudget: Double? = null,
    val alertThreshold: Double = 0.8, // Alert when 80% of budget is reached
    val isEnabled: Boolean = true
)

/**
 * User statistics for profile screen
 */
data class UserStats(
    val totalSavings: Double = 0.0,
    val savingsThisMonth: Double = 0.0,
    val savingsThisYear: Double = 0.0,
    val totalComparisons: Int = 0,
    val comparisonsThisMonth: Int = 0,
    val averageSavingsPerCart: Double = 0.0,
    val favoriteStoreChain: String? = null,
    val totalCartsSaved: Int = 0,
    val activePriceAlerts: Int = 0
)

/**
 * Saved cart from server
 * Matches: GET /savedcarts/{email} response
 */
data class SavedCart(
    val cartName: String,
    val city: String,
    val items: List<SavedCartItem>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val totalPrice: Double
        get() = items.sumOf { it.price * it.quantity }

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

/**
 * Saved cart item with price
 */
data class SavedCartItem(
    val itemName: String,
    val quantity: Int,
    val price: Double
)

/**
 * Save cart request
 * Matches: POST /save-cart request format
 */
data class SaveCartRequest(
    val cartName: String,
    val email: String,
    val city: String,
    val items: List<CartProduct>
)

/**
 * Supported languages
 */
enum class Language {
    HEBREW, ENGLISH, ARABIC
}

/**
 * Supported currencies
 */
enum class Currency {
    ILS, USD, EUR
}

/**
 * Theme preferences
 */
enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

/**
 * Dietary restrictions
 */
enum class DietaryRestriction {
    KOSHER,
    HALAL,
    VEGETARIAN,
    VEGAN,
    GLUTEN_FREE,
    LACTOSE_FREE,
    NUT_FREE,
    DIABETIC_FRIENDLY
}