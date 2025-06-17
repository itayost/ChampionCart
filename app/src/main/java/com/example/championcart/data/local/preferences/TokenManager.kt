package com.example.championcart.data.local.preferences

import android.content.Context
import com.example.championcart.utils.Constants

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // ============ AUTHENTICATION TOKEN METHODS ============

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(Constants.KEY_AUTH_TOKEN).apply()
    }

    // ============ USER EMAIL METHODS ============

    fun saveUserEmail(email: String) {
        prefs.edit().putString(Constants.KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }

    fun clearUserEmail() {
        prefs.edit().remove(Constants.KEY_USER_EMAIL).apply()
    }

    // ============ CITY SELECTION METHODS ============

    fun saveSelectedCity(city: String) {
        prefs.edit().putString(Constants.KEY_SELECTED_CITY, city).apply()
    }

    fun getSelectedCity(): String {
        return prefs.getString(Constants.KEY_SELECTED_CITY, "Tel Aviv") ?: "Tel Aviv"
    }

    // ============ RECENT SEARCHES METHODS ============

    /**
     * Save list of recent searches to SharedPreferences
     * Uses comma-separated format with escape characters for commas in search terms
     */
    fun saveRecentSearches(searches: List<String>) {
        val searchesJson = searches.joinToString(",") { it.replace(",", "\\,") }
        prefs.edit().putString(Constants.KEY_RECENT_SEARCHES, searchesJson).apply()
    }

    /**
     * Load recent searches from SharedPreferences
     * Returns empty list if no searches found
     */
    fun getRecentSearches(): List<String> {
        val searchesJson = prefs.getString(Constants.KEY_RECENT_SEARCHES, "") ?: ""
        return if (searchesJson.isBlank()) {
            emptyList()
        } else {
            searchesJson.split(",")
                .map { it.replace("\\,", ",") }
                .filter { it.isNotBlank() }
        }
    }

    /**
     * Add a new search term to recent searches
     * - Removes duplicate if already exists
     * - Adds to beginning of list
     * - Keeps only last 10 searches
     */
    fun addRecentSearch(search: String) {
        val cleanSearch = search.trim()
        if (cleanSearch.isBlank()) return

        val currentSearches = getRecentSearches().toMutableList()

        // Remove if already exists (to avoid duplicates)
        currentSearches.remove(cleanSearch)

        // Add to beginning
        currentSearches.add(0, cleanSearch)

        // Keep only last 10 searches
        val updatedSearches = currentSearches.take(10)

        saveRecentSearches(updatedSearches)
    }

    /**
     * Clear all recent searches
     */
    fun clearRecentSearches() {
        prefs.edit().remove(Constants.KEY_RECENT_SEARCHES).apply()
    }

    /**
     * Remove a specific search from recent searches
     */
    fun removeRecentSearch(search: String) {
        val currentSearches = getRecentSearches().toMutableList()
        currentSearches.remove(search)
        saveRecentSearches(currentSearches)
    }

    // ============ UTILITY METHODS ============

    /**
     * Clear all user data (useful for logout)
     */
    fun clearAllUserData() {
        prefs.edit().apply {
            remove(Constants.KEY_AUTH_TOKEN)
            remove(Constants.KEY_USER_EMAIL)
            remove(Constants.KEY_RECENT_SEARCHES)
            // Keep selected city for better UX
            apply()
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank() && !getUserEmail().isNullOrBlank()
    }
}