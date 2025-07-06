package com.example.championcart.presentation.screens.home

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

    private val _uiState = MutableStateFlow(HomeUiState())
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

        _uiState.update { it.copy(
            userName = if (isGuest) "אורח" else email?.substringBefore("@") ?: "משתמש",
            isGuest = isGuest
        ) }
    }

    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase().collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        val currentCity = preferencesManager.getSelectedCity()
                        _uiState.update {
                            it.copy(
                                cities = cities,
                                selectedCity = currentCity,
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
        // Load from preferences
        val recentSearches = preferencesManager.getRecentSearches()
        _uiState.update { it.copy(recentSearches = recentSearches) }
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFeaturedLoading = true) }

            // Search for popular items
            val popularItems = listOf("חלב", "לחם", "ביצים", "עגבניות")
            val city = _uiState.value.selectedCity

            popularItems.forEach { item ->
                searchProductsUseCase(item, city).collect { result ->
                    result.fold(
                        onSuccess = { products ->
                            if (products.isNotEmpty()) {
                                _uiState.update { state ->
                                    state.copy(
                                        featuredProducts = state.featuredProducts + products.first(),
                                        isFeaturedLoading = false
                                    )
                                }
                            }
                        },
                        onFailure = {
                            _uiState.update { it.copy(isFeaturedLoading = false) }
                        }
                    )
                }
            }
        }
    }

    private fun observeCartChanges() {
        cartManager.cartItems.onEach {
            updateCartInfo()
        }.launchIn(viewModelScope)
    }

    private fun updateCartInfo() {
        val totalItems = cartManager.getItemCount()

        _uiState.update { it.copy(cartItemCount = totalItems) }

        // Calculate potential savings
        if (totalItems > 0) {
            calculateCartSavings()
        }
    }

    private fun calculateCartSavings() {
        viewModelScope.launch {
            calculateCheapestStoreUseCase(_uiState.value.selectedCity).collect { result ->
                result.fold(
                    onSuccess = { cheapestStore ->
                        // Calculate potential savings by comparing cheapest with most expensive
                        val maxPrice = cheapestStore.storeTotals.values.maxOrNull() ?: 0.0
                        val minPrice = cheapestStore.totalPrice
                        val savings = maxPrice - minPrice

                        _uiState.update { it.copy(
                            totalSavings = _uiState.value.totalSavings + savings
                        ) }
                    },
                    onFailure = { /* Handle error */ }
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

            // Perform search
            searchProducts(query)
        }
    }

    fun onCitySelected(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        preferencesManager.setSelectedCity(city)

        // Reload featured products for new city
        loadFeaturedProducts()
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
        searchProducts(search)
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isSearching = true,
                searchResults = emptyList(),
                error = null
            ) }

            searchProductsUseCase(query, _uiState.value.selectedCity).collect { result ->
                result.fold(
                    onSuccess = { products ->
                        _uiState.update { it.copy(
                            searchResults = products,
                            isSearching = false
                        ) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(
                            isSearching = false,
                            error = error.message ?: "שגיאה בחיפוש"
                        ) }
                    }
                )
            }
        }
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
    val selectedCity: String = "תל אביב",
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