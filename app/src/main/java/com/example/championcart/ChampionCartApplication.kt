package com.example.championcart

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChampionCartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}