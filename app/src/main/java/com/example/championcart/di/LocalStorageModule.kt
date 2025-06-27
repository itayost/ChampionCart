package com.example.championcart.di

import android.content.Context
import android.content.SharedPreferences
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {

    private const val PREFS_NAME = "champion_cart_prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        sharedPreferences: SharedPreferences
    ): TokenManager {
        return TokenManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        sharedPreferences: SharedPreferences
    ): PreferencesManager {
        return PreferencesManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideCartManager(): CartManager {
        return CartManager()
    }
}