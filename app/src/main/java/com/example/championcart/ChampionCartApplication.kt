package com.example.championcart

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Champion Cart
 * Required for Hilt dependency injection
 */
@HiltAndroidApp
class ChampionCartApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize any app-wide components here
        // Hilt will automatically handle dependency injection setup
    }
}