package com.example.championcart.utils

object Constants {
    const val BASE_URL = "http://172.20.10.13:8000/" // For Android emulator
    // For physical device on same network, use your computer's IP:
    // const val BASE_URL = "http://192.168.1.xxx:8000/"

    const val PREFS_NAME = "champion_cart_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_SELECTED_CITY = "selected_city"
}