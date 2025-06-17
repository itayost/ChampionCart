package com.example.championcart.domain.models

/**
 * Local-only user statistics - no server integration yet
 * Default/empty values until server implementation
 */
data class UserStats(
    val totalSavings: Double = 0.0,
    val itemsTracked: Int = 0,
    val cartsCreated: Int = 0,
    val averageSavings: Double = 0.0,
    val favoriteStore: String = "",
    val thisMonthSavings: Double = 0.0
)

/**
 * Local-only user preferences - stored in TokenManager/SharedPreferences
 * App-level settings only, no server sync
 */
data class UserPreferences(
    val defaultCity: String = "Tel Aviv",
    val language: Language = Language.ENGLISH,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val priceAlertsEnabled: Boolean = true,
    val emailNotifications: Boolean = false
)

/**
 * Language options for the app
 */
enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    HEBREW("עברית", "he")
}

/**
 * Theme preference options
 */
enum class ThemePreference(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Default")
}