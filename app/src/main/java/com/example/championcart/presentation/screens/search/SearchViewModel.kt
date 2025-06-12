package com.example.championcart.presentation.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.ProductStorePrice
import com.example.championcart.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val groupedProducts: List<GroupedProduct> = emptyList(),  // Using domain model
    val error: String? = null,
    val selectedCity: String = "Tel Aviv",
    val cartItemsCount: Map<String, Int> = emptyMap()
)

class SearchViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val tokenManager: TokenManager,
    private val cartManager: CartManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        try {
            val savedCity = tokenManager.getSelectedCity()
            _uiState.value = _uiState.value.copy(selectedCity = savedCity)
            Log.d("SearchViewModel", "Initialized with city: $savedCity")
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error loading city", e)
            _uiState.value = _uiState.value.copy(selectedCity = "Tel Aviv")
        }

        // Observe cart changes
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                val cartCounts = items.associate { it.itemCode to it.quantity }
                _uiState.value = _uiState.value.copy(cartItemsCount = cartCounts)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        Log.d("SearchViewModel", "Query changed: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query, error = null)
    }

    fun searchProducts() {
        Log.d("SearchViewModel", "searchProducts called with query: ${_uiState.value.searchQuery}")

        searchJob?.cancel()

        if (_uiState.value.searchQuery.isBlank()) {
            Log.d("SearchViewModel", "Query is blank, skipping search")
            return
        }

        searchJob = viewModelScope.launch {
            Log.d("SearchViewModel", "Starting search for: ${_uiState.value.searchQuery} in ${_uiState.value.selectedCity}")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = searchProductsUseCase(
                    city = _uiState.value.selectedCity,
                    query = _uiState.value.searchQuery.trim()
                )

                result.fold(
                    onSuccess = { products ->
                        Log.d("SearchViewModel", "Search successful, found ${products.size} products")

                        // Group products by item code
                        val grouped = products.groupBy { it.itemCode }
                            .map { (itemCode, productList) ->
                                val prices = productList.map { it.price }.sorted()
                                val storePrices = productList.map {
                                    ProductStorePrice(  // Using domain model
                                        chain = it.chainName,
                                        storeId = it.storeName,
                                        price = it.price
                                    )
                                }.sortedBy { it.price }

                                GroupedProduct(  // Using domain model
                                    itemCode = itemCode,
                                    itemName = productList.first().itemName,
                                    storePrices = storePrices,
                                    lowestPrice = prices.firstOrNull(),
                                    highestPrice = prices.lastOrNull(),
                                    savings = if (prices.size > 1) prices.last() - prices.first() else 0.0
                                )
                            }
                            .sortedByDescending { it.savings }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            products = products,
                            groupedProducts = grouped,
                            error = if (products.isEmpty()) "No products found" else null
                        )
                    },
                    onFailure = { exception ->
                        Log.e("SearchViewModel", "Search failed", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "An error occurred"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search exception", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }

    fun addToCart(product: GroupedProduct) {
        val cheapestStore = product.storePrices.firstOrNull()
        cartManager.addToCart(
            itemCode = product.itemCode,
            itemName = product.itemName,
            chain = cheapestStore?.chain,
            price = cheapestStore?.price
        )
    }

    fun onCityChange(city: String) {
        try {
            tokenManager.saveSelectedCity(city)
            _uiState.value = _uiState.value.copy(selectedCity = city)
            if (_uiState.value.searchQuery.isNotBlank()) {
                searchProducts()
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to save city: ${e.message}"
            )
        }
    }
}