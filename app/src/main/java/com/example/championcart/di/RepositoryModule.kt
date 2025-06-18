package com.example.championcart.di

import com.example.championcart.data.repository.AuthRepositoryImpl
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPriceRepository(
        priceRepositoryImpl: PriceRepositoryImpl
    ): PriceRepository

    // If PriceRepositoryImpl also implements CartRepository
    // Otherwise, you need to create CartRepositoryImpl
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        priceRepositoryImpl: PriceRepositoryImpl
    ): CartRepository
}