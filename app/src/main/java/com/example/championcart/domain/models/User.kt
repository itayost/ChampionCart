package com.example.championcart.domain.models

/**
 * User model for authentication and profile
 */
data class User(
    val id: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val isGuest: Boolean = false,
    val preferences: UserPreferences = UserPreferences()
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
 * User preferences and settings - Complete version matching UserRepositoryImpl
 */
data class UserPreferences(
    val defaultCity: String = "Tel Aviv",
    val language: Language = Language.ENGLISH,
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
 * Language options
 */
enum class Language {
    ENGLISH, HEBREW
}

/**
 * Currency options
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
 * Dietary restriction options
 */
enum class DietaryRestriction {
    VEGETARIAN, VEGAN, KOSHER, HALAL, GLUTEN_FREE
}

/**
 * Budget alert settings
 */
data class BudgetAlerts(
    val monthlyBudget: Double? = null,
    val weeklyBudget: Double? = null,
    val alertThreshold: Double = 0.8,
    val isEnabled: Boolean = true
)

/**
 * User statistics for profile screen - Complete version
 */
data class UserStats(
    val totalSavings: Double = 0.0,
    val savingsThisMonth: Double = 0.0,
    val savingsThisYear: Double = 0.0,
    val comparisonsCount: Int = 0,
    // Add missing properties that UserRepositoryImpl expects
    val totalComparisons: Int = 0,
    val comparisonsThisMonth: Int = 0,
    val averageSavingsPerCart: Double = 0.0,
    val favoriteStoreChain: String? = null,
    val totalCartsSaved: Int = 0,
    val activePriceAlerts: Int = 0
)