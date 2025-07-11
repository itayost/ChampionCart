package com.example.championcart.di

import com.example.championcart.domain.repository.*
import com.example.championcart.domain.usecase.auth.LoginUseCase
import com.example.championcart.domain.usecase.cart.*
import com.example.championcart.domain.usecase.city.GetCitiesUseCase
import com.example.championcart.domain.usecase.product.SearchProductsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchProductsUseCase(
        priceRepository: PriceRepository
    ): SearchProductsUseCase {
        return SearchProductsUseCase(priceRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCalculateCheapestStoreUseCase(
        cartRepository: CartRepository
    ): CalculateCheapestStoreUseCase {
        return CalculateCheapestStoreUseCase(cartRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveCartUseCase(
        cartRepository: CartRepository
    ): SaveCartUseCase {
        return SaveCartUseCase(cartRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSavedCartsUseCase(
        cartRepository: CartRepository
    ): GetSavedCartsUseCase {
        return GetSavedCartsUseCase(cartRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLoadSavedCartUseCase(
        cartRepository: CartRepository
    ): LoadSavedCartUseCase {
        return LoadSavedCartUseCase(cartRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCitiesUseCase(
        cityRepository: CityRepository
    ): GetCitiesUseCase {
        return GetCitiesUseCase(cityRepository)
    }
}