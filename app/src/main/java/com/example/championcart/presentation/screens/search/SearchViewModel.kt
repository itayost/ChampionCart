package com.example.championcart.presentation.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow<String?>(null)
    val selectedCity: StateFlow<String?> = _selectedCity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            priceRepository.getCities().fold(
                onSuccess = { cities ->
                    _uiState.value = _uiState.value.copy(availableCities = cities)
                    if (cities.isNotEmpty() && _selectedCity.value == null) {
                        _selectedCity.value = cities.first()
                    }
                },
                onFailure = {
                    Log.e(TAG, "Failed to load cities", it)
                }
            )
        }
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
        // Re-search with new city if there's an active query
        if (_searchQuery.value.isNotEmpty()) {
            performSearch(_searchQuery.value)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        // Cancel previous search
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearching = false
            )
            return
        }

        // Debounce search
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSearching = true,
                isLoading = true,
                error = null
            )

            val result = searchProductsUseCase(
                query = query,
                selectedCity = _selectedCity.value
            )

            result.fold(
                onSuccess = { products ->
                    Log.d(TAG, "Search successful: ${products.size} products found")
                    _uiState.value = _uiState.value.copy(
                        searchResults = products,
                        groupedProducts = products,
                        isSearching = false,
                        isLoading = false,
                        hasSearched = true,
                        searchQuery = query,
                        selectedCity = _selectedCity.value,
                        recentSearches = updateRecentSearches(query)
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Search failed", error)
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        isLoading = false,
                        error = error.message ?: "Search failed"
                    )
                }
            )
        }
    }

    private fun updateRecentSearches(query: String): List<String> {
        val current = _uiState.value.recentSearches.toMutableList()
        current.remove(query)
        current.add(0, query)
        return current.take(5)
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            isSearching = false,
            error = null
        )
    }

    fun applyFilter(filter: SearchFilter) {
        _uiState.value = _uiState.value.copy(activeFilter = filter)
        sortResults()
    }

    fun toggleSortOrder() {
        val newOrder = when (_uiState.value.sortOrder) {
            SortOrder.PRICE_LOW_TO_HIGH -> SortOrder.PRICE_HIGH_TO_LOW
            SortOrder.PRICE_HIGH_TO_LOW -> SortOrder.NAME_A_TO_Z
            SortOrder.NAME_A_TO_Z -> SortOrder.NAME_Z_TO_A
            SortOrder.NAME_Z_TO_A -> SortOrder.PRICE_LOW_TO_HIGH
        }
        _uiState.value = _uiState.value.copy(sortOrder = newOrder)
        sortResults()
    }

    private fun sortResults() {
        val sorted = when (_uiState.value.sortOrder) {
            SortOrder.PRICE_LOW_TO_HIGH -> {
                _uiState.value.searchResults.sortedBy {
                    it.prices.minByOrNull { price -> price.price }?.price ?: Double.MAX_VALUE
                }
            }
            SortOrder.PRICE_HIGH_TO_LOW -> {
                _uiState.value.searchResults.sortedByDescending {
                    it.prices.maxByOrNull { price -> price.price }?.price ?: 0.0
                }
            }
            SortOrder.NAME_A_TO_Z -> {
                _uiState.value.searchResults.sortedBy { it.itemName }
            }
            SortOrder.NAME_Z_TO_A -> {
                _uiState.value.searchResults.sortedByDescending { it.itemName }
            }
        }

        _uiState.value = _uiState.value.copy(searchResults = sorted)
    }

    fun addToCart(product: GroupedProduct) {
        viewModelScope.launch {
            // Find the best price store
            val bestStore = product.prices.minByOrNull { it.price }
            if (bestStore != null) {
                val cartProduct = Product(
                    itemCode = product.itemCode,
                    itemName = product.itemName,
                    chain = bestStore.chain,
                    storeId = bestStore.storeId,
                    price = bestStore.price,
                    city = bestStore.city
                )

                cartRepository.addToCart(cartProduct).fold(
                    onSuccess = {
                        Log.d(TAG, "Product added to cart: ${product.itemName}")
                        _uiState.value = _uiState.value.copy(
                            showAddedToCart = true,
                            lastAddedProduct = product.itemName
                        )
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to add to cart", it)
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to add to cart"
                        )
                    }
                )
            }
        }
    }

    fun dismissAddedToCart() {
        _uiState.value = _uiState.value.copy(showAddedToCart = false)
    }

    fun searchProducts(query: String) {
        onSearchQueryChanged(query)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleIdenticalOnly() {
        _uiState.value = _uiState.value.copy(showIdenticalOnly = !_uiState.value.showIdenticalOnly)
        if (_searchQuery.value.isNotEmpty()) {
            performSearch(_searchQuery.value)
        }
    }

    fun retry() {
        if (_searchQuery.value.isNotEmpty()) {
            performSearch(_searchQuery.value)
        }
    }

    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            groupedProducts = emptyList(),
            hasSearched = false
        )
    }

    fun updateSort(sortOption: String) {
        val newOrder = when (sortOption) {
            "price_low_high" -> SortOrder.PRICE_LOW_TO_HIGH
            "price_high_low" -> SortOrder.PRICE_HIGH_TO_LOW
            "name_a_z" -> SortOrder.NAME_A_TO_Z
            "name_z_a" -> SortOrder.NAME_Z_TO_A
            else -> SortOrder.PRICE_LOW_TO_HIGH
        }
        _uiState.value = _uiState.value.copy(sortOrder = newOrder, sortOption = sortOption)
        sortResults()
    }

    fun searchFromSuggestion(suggestion: String) {
        onSearchQueryChanged(suggestion)
    }
}

data class SearchUiState(
    val searchResults: List<GroupedProduct> = emptyList(),
    val groupedProducts: List<GroupedProduct> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val recentSearches: List<String> = emptyList(),
    val popularSearches: List<String> = listOf("חלב", "לחם", "ביצים", "גבינה", "יוגורט"),
    val activeFilter: SearchFilter = SearchFilter.ALL,
    val sortOrder: SortOrder = SortOrder.PRICE_LOW_TO_HIGH,
    val sortOption: String = "price_low_high",
    val availableCities: List<String> = emptyList(),
    val selectedCity: String? = null,
    val showAddedToCart: Boolean = false,
    val lastAddedProduct: String? = null,
    val showIdenticalOnly: Boolean = false
)

enum class SearchFilter {
    ALL,
    ON_SALE,
    NEW_PRODUCTS,
    POPULAR
}

enum class SortOrder {
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    NAME_A_TO_Z,
    NAME_Z_TO_A
}