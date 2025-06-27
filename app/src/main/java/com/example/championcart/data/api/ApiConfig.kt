package com.example.championcart.data.api

object ApiConfig {
    // Production URL - Update this with your actual server URL
    const val PRODUCTION_URL = "https://your-api-server.com/"

    // Development URLs
    const val LOCAL_EMULATOR_URL = "http://10.0.2.2:3000/" // Android emulator
    const val LOCAL_DEVICE_URL = "http://192.168.1.100:3000/" // Update with your IP

    // Current active URL
    const val BASE_URL = LOCAL_EMULATOR_URL // Change this based on your setup

    // API Endpoints
    object Endpoints {
        const val LOGIN = "api/auth/login"
        const val REGISTER = "api/auth/register"
        const val SEARCH_PRICES = "api/prices/by-item/{city}/{item_name}"
        const val CITIES = "api/cities"
        const val SAVE_CART = "api/carts/save"
        const val SAVED_CARTS = "api/carts/saved"
        const val CHEAPEST_CART = "api/cheapest-cart"
    }

    // Request timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}