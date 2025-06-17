package com.example.championcart.data.local.preferences

import android.content.Context
import com.example.championcart.utils.Constants

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(Constants.KEY_AUTH_TOKEN).apply()
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString(Constants.KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }

    // ADDED: Missing method that AuthRepositoryImpl was trying to call
    fun clearUserEmail() {
        prefs.edit().remove(Constants.KEY_USER_EMAIL).apply()
    }

    fun saveSelectedCity(city: String) {
        prefs.edit().putString(Constants.KEY_SELECTED_CITY, city).apply()
    }

    fun getSelectedCity(): String {
        return prefs.getString(Constants.KEY_SELECTED_CITY, "Tel Aviv") ?: "Tel Aviv"
    }
}