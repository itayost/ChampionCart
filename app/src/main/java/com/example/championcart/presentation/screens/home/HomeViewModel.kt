package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.di.NetworkModule
import com.example.championcart.domain.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class QuickStats(
    val totalSaved: Double = 0.0,
    val itemsTracked: Int = 0,
    val cheapestStore: String = "Loading...",
    val savingsPercentage: Double = 0.0
)

data class FeaturedDeal(
    val productName: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val storeName: String,
    val savingsPercentage: Double,
    val imageUrl: String? = null
)

data class RecentComparison(
    val productName: String,
    val lowestPrice: Double,
    val highestPrice: Double,
    val bestStore: String,
    val comparedAt: String
)

data class HomeUiState(
    val userName: String = "Guest",
    val selectedCity: String = "Tel Aviv",
    val quickStats: QuickStats = QuickStats(),
    val featuredDeals: List<FeaturedDeal> = emptyList(),
    val recentComparisons: List<RecentComparison> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val itemsInCart: Int = 0,
    val greeting: String = "Welcome back"
)

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val cartManager: CartManager,
    private val priceRepository: PriceRepositoryImpl = PriceRepositoryImpl(NetworkModule.priceApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        observeCart()
        loadDashboardData()
    }

    private fun loadUserData() {
        val email = tokenManager.getUserEmail()
        val city = tokenManager.getSelectedCity()

        val greeting = getGreeting()
        val userName = email?.substringBefore("@") ?: "Guest"

        _uiState.value = _uiState.value.copy(
            userName = userName,
            selectedCity = city,
            greeting = greeting
        )
    }

    private fun getGreeting(): String {
        val hour = LocalDateTime.now().hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartManager.cartCount.collect { count ->
                _uiState.value = _uiState.value.copy(itemsInCart = count)
            }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Load quick stats (mock data for now)
                val quickStats = QuickStats(
                    totalSaved = 234.50,
                    itemsTracked = 47,
                    cheapestStore = "Rami Levy",
                    savingsPercentage = 18.5
                )

                // Load featured deals (mock data)
                val featuredDeals = listOf(
                    FeaturedDeal(
                        productName = "Tnuva Milk 3%",
                        originalPrice = 7.90,
                        discountedPrice = 5.90,
                        storeName = "Shufersal",
                        savingsPercentage = 25.3
                    ),
                    FeaturedDeal(
                        productName = "Osem Bamba 80g",
                        originalPrice = 5.90,
                        discountedPrice = 3.90,
                        storeName = "Victory",
                        savingsPercentage = 33.9
                    ),
                    FeaturedDeal(
                        productName = "Coca Cola 1.5L",
                        originalPrice = 8.90,
                        discountedPrice = 6.90,
                        storeName = "Rami Levy",
                        savingsPercentage = 22.5
                    )
                )

                // Load recent comparisons (mock data)
                val recentComparisons = listOf(
                    RecentComparison(
                        productName = "White Bread",
                        lowestPrice = 5.90,
                        highestPrice = 8.90,
                        bestStore = "Rami Levy",
                        comparedAt = "2 hours ago"
                    ),
                    RecentComparison(
                        productName = "Eggs 12 pack",
                        lowestPrice = 12.90,
                        highestPrice = 15.90,
                        bestStore = "Victory",
                        comparedAt = "Yesterday"
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    quickStats = quickStats,
                    featuredDeals = featuredDeals,
                    recentComparisons = recentComparisons
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onCityChange(city: String) {
        tokenManager.saveSelectedCity(city)
        _uiState.value = _uiState.value.copy(selectedCity = city)
        loadDashboardData() // Reload data for new city
    }

    fun refresh() {
        loadDashboardData()
    }
}