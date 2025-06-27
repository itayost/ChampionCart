package com.example.championcart.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_GUEST = "is_guest"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_EMAIL)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_GUEST, isGuest)
            .apply()
    }

    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null || isGuestMode()
    }
}