package com.example.championcart.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val priceRepository: PriceRepository,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow<String?>(null)
    val selectedCity: StateFlow<String?> = _selectedCity.asStateFlow()

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load user info
            loadUserInfo()

            // Load cities
            loadCities()

            // Load featured products
            loadFeaturedProducts()
        }
    }

    private suspend fun loadUserInfo() {
        authRepository.getCurrentUser().fold(
            onSuccess = { user ->
                _uiState.value = _uiState.value.copy(
                    userName = user?.email?.substringBefore("@") ?: "Guest",
                    isGuest = user == null
                )
            },
            onFailure = {
                Log.e(TAG, "Failed to load user info", it)
                _uiState.value = _uiState.value.copy(
                    userName = "Guest",
                    isGuest = true
                )
            }
        )
    }

    private suspend fun loadCities() {
        priceRepository.getCities().fold(
            onSuccess = { citiesList ->
                _cities.value = citiesList
                _uiState.value = _uiState.value.copy(
                    availableCities = citiesList
                )

                // Set default city if not already set
                if (_uiState.value.selectedCity == null && citiesList.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        selectedCity = citiesList.first()
                    )
                    _selectedCity.value = citiesList.first()
                }
            },
            onFailure = {
                Log.e(TAG, "Failed to load cities", it)
                // Set default cities if API fails
                val defaultCities = listOf("תל אביב", "ירושלים", "חיפה", "ראשון לציון")
                _uiState.value = _uiState.value.copy(
                    availableCities = defaultCities,
                    selectedCity = defaultCities.first()
                )
            }
        )
    }

    private suspend fun loadFeaturedProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            // Load milk products
            searchProductsUseCase("חלב", _selectedCity.value ?: "תל אביב").fold(
                onSuccess = { products ->
                    _uiState.value = _uiState.value.copy(
                        milkProducts = products.take(3),
                        featuredDeals = products.filter { it.savings > 0 }.take(5)
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to load milk products", it)
                }
            )

            // Load bread products
            searchProductsUseCase("לחם", _selectedCity.value ?: "תל אביב").fold(
                onSuccess = { products ->
                    _uiState.value = _uiState.value.copy(
                        breadProducts = products.take(3)
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to load bread products", it)
                }
            )

            // Load eggs products
            searchProductsUseCase("ביצים", _selectedCity.value ?: "תל אביב").fold(
                onSuccess = { products ->
                    _uiState.value = _uiState.value.copy(
                        eggProducts = products.take(3)
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to load egg products", it)
                }
            )

            // Set popular products from all categories
            val allProducts = _uiState.value.milkProducts +
                    _uiState.value.breadProducts +
                    _uiState.value.eggProducts

            _uiState.value = _uiState.value.copy(
                popularProducts = allProducts.sortedByDescending { it.savings }.take(10)
            )

            // Calculate quick stats
            calculateQuickStats()

        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun calculateQuickStats() {
        val totalSavings = _uiState.value.featuredDeals.sumOf { it.savings }
        val savedCarts = 3 // Mock value, should come from repository

        _uiState.value = _uiState.value.copy(
            quickStats = QuickStats(
                savedThisMonth =  totalSavings,
                savedCarts = savedCarts
            ),
            totalSavings = totalSavings
        )
    }

    fun showCitySelector() {
        _uiState.value = _uiState.value.copy(showCitySelector = true)
    }

    fun hideCitySelector() {
        _uiState.value = _uiState.value.copy(showCitySelector = false)
    }

    fun updateCity(city: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedCity = city,
                showCitySelector = false
            )
            _selectedCity.value = city

            // Reload products for new city
            loadFeaturedProducts()
        }
    }

    fun navigateToProduct(product: GroupedProduct) {
        // Navigation will be handled by the UI layer
        Log.d(TAG, "Navigate to product: ${product.itemName}")
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            searchProductsUseCase(query, _selectedCity.value).fold(
                onSuccess = { products ->
                    // Handle search results
                    Log.d(TAG, "Search found ${products.size} products")
                },
                onFailure = {
                    Log.e(TAG, "Search failed", it)
                }
            )
        }
    }

    fun addToCart(product: GroupedProduct) {
        // Convert GroupedProduct to Product for cart
        val lowestPriceStore = product.prices.minByOrNull { it.price }
        if (lowestPriceStore != null) {
            val cartProduct = Product(
                itemCode = product.itemCode,
                itemName = product.itemName,
                chain = lowestPriceStore.chain,
                storeId = lowestPriceStore.storeId,
                price = lowestPriceStore.price,
                city = lowestPriceStore.city
            )
            // Add to cart logic here
            Log.d(TAG, "Add to cart: ${product.itemName}")
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadFeaturedProducts()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun refresh() {
        refreshData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getUserEmail(): String {
        return _uiState.value.userName
    }
}

// Updated HomeUiState with ALL required properties
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val userName: String = "Guest",
    val isGuest: Boolean = true,
    val selectedCity: String? = null,          // ADDED - for city selection
    val availableCities: List<String> = emptyList(),  // ADDED - list of cities
    val showCitySelector: Boolean = false,     // ADDED - dialog visibility
    val quickStats: QuickStats? = null,        // ADDED - user statistics
    val milkProducts: List<GroupedProduct> = emptyList(),
    val breadProducts: List<GroupedProduct> = emptyList(),
    val eggProducts: List<GroupedProduct> = emptyList(),
    val featuredDeals: List<GroupedProduct> = emptyList(),
    val popularProducts: List<GroupedProduct> = emptyList(),
    val categories: List<ProductCategory> = emptyList(),
    val selectedCategory: String? = null,
    val totalSavings: Double = 0.0,
    val error: String? = null
)

data class ProductCategory(
    val name: String,
    val productCount: Int,
    val averagePrice: Double
)