// File: app/src/main/java/com/example/championcart/ChampionCartApplication.kt
package com.example.championcart

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class ChampionCartApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setLocale(this, "he")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val locale = Locale("he", "IL")
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    companion object {
        fun setLocale(context: Context, languageCode: String) {
            val locale = when (languageCode) {
                "he" -> Locale("he", "IL")
                "en" -> Locale("en", "US")
                else -> Locale("he", "IL") // Default to Hebrew
            }

            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
}