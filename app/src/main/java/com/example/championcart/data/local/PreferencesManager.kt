package com.example.championcart.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        // Existing keys
        private const val KEY_SELECTED_CITY = "selected_city"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_RECENT_SEARCHES = "recent_searches"

        // New keys for profile features
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_PRICE_ALERTS = "price_alerts"
        private const val KEY_NEW_DEALS = "new_deals"
        private const val KEY_CART_REMINDERS = "cart_reminders"
        private const val KEY_MONTHLY_SUMMARY = "monthly_summary"
        private const val KEY_SAVED_CARTS_COUNT = "saved_carts_count"
        private const val KEY_TOTAL_SAVINGS = "total_savings"
        private const val KEY_TRACKED_PRODUCTS_COUNT = "tracked_products_count"
        private const val KEY_FAVORITE_STORE = "favorite_store"

        // Default values
        private const val DEFAULT_CITY = "תל אביב"
        private const val DEFAULT_LANGUAGE = "he"
        private const val SEARCH_DELIMITER = "|"
        private const val MAX_RECENT_SEARCHES = 5
    }

    // City management
    fun getSelectedCity(): String {
        return sharedPreferences.getString(KEY_SELECTED_CITY, DEFAULT_CITY) ?: DEFAULT_CITY
    }

    fun setSelectedCity(city: String) {
        sharedPreferences.edit()
            .putString(KEY_SELECTED_CITY, city)
            .apply()
    }

    // Alias for consistency with ProfileViewModel
    fun saveSelectedCity(city: String) = setSelectedCity(city)

    // Language management
    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(language: String) {
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    // Alias for consistency with ProfileViewModel
    fun saveLanguage(language: String) = setLanguage(language)

    // Theme management
    fun getThemeMode(): ThemeMode {
        val mode = sharedPreferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(mode ?: ThemeMode.SYSTEM.name)
    }

    fun setThemeMode(mode: ThemeMode) {
        sharedPreferences.edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE_ENABLED, false)
    }

    fun saveDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE_ENABLED, enabled)
            .apply()
    }

    // Notification settings
    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }

    fun getPriceAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_PRICE_ALERTS, true)
    }

    fun savePriceAlertsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_PRICE_ALERTS, enabled)
            .apply()
    }

    fun getNewDealsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NEW_DEALS, true)
    }

    fun saveNewDealsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NEW_DEALS, enabled)
            .apply()
    }

    fun getCartRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_CART_REMINDERS, false)
    }

    fun saveCartRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_CART_REMINDERS, enabled)
            .apply()
    }

    fun getMonthlySummaryEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_MONTHLY_SUMMARY, true)
    }

    fun saveMonthlySummaryEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_MONTHLY_SUMMARY, enabled)
            .apply()
    }

    // First launch
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(isFirst: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, isFirst)
            .apply()
    }

    // Recent searches
    fun getRecentSearches(): List<String> {
        val searchesString = sharedPreferences.getString(KEY_RECENT_SEARCHES, "") ?: ""
        return if (searchesString.isEmpty()) {
            emptyList()
        } else {
            searchesString.split(SEARCH_DELIMITER).filter { it.isNotBlank() }
        }
    }

    fun addRecentSearch(search: String) {
        val currentSearches = getRecentSearches().toMutableList()

        // Remove if already exists (to move to front)
        currentSearches.remove(search)

        // Add to front
        currentSearches.add(0, search)

        // Keep only last N searches
        val trimmedSearches = currentSearches.take(MAX_RECENT_SEARCHES)

        // Save
        sharedPreferences.edit()
            .putString(KEY_RECENT_SEARCHES, trimmedSearches.joinToString(SEARCH_DELIMITER))
            .apply()
    }

    fun clearRecentSearches() {
        sharedPreferences.edit()
            .remove(KEY_RECENT_SEARCHES)
            .apply()
    }

    // Statistics and user data
    fun getSavedCartsCount(): Int {
        return sharedPreferences.getInt(KEY_SAVED_CARTS_COUNT, 0)
    }

    fun incrementSavedCartsCount() {
        val current = getSavedCartsCount()
        sharedPreferences.edit()
            .putInt(KEY_SAVED_CARTS_COUNT, current + 1)
            .apply()
    }

    fun getTotalSavings(): Double {
        return sharedPreferences.getFloat(KEY_TOTAL_SAVINGS, 0f).toDouble()
    }

    fun addToTotalSavings(amount: Double) {
        val current = getTotalSavings()
        sharedPreferences.edit()
            .putFloat(KEY_TOTAL_SAVINGS, (current + amount).toFloat())
            .apply()
    }

    fun getTrackedProductsCount(): Int {
        return sharedPreferences.getInt(KEY_TRACKED_PRODUCTS_COUNT, 0)
    }

    fun saveTrackedProductsCount(count: Int) {
        sharedPreferences.edit()
            .putInt(KEY_TRACKED_PRODUCTS_COUNT, count)
            .apply()
    }

    fun getFavoriteStore(): String? {
        return sharedPreferences.getString(KEY_FAVORITE_STORE, null)
    }

    fun saveFavoriteStore(store: String) {
        sharedPreferences.edit()
            .putString(KEY_FAVORITE_STORE, store)
            .apply()
    }

    // Clear methods
    fun clearUserData() {
        // Clear user-specific data while keeping app settings
        sharedPreferences.edit()
            .remove(KEY_SAVED_CARTS_COUNT)
            .remove(KEY_TOTAL_SAVINGS)
            .remove(KEY_TRACKED_PRODUCTS_COUNT)
            .remove(KEY_FAVORITE_STORE)
            .remove(KEY_RECENT_SEARCHES)
            .apply()
    }

    fun clearAll() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}