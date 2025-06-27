package com.example.championcart.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_SELECTED_CITY = "selected_city"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LAUNCH = "first_launch"

        private const val DEFAULT_CITY = "תל אביב"
        private const val DEFAULT_LANGUAGE = "he"
    }

    fun getSelectedCity(): String {
        return sharedPreferences.getString(KEY_SELECTED_CITY, DEFAULT_CITY) ?: DEFAULT_CITY
    }

    fun setSelectedCity(city: String) {
        sharedPreferences.edit()
            .putString(KEY_SELECTED_CITY, city)
            .apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(language: String) {
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun getThemeMode(): ThemeMode {
        val mode = sharedPreferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(mode ?: ThemeMode.SYSTEM.name)
    }

    fun setThemeMode(mode: ThemeMode) {
        sharedPreferences.edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(isFirst: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, isFirst)
            .apply()
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}