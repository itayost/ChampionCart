package com.example.championcart.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.ProductStorePrice
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
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
    val selectedCity: String = "Tel Aviv", // Default city
    val sortOption: SortOption = SortOption.PRICE_LOW_TO_HIGH,
    val recentSearches: List<String> = emptyList(),
    val popularSearches: List<String> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
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
            val recentSearches = listOf("חלב", "לחם", "ביצים", "במבה", "קפה")

            // Load popular searches (could be from API)
            val popularSearches = listOf(
                "חלב תנובה 3%",
                "לחם אחיד",
                "ביצים L",
                "במבה אוסם",
                "קוטג' 5%",
                "שמן זית"
            )

            _state.update {
                it.copy(
                    recentSearches = recentSearches,
                    popularSearches = popularSearches
                )
            }

            // Load selected city from preferences
            // val savedCity = tokenManager.getSelectedCity() ?: "Tel Aviv"
            // _state.update { it.copy(selectedCity = savedCity) }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun searchProducts() {
        val query = _state.value.searchQuery.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    hasSearched = true
                )
            }

            try {
                val result = priceRepository.searchProducts(
                    city = _state.value.selectedCity,
                    productName = query
                )

                result.fold(
                    onSuccess = { products ->
                        val groupedProducts = groupProductsByCode(products)
                        val sortedProducts = sortProducts(groupedProducts, _state.value.sortOption)

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
                                error = exception.message ?: "Search failed"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error. Please try again."
                    )
                }
            }
        }
    }

    private fun groupProductsByCode(products: List<Product>): List<GroupedProduct> {
        return products
            .groupBy { it.itemCode }
            .map { (itemCode, productList) ->
                val storePrices = productList.map { product ->
                    ProductStorePrice(
                        chain = product.chainName,
                        storeId = product.storeName.substringAfter("Store "),
                        price = product.price
                    )
                }

                val lowestPrice = storePrices.minByOrNull { it.price }?.price
                val highestPrice = storePrices.maxByOrNull { it.price }?.price
                val savings = if (lowestPrice != null && highestPrice != null) {
                    highestPrice - lowestPrice
                } else {
                    0.0
                }

                GroupedProduct(
                    itemCode = itemCode,
                    itemName = productList.first().itemName,
                    storePrices = storePrices.sortedBy { it.price },
                    lowestPrice = lowestPrice,
                    highestPrice = highestPrice,
                    savings = savings
                )
            }
    }

    private fun sortProducts(
        products: List<GroupedProduct>,
        sortOption: SortOption
    ): List<GroupedProduct> {
        return when (sortOption) {
            SortOption.PRICE_LOW_TO_HIGH -> products.sortedBy { it.lowestPrice ?: Double.MAX_VALUE }
            SortOption.PRICE_HIGH_TO_LOW -> products.sortedByDescending { it.lowestPrice ?: 0.0 }
            SortOption.SAVINGS_HIGHEST -> products.sortedByDescending { it.savings }
            SortOption.NAME_A_TO_Z -> products.sortedBy { it.itemName }
        }
    }

    fun updateSort(sortOption: SortOption) {
        _state.update { currentState ->
            currentState.copy(
                sortOption = sortOption,
                groupedProducts = sortProducts(currentState.groupedProducts, sortOption)
            )
        }
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            try {
                // Find the product with the best price
                val groupedProduct = _state.value.groupedProducts.find { it.itemCode == productId }
                groupedProduct?.let { product ->
                    val bestPrice = product.storePrices.minByOrNull { it.price }

                    // Add to cart with best price store
                    // cartRepository.addToCart(
                    //     productId = productId,
                    //     productName = product.itemName,
                    //     price = bestPrice?.price ?: 0.0,
                    //     storeName = "${bestPrice?.chain} - ${bestPrice?.storeId}"
                    // )

                    // Show success feedback
                    // Could emit a UI event here for showing a snackbar
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun retry() {
        if (_state.value.searchQuery.isNotEmpty()) {
            searchProducts()
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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

// Alternative ViewModel without Hilt for testing
class SearchViewModelFactory(
    private val priceRepository: PriceRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(priceRepository, cartRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}