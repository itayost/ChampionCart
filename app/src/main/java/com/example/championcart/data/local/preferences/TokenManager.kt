package com.example.championcart.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREF_NAME = "champion_cart_secure_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        // Add new preference keys
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_SELECTED_CITY = "selected_city"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    private val prefs: SharedPreferences by lazy {
        try {
            // Try to use encrypted shared preferences
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular shared preferences if encryption fails
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveTokenType(tokenType: String) {
        prefs.edit().putString(KEY_TOKEN_TYPE, tokenType).apply()
    }

    fun getTokenType(): String? {
        return prefs.getString(KEY_TOKEN_TYPE, "Bearer")
    }

    fun saveRefreshToken(refreshToken: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_TOKEN_TYPE)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_EMAIL)
            .apply()
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }

    // New methods for user preferences

    fun saveUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun saveSelectedCity(city: String) {
        prefs.edit().putString(KEY_SELECTED_CITY, city).apply()
    }

    fun getSelectedCity(): String {
        return prefs.getString(KEY_SELECTED_CITY, "Tel Aviv") ?: "Tel Aviv"
    }

    fun saveLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return prefs.getString(KEY_LANGUAGE, "en")
    }

    fun saveTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    fun getTheme(): String? {
        return prefs.getString(KEY_THEME, "system")
    }

    fun saveNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
}