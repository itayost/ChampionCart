package com.example.championcart.utils

object Constants {
    // Server Configuration
    // For Android emulator use 10.0.2.2 to access host machine's localhost
    // For physical device, use your computer's IP address on the same network
    // const val BASE_URL = "http://10.0.2.2:8000/"  // For emulator
    // const val BASE_URL = "http://192.168.1.xxx:8000/" // For physical device
    const val BASE_URL = "https://price-comparison-production-3906.up.railway.app/" // Production server

    // SharedPreferences Configuration
    const val PREFS_NAME = "champion_cart_prefs"

    // Authentication Keys
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_EMAIL = "user_email"

    // User Preferences Keys
    const val KEY_SELECTED_CITY = "selected_city"
    const val KEY_RECENT_SEARCHES = "recent_searches"           // NEW: For search history

    // Optional: Additional preference keys for future use
    const val KEY_USER_LANGUAGE = "user_language"              // For language preference
    const val KEY_USER_THEME = "user_theme"                    // For theme preference
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"  // For notification settings
    const val KEY_FIRST_APP_LAUNCH = "first_app_launch"        // For onboarding

    // Default Values
    const val DEFAULT_CITY = "Tel Aviv"
    const val DEFAULT_SEARCH_LIMIT = 50
    const val MAX_RECENT_SEARCHES = 10                         // NEW: Limit for recent searches

    // API Configuration
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Search Configuration
    const val MIN_SEARCH_QUERY_LENGTH = 2                      // NEW: Minimum characters to search
    const val SEARCH_DEBOUNCE_DELAY = 300L                     // NEW: Delay for search input (ms)

    // Cart Configuration
    const val MAX_CART_ITEMS = 100                             // NEW: Maximum items in cart
    const val CART_SYNC_INTERVAL = 5000L                       // NEW: Cart sync interval (ms)
}