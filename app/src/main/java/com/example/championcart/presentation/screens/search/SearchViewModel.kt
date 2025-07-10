package com.example.championcart.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.usecase.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val cartManager: CartManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // Load recent searches
        val recentSearches = preferencesManager.getRecentSearches()
        _uiState.update { it.copy(recentSearches = recentSearches) }

        // Set selected city
        val selectedCity = preferencesManager.getSelectedCity()
        _uiState.update { it.copy(selectedCity = selectedCity) }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        // Cancel previous search
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(
                searchResults = emptyList(),
                isSearching = false,
                showSuggestions = true
            ) }
            return
        }

        // Debounce search WITHOUT immediately setting isSearching
        searchJob = viewModelScope.launch {
            delay(300) // Debounce delay
            _uiState.update { it.copy(isSearching = true, showSuggestions = false) }
            performSearch(query)
        }
    }

    fun onSearch() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty()) {
            // Add to recent searches
            preferencesManager.addRecentSearch(query)
            loadInitialData() // Reload recent searches

            // Perform search immediately
            searchJob?.cancel()
            viewModelScope.launch {
                _uiState.update { it.copy(isSearching = true, showSuggestions = false) }
                performSearch(query)
            }
        }
    }

    private suspend fun performSearch(query: String) {
        searchProductsUseCase(query, _uiState.value.selectedCity).collect { result ->
            result.fold(
                onSuccess = { products ->
                    _uiState.update { it.copy(
                        searchResults = products,
                        isSearching = false,
                        error = null
                    ) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        error = error.message ?: "שגיאה בחיפוש"
                    ) }
                }
            )
        }
    }

    fun onProductClick(product: Product) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun onAddToCart(product: Product) {
        cartManager.addToCart(product)
        _uiState.update { it.copy(
            snackbarMessage = "נוסף לעגלה: ${product.name}"
        ) }
    }

    fun onRecentSearchClick(search: String) {
        _searchQuery.value = search
        onSearch()
    }

    fun onClearRecentSearches() {
        preferencesManager.clearRecentSearches()
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun onSortOptionSelected(sortOption: SortOption) {
        _uiState.update { currentState ->
            val sortedResults = when (sortOption) {
                SortOption.PRICE_LOW_TO_HIGH -> currentState.searchResults.sortedBy { it.bestPrice }
                SortOption.PRICE_HIGH_TO_LOW -> currentState.searchResults.sortedByDescending { it.bestPrice }
                SortOption.NAME_A_TO_Z -> currentState.searchResults.sortedBy { it.name }
                SortOption.BEST_SAVINGS -> currentState.searchResults.sortedByDescending { product ->
                    val maxPrice = product.stores.maxOfOrNull { it.price } ?: product.bestPrice
                    maxPrice - product.bestPrice
                }
            }
            currentState.copy(
                searchResults = sortedResults,
                selectedSortOption = sortOption
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

data class SearchUiState(
    val searchResults: List<Product> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val showSuggestions: Boolean = true,
    val selectedCity: String = "תל אביב",
    val selectedSortOption: SortOption = SortOption.PRICE_LOW_TO_HIGH,
    val selectedProduct: Product? = null,
    val error: String? = null,
    val snackbarMessage: String? = null
)

enum class SortOption(val displayName: String) {
    PRICE_LOW_TO_HIGH("מחיר - נמוך לגבוה"),
    PRICE_HIGH_TO_LOW("מחיר - גבוה לנמוך"),
    NAME_A_TO_Z("שם - א׳ עד ת׳"),
    BEST_SAVINGS("חיסכון הכי גדול")
}