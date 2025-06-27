package com.example.championcart.di

import com.example.championcart.data.repository.AuthRepositoryImpl
import com.example.championcart.data.repository.CartRepositoryImpl
import com.example.championcart.data.repository.CityRepositoryImpl
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.data.repository.UserRepositoryImpl
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.CityRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        cityRepositoryImpl: CityRepositoryImpl
    ): CityRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}