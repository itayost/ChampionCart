package com.example.championcart.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.usecase.cart.CalculateCheapestStoreUseCase
import com.example.championcart.domain.usecase.city.GetCitiesUseCase
import com.example.championcart.domain.usecase.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val calculateCheapestStoreUseCase: CalculateCheapestStoreUseCase,
    private val tokenManager: TokenManager,
    private val preferencesManager: PreferencesManager,
    private val cartManager: CartManager
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState(
        selectedCity = preferencesManager.getSelectedCity()
    ))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadInitialData()
        observeCartChanges()
    }

    private fun loadInitialData() {
        loadUserInfo()
        loadCities()
        loadRecentSearches()
        loadFeaturedProducts()
        updateCartInfo()
    }

    private fun loadUserInfo() {
        val email = tokenManager.getUserEmail()
        val isGuest = tokenManager.isGuestMode()

        // Load saved total savings from preferences
        val savedTotalSavings = preferencesManager.getTotalSavings()

        _uiState.update { it.copy(
            userName = if (isGuest) "אורח" else email?.substringBefore("@") ?: "משתמש",
            isGuest = isGuest,
            totalSavings = savedTotalSavings
        ) }
    }

    private fun loadCities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCitiesLoading = true) }

            getCitiesUseCase().collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        _uiState.update {
                            it.copy(
                                cities = cities,
                                isCitiesLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isCitiesLoading = false,
                                error = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    private fun loadRecentSearches() {
        val searches = preferencesManager.getRecentSearches()
        _uiState.update { it.copy(recentSearches = searches) }
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFeaturedLoading = true) }

            // Search for popular items
            val popularItems = listOf("חלב", "לחם", "ביצים")
            val city = _uiState.value.selectedCity

            Log.d(TAG, "Loading featured products for city: '$city'")
            if (city.isEmpty()) {
                Log.w(TAG, "City is empty, featured products may not load correctly")
            }

            val featuredList = mutableListOf<Product>()

            popularItems.forEach { item ->
                searchProductsUseCase(item, city).collect { result ->
                    result.fold(
                        onSuccess = { products ->
                            if (products.isNotEmpty()) {
                                val product = products.first()
                                featuredList.add(product)
                                // Debug logging
                                Log.d(TAG, "Featured product added: name='${product.name}', price=${product.bestPrice}")
                            }
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Failed to load featured products for '$item'", error)
                        }
                    )
                }
            }

            Log.d(TAG, "Total featured products: ${featuredList.size}")
            _uiState.update { it.copy(
                featuredProducts = featuredList,
                isFeaturedLoading = false
            ) }
        }
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                val itemCount = items.sumOf { it.quantity }
                _uiState.update { it.copy(cartItemCount = itemCount) }

                // Update potential savings if cart has items
                if (items.isNotEmpty()) {
                    updatePotentialSavings()
                }
            }
        }
    }

    private fun updateCartInfo() {
        val itemCount = cartManager.getItemCount()
        _uiState.update { it.copy(cartItemCount = itemCount) }
    }

    private fun updatePotentialSavings() {
        viewModelScope.launch {
            calculateCheapestStoreUseCase(_uiState.value.selectedCity).collect { result ->
                result.fold(
                    onSuccess = { cheapestStore ->
                        // Calculate potential savings
                        val cartItems = cartManager.cartItems.value
                        val currentTotal = cartItems.sumOf { item ->
                            item.product.bestPrice * item.quantity
                        }

                        if (cheapestStore.storeTotals.isNotEmpty()) {
                            val maxPrice = cheapestStore.storeTotals.values.maxOrNull() ?: currentTotal
                            val savings = maxPrice - cheapestStore.totalPrice

                            if (savings > 0) {
                                // Add to running total
                                val newTotal = _uiState.value.totalSavings + savings
                                preferencesManager.addToTotalSavings(savings)
                                _uiState.update { it.copy(totalSavings = newTotal) }
                            }
                        }
                    },
                    onFailure = { /* Ignore calculation errors */ }
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearch() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty()) {
            // Add to recent searches
            preferencesManager.addRecentSearch(query)
            loadRecentSearches()

            // Navigation will be handled by the HomeScreen
        }
    }

    fun onClearRecentSearches() {
        preferencesManager.clearRecentSearches()
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun onCitySelected(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        preferencesManager.setSelectedCity(city)

        // Reload featured products for new city
        loadFeaturedProducts()

        // Recalculate savings if cart has items
        if (cartManager.getItemCount() > 0) {
            updatePotentialSavings()
        }
    }

    fun onProductClick(product: Product) {
        // Navigation will be handled by the screen
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun onAddToCart(product: Product) {
        cartManager.addToCart(product)

        // Show success message
        _uiState.update { it.copy(
            snackbarMessage = "נוסף לעגלה: ${product.name}"
        ) }
    }

    fun onRecentSearchClick(search: String) {
        _searchQuery.value = search
        // Navigation to search screen will be handled by HomeScreen
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshData() {
        loadInitialData()
    }
}

data class HomeUiState(
    // User info
    val userName: String = "",
    val isGuest: Boolean = false,
    val totalSavings: Double = 0.0,

    // City selection
    val cities: List<String> = emptyList(),
    val selectedCity: String = "",  // Will be set from preferences
    val isCitiesLoading: Boolean = false,

    // Search
    val searchResults: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val recentSearches: List<String> = emptyList(),

    // Featured products
    val featuredProducts: List<Product> = emptyList(),
    val isFeaturedLoading: Boolean = false,

    // Cart
    val cartItemCount: Int = 0,

    // Navigation
    val selectedProduct: Product? = null,

    // UI states
    val error: String? = null,
    val snackbarMessage: String? = null
)