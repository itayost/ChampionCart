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
                _uiState.value = _uiState.value.copy(isGuest = true)
            }
        )
    }

    private suspend fun loadCities() {
        priceRepository.getCities().fold(
            onSuccess = { citiesList ->
                _cities.value = citiesList
                if (citiesList.isNotEmpty() && _selectedCity.value == null) {
                    _selectedCity.value = citiesList.first()
                }
            },
            onFailure = {
                Log.e(TAG, "Failed to load cities", it)
            }
        )
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
        loadFeaturedProducts()
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Load milk products
                loadMilkProducts()

                // Load bread products
                loadBreadProducts()

                // Load egg products
                loadEggProducts()

                // Calculate total savings
                calculateTotalSavings()

                // Set featured deals and popular products
                val allProducts = listOf(
                    _uiState.value.milkProducts,
                    _uiState.value.breadProducts,
                    _uiState.value.eggProducts
                ).flatten()

                _uiState.value = _uiState.value.copy(
                    featuredDeals = allProducts.sortedByDescending { it.priceComparison?.savings ?: 0.0 }.take(5),
                    popularProducts = allProducts.shuffled().take(5)
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error loading featured products", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun loadMilkProducts() {
        priceRepository.searchProducts(
            query = "חלב",
            city = _selectedCity.value
        ).fold(
            onSuccess = { products ->
                val topMilk = products.take(5)
                _uiState.value = _uiState.value.copy(
                    milkProducts = topMilk,
                    categories = updateCategory("חלב", topMilk)
                )
            },
            onFailure = {
                Log.e(TAG, "Failed to load milk products", it)
            }
        )
    }

    private suspend fun loadBreadProducts() {
        priceRepository.searchProducts(
            query = "לחם",
            city = _selectedCity.value
        ).fold(
            onSuccess = { products ->
                val topBread = products.take(5)
                _uiState.value = _uiState.value.copy(
                    breadProducts = topBread,
                    categories = updateCategory("לחם", topBread)
                )
            },
            onFailure = {
                Log.e(TAG, "Failed to load bread products", it)
            }
        )
    }

    private suspend fun loadEggProducts() {
        priceRepository.searchProducts(
            query = "ביצים",
            city = _selectedCity.value
        ).fold(
            onSuccess = { products ->
                val topEggs = products.take(5)
                _uiState.value = _uiState.value.copy(
                    eggProducts = topEggs,
                    categories = updateCategory("ביצים", topEggs)
                )
            },
            onFailure = {
                Log.e(TAG, "Failed to load egg products", it)
            }
        )
    }

    private fun updateCategory(name: String, products: List<GroupedProduct>): List<ProductCategory> {
        val currentCategories = _uiState.value.categories.toMutableList()
        val existingIndex = currentCategories.indexOfFirst { it.name == name }

        val category = ProductCategory(
            name = name,
            productCount = products.size,
            averagePrice = calculateAveragePrice(products)
        )

        if (existingIndex >= 0) {
            currentCategories[existingIndex] = category
        } else {
            currentCategories.add(category)
        }

        return currentCategories
    }

    private fun calculateAveragePrice(products: List<GroupedProduct>): Double {
        if (products.isEmpty()) return 0.0

        val prices = products.mapNotNull { product ->
            product.prices.minByOrNull { it.price }?.price
        }

        return if (prices.isNotEmpty()) {
            prices.average()
        } else {
            0.0
        }
    }

    private fun calculateTotalSavings() {
        val allProducts = listOf(
            _uiState.value.milkProducts,
            _uiState.value.breadProducts,
            _uiState.value.eggProducts
        ).flatten()

        val totalSavings = allProducts.sumOf { product ->
            product.priceComparison?.savings ?: 0.0
        }

        _uiState.value = _uiState.value.copy(
            totalSavings = totalSavings
        )
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

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val userName: String = "Guest",
    val isGuest: Boolean = true,
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