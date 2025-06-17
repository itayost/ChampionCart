package com.example.championcart.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.SortOption
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val searchQuery: String = "",
    val groupedProducts: List<GroupedProduct> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val selectedCity: String = "Tel Aviv",
    val sortOption: SortOption = SortOption.RELEVANCE,
    val recentSearches: List<String> = emptyList(),
    val popularSearches: List<String> = emptyList(),
    val showIdenticalOnly: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load recent searches from local storage
            val recentSearches = listOf("חלב", "לחם", "ביצים", "במבה", "קפה", "שמן זית")

            // Load popular searches (could be from analytics)
            val popularSearches = listOf(
                "חלב תנובה 3%",
                "לחם אחיד",
                "ביצים L",
                "במבה אוסם",
                "קוטג' 5%",
                "שמן זית"
            )

            // Get selected city from preferences
            val savedCity = "Tel Aviv" // Could be tokenManager.getSelectedCity()

            _state.update {
                it.copy(
                    recentSearches = recentSearches,
                    popularSearches = popularSearches,
                    selectedCity = savedCity
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                error = null
            )
        }
    }

    fun searchProducts() {
        val currentState = _state.value
        val query = currentState.searchQuery.trim()

        if (query.isEmpty()) {
            _state.update { it.copy(error = "Please enter a search term") }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    hasSearched = true
                )
            }

            try {
                val result = if (currentState.showIdenticalOnly) {
                    // Search for identical products across chains only
                    priceRepository.getIdenticalProducts(
                        city = currentState.selectedCity,
                        productName = query,
                        limit = 50
                    )
                } else {
                    // Regular search using use case
                    searchProductsUseCase(
                        city = currentState.selectedCity,
                        query = query,
                        groupByCode = true,
                        limit = 50
                    )
                }

                result.fold(
                    onSuccess = { products ->
                        val sortedProducts = sortProducts(products, currentState.sortOption)
                        _state.update {
                            it.copy(
                                groupedProducts = sortedProducts,
                                isLoading = false,
                                error = null
                            )
                        }

                        // Save to recent searches
                        saveRecentSearch(query)
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Search failed: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Search error: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchFromSuggestion(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchProducts()
    }

    fun toggleIdenticalOnly() {
        _state.update {
            it.copy(showIdenticalOnly = !it.showIdenticalOnly)
        }

        // Re-search if we have a query
        if (_state.value.searchQuery.isNotEmpty()) {
            searchProducts()
        }
    }

    fun updateSort(sortOption: SortOption) {
        _state.update { currentState ->
            val sortedProducts = sortProducts(currentState.groupedProducts, sortOption)
            currentState.copy(
                sortOption = sortOption,
                groupedProducts = sortedProducts
            )
        }
    }

    fun selectCity(city: String) {
        _state.update {
            it.copy(
                selectedCity = city,
                error = null
            )
        }

        // Re-search if we have results
        if (_state.value.hasSearched && _state.value.searchQuery.isNotEmpty()) {
            searchProducts()
        }

        // Save city preference
        viewModelScope.launch {
            // tokenManager.saveSelectedCity(city)
        }
    }

    fun addToCart(groupedProduct: GroupedProduct) {
        viewModelScope.launch {
            try {
                // Find the best price (lowest)
                val bestPrice = groupedProduct.prices.minByOrNull { it.price }

                if (bestPrice != null) {
                    // Add to cart through CartRepository
                    cartRepository.addToCart(
                        productId = groupedProduct.itemCode,
                        productName = groupedProduct.itemName,
                        price = bestPrice.price,
                        quantity = 1,
                        storeChain = bestPrice.chain,
                        storeId = bestPrice.storeId
                    )

                    // Could show success message here
                } else {
                    _state.update {
                        it.copy(error = "No price information available for this product")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to add to cart: ${e.message}")
                }
            }
        }
    }

    fun retry() {
        if (_state.value.searchQuery.isNotEmpty()) {
            searchProducts()
        } else {
            clearError()
        }
    }

    fun clearResults() {
        _state.update {
            it.copy(
                groupedProducts = emptyList(),
                hasSearched = false,
                searchQuery = "",
                error = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun sortProducts(products: List<GroupedProduct>, sortOption: SortOption): List<GroupedProduct> {
        return when (sortOption) {
            SortOption.RELEVANCE -> products.sortedByDescending { it.relevanceScore ?: 0.0 }
            SortOption.PRICE_LOW_TO_HIGH -> products.sortedBy { it.lowestPrice ?: Double.MAX_VALUE }
            SortOption.PRICE_HIGH_TO_LOW -> products.sortedByDescending { it.lowestPrice ?: 0.0 }
            SortOption.NAME_A_TO_Z -> products.sortedBy { it.itemName }
            SortOption.NAME_Z_TO_A -> products.sortedByDescending { it.itemName }
            SortOption.SAVINGS_HIGH_TO_LOW -> products.sortedByDescending { it.savings }
        }
    }

    private fun saveRecentSearch(query: String) {
        viewModelScope.launch {
            val currentRecent = _state.value.recentSearches.toMutableList()

            // Remove if already exists
            currentRecent.remove(query)

            // Add to beginning
            currentRecent.add(0, query)

            // Keep only last 10
            val updatedRecent = currentRecent.take(10)

            _state.update { it.copy(recentSearches = updatedRecent) }

            // Save to local storage
            // tokenManager.saveRecentSearches(updatedRecent)
        }
    }
}