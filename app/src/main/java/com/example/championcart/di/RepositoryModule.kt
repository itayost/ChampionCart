package com.example.championcart.di

import android.content.Context
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.repository.AuthRepositoryImpl
import com.example.championcart.data.repository.CartRepositoryImpl
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.data.repository.UserRepositoryImpl
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.UserRepository
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
    fun provideChampionCartApi(
        @ApplicationContext context: Context
    ): ChampionCartApi {
        NetworkModule.initialize(context)
        return NetworkModule.api
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ChampionCartApi,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            api = api,
            tokenManager = tokenManager
        )
    }

    @Provides
    @Singleton
    fun providePriceRepository(
        api: ChampionCartApi
    ): PriceRepository {
        return PriceRepositoryImpl(
            api = api
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
    fun provideCartRepository(
        @ApplicationContext context: Context,
        cartManager: CartManager,
        priceRepository: PriceRepository
    ): CartRepository {
        return CartRepositoryImpl(
            cartManager = cartManager,
            priceRepository = priceRepository
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        @ApplicationContext context: Context
    ): UserRepository {
        return UserRepositoryImpl()
    }
}