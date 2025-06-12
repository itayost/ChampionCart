package com.example.championcart.utils

object Constants {
    // For Android emulator use 10.0.2.2 to access host machine's localhost
    // For physical device, use your computer's IP address on the same network
    // const val BASE_URL = "http://10.0.2.2:8000/"  // For emulator
    // const val BASE_URL = "http://192.168.1.xxx:8000/" // For physical device
    const val BASE_URL = "http://192.168.50.143:8000/" // Your current IP - WITH http://

    const val PREFS_NAME = "champion_cart_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_SELECTED_CITY = "selected_city"

    // Default values
    const val DEFAULT_CITY = "Tel Aviv"
    const val DEFAULT_SEARCH_LIMIT = 50

    // API timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}