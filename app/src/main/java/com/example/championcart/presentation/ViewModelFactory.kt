package com.example.championcart.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.repository.AuthRepositoryImpl
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.di.NetworkModule
import com.example.championcart.domain.usecase.LoginUseCase
import com.example.championcart.domain.usecase.RegisterUseCase
import com.example.championcart.domain.usecase.SearchProductsUseCase
import com.example.championcart.presentation.screens.auth.LoginViewModel
import com.example.championcart.presentation.screens.auth.RegisterViewModel
import com.example.championcart.presentation.screens.cart.CartViewModel
import com.example.championcart.presentation.screens.search.SearchViewModel
import com.example.championcart.presentation.screens.home.HomeViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    init {
        try {
            NetworkModule.initialize(context.applicationContext)
        } catch (e: Exception) {
            // Already initialized, ignore
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context.applicationContext)
        val cartManager = CartManager.getInstance(context.applicationContext)
        val authRepository = AuthRepositoryImpl(NetworkModule.authApi, tokenManager)
        val priceRepository = PriceRepositoryImpl(NetworkModule.priceApi)

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val loginUseCase = LoginUseCase(authRepository)
                LoginViewModel(loginUseCase) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                val registerUseCase = RegisterUseCase(authRepository)
                RegisterViewModel(registerUseCase) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                val searchProductsUseCase = SearchProductsUseCase(priceRepository)
                SearchViewModel(searchProductsUseCase, tokenManager, cartManager) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartManager, tokenManager, priceRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(tokenManager, cartManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}