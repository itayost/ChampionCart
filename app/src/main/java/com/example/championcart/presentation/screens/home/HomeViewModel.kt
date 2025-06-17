package com.example.championcart.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.AuthState
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * ViewModel for the modern home screen
 * Manages city selection, featured deals, and recent comparisons
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        loadInitialData()
        observeUserData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load cities
            loadCities()

            // Load user data
            loadUserData()

            // Load featured deals and recent comparisons
            loadHomeContent()

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> loadUserData()
                    else -> _state.update {
                        it.copy(
                            userName = null,
                            totalSaved = 0.0,
                            itemsCompared = 0
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadCities() {
        _state.update { it.copy(isLoadingCities = true) }

        priceRepository.getCitiesWithStores().fold(
            onSuccess = { cities ->
                _state.update {
                    it.copy(
                        availableCities = cities,
                        isLoadingCities = false
                    )
                }
            },
            onFailure = { error ->
                _state.update {
                    it.copy(
                        isLoadingCities = false,
                        error = error.message
                    )
                }
            }
        )
    }

    private suspend fun loadUserData() {
        authRepository.getCurrentUser()?.let { user ->
            _state.update {
                it.copy(
                    userName = user.email.substringBefore("@"),
                    isGuest = user.isGuest
                )
            }
        }

        // Load saved carts to calculate total saved
        authRepository.getUserSavedCarts().fold(
            onSuccess = { savedCarts ->
                val totalSaved = savedCarts.sumOf { cart ->
                    cart.items.sumOf { item ->
                        // Calculate savings per item
                        // This is a simplified calculation
                        item.quantity * 2.5 // Average saving per item
                    }
                }

                val totalItems = savedCarts.sumOf { cart ->
                    cart.items.size
                }

                _state.update {
                    it.copy(
                        totalSaved = totalSaved,
                        itemsCompared = totalItems
                    )
                }
            },
            onFailure = { /* Handle error */ }
        )
    }

    private suspend fun loadHomeContent() {
        val currentCity = _state.value.selectedCity

        // Load featured deals (products with high savings)
        priceRepository.searchProducts(
            city = currentCity,
            productName = "חלב", // Example search
            groupByCode = true,
            limit = 10
        ).fold(
            onSuccess = { products ->
                // Filter products with good savings
                val dealsWithSavings = products.filter { product ->
                    product.savings > 0
                }.sortedByDescending { it.savings }
                    .take(5)

                _state.update {
                    it.copy(featuredDeals = dealsWithSavings)
                }
            },
            onFailure = { /* Handle error */ }
        )

        // For demo: simulate recent comparisons
        // In real app, this would come from local storage
        delay(100)

        priceRepository.searchProducts(
            city = currentCity,
            productName = "לחם",
            groupByCode = true,
            limit = 5
        ).fold(
            onSuccess = { products ->
                _state.update {
                    it.copy(recentComparisons = products)
                }

                // Update cheapest store based on results
                val cheapestStore = products
                    .flatMap { it.prices }
                    .minByOrNull { it.price }
                    ?.chain

                _state.update {
                    it.copy(cheapestStore = cheapestStore)
                }
            },
            onFailure = { /* Handle error */ }
        )
    }

    fun selectCity(city: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCity = city,
                    showCitySelector = false,
                    recentCities = (listOf(city) + it.recentCities).distinct().take(3)
                )
            }

            // Reload content for new city
            loadHomeContent()
        }
    }

    fun showCitySelector() {
        _state.update { it.copy(showCitySelector = true) }
    }

    fun hideCitySelector() {
        _state.update { it.copy(showCitySelector = false) }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            loadHomeContent()
            loadUserData()

            // Simulate network delay
            delay(1000)

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun addToCart(product: GroupedProduct, storePrice: StorePrice) {
        viewModelScope.launch {
            // Convert to domain Product model
            val domainProduct = com.example.championcart.domain.models.Product(
                itemCode = product.itemCode,
                itemName = product.itemName,
                chain = storePrice.chain,
                storeId = storePrice.storeId,
                price = storePrice.price,
                timestamp = storePrice.timestamp,
                weight = product.weight,
                unit = product.unit,
                pricePerUnit = product.pricePerUnit
            )

            cartRepository.addToCart(domainProduct, 1).fold(
                onSuccess = {
                    _state.update {
                        it.copy(showCartAddedFeedback = true)
                    }

                    // Hide feedback after delay
                    delay(2000)
                    _state.update {
                        it.copy(showCartAddedFeedback = false)
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(error = error.message)
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

/**
 * Home screen UI state
 */
data class HomeScreenState(
    // User data
    val userName: String? = null,
    val isGuest: Boolean = false,

    // City selection
    val selectedCity: String = "תל אביב",
    val availableCities: List<String> = emptyList(),
    val recentCities: List<String> = emptyList(),
    val showCitySelector: Boolean = false,
    val isLoadingCities: Boolean = false,

    // Stats
    val totalSaved: Double = 0.0,
    val itemsCompared: Int = 0,
    val cheapestStore: String? = null,

    // Content
    val featuredDeals: List<GroupedProduct> = emptyList(),
    val recentComparisons: List<GroupedProduct> = emptyList(),

    // UI states
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showCartAddedFeedback: Boolean = false,
    val error: String? = null
)