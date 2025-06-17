package com.example.championcart.di

import android.content.Context
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.repository.AuthRepositoryImpl
import com.example.championcart.data.repository.CartRepositoryImpl
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.UserRepository
import com.example.championcart.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context
    ): AuthRepository {
        NetworkModule.initialize(context)
        return AuthRepositoryImpl(
            authApi = NetworkModule.authApi,
            tokenManager = com.example.championcart.data.local.preferences.TokenManager(context)
        )
    }

    @Provides
    @Singleton
    fun providePriceRepository(
        @ApplicationContext context: Context
    ): PriceRepository {
        NetworkModule.initialize(context)
        return PriceRepositoryImpl(
            priceApi = NetworkModule.priceApi
        )
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        @ApplicationContext context: Context,
        priceRepository: PriceRepository
    ): CartRepository {
        return CartRepositoryImpl(
            cartManager = CartManager.getInstance(context),
            priceRepository = priceRepository
        )
    }

    @Provides
    @Singleton
    fun provideCartManager(
        @ApplicationContext context: Context
    ): CartManager {
        return CartManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): com.example.championcart.data.local.preferences.TokenManager {
        return com.example.championcart.data.local.preferences.TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}