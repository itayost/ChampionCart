package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Local cart item - enhanced with emoji support
 */
data class LocalCartItem(
    val id: String,
    val itemName: String,
    val price: Double,
    val quantity: Int,
    val chain: String? = null,
    val storeId: String? = null,
    val timestamp: String? = null,
    val emoji: String? = null  // Added for visual representation
)

/**
 * Enhanced Cart State
 */
data class CartState(
    // Cart items
    val cartItems: List<LocalCartItem> = emptyList(),
    val totalPrice: Double = 0.0,

    // City and store selection
    val selectedCity: String = "Tel Aviv",
    val selectedStore: Store? = null,
    val availableStores: List<StoreWithPrice> = emptyList(),
    val showStoreSelector: Boolean = false,

    // Price comparison
    val cheapestCartResult: CheapestCartResult? = null,
    val potentialSavings: Double = 0.0,

    // Saved carts
    val savedCarts: List<SavedCart> = emptyList(),

    // Loading states
    val isLoading: Boolean = false,
    val isFindingCheapest: Boolean = false,
    val isSaving: Boolean = false,

    // Error handling
    val error: String? = null,

    // Dialog states
    val showCityDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val saveCartName: String = ""
)

/**
 * Enhanced CartViewModel with store selection
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadSelectedCity()
        loadSavedCarts()
        calculateTotals()
    }

    // ============ CART OPERATIONS ============

    fun addToCart(product: Product, quantity: Int = 1) {
        val existingItem = _state.value.cartItems.find { it.itemName == product.itemName }

        if (existingItem != null) {
            updateQuantity(existingItem.id, existingItem.quantity + quantity)
        } else {
            val newItem = LocalCartItem(
                id = generateCartItemId(),
                itemName = product.itemName,
                price = product.price ?: 0.0,
                quantity = quantity,
                emoji = getProductEmoji(product.itemName)
            )

            _state.update { state ->
                state.copy(
                    cartItems = state.cartItems + newItem
                )
            }
            calculateTotals()
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(itemId)
            return
        }

        _state.update { state ->
            state.copy(
                cartItems = state.cartItems.map { item ->
                    if (item.id == itemId) {
                        item.copy(quantity = newQuantity)
                    } else {
                        item
                    }
                }
            )
        }
        calculateTotals()
    }

    fun removeItem(itemId: String) {
        _state.update { state ->
            state.copy(
                cartItems = state.cartItems.filterNot { it.id == itemId }
            )
        }
        calculateTotals()
    }

    fun clearCart() {
        _state.update { state ->
            state.copy(
                cartItems = emptyList(),
                totalPrice = 0.0,
                potentialSavings = 0.0,
                selectedStore = null
            )
        }
    }

    // ============ PRICE COMPARISON ============

    fun findCheapestCart() {
        if (_state.value.cartItems.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isFindingCheapest = true, error = null) }

            try {
                // Convert to API format
                val cartProducts = _state.value.cartItems.map { item ->
                    CartProduct(
                        productName = item.itemName,
                        quantity = item.quantity
                    )
                }

                // Call API
                priceRepository.findCheapestCart(
                    city = _state.value.selectedCity,
                    cartProducts = cartProducts
                ).fold(
                    onSuccess = { result ->
                        // Calculate store prices
                        val storesWithPrices = calculateStoresWithPrices(result)

                        _state.update { state ->
                            state.copy(
                                cheapestCartResult = result,
                                availableStores = storesWithPrices,
                                potentialSavings = calculatePotentialSavings(storesWithPrices),
                                showStoreSelector = true,
                                isFindingCheapest = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                error = error.message ?: "Failed to find prices",
                                isFindingCheapest = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isFindingCheapest = false
                    )
                }
            }
        }
    }

    // ============ STORE SELECTION ============

    fun selectStore(store: Store) {
        _state.update { it.copy(selectedStore = store) }
    }

    fun showStoreSelector() {
        _state.update { it.copy(showStoreSelector = true) }
    }

    fun hideStoreSelector() {
        _state.update { it.copy(showStoreSelector = false) }
    }

    // ============ SAVED CARTS ============

    fun loadSavedCarts() {
        viewModelScope.launch {
            try {
                authRepository.getSavedCarts().fold(
                    onSuccess = { carts ->
                        _state.update { it.copy(savedCarts = carts) }
                    },
                    onFailure = { /* Handle error */ }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun saveCart() {
        val cartName = _state.value.saveCartName.ifBlank {
            "Cart ${_state.value.savedCarts.size + 1}"
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val cartProducts = _state.value.cartItems.map { item ->
                    CartProduct(item.itemName, item.quantity)
                }

                authRepository.saveCart(
                    SaveCartRequest(
                        cartName = cartName,
                        city = _state.value.selectedCity,
                        products = cartProducts
                    )
                ).fold(
                    onSuccess = {
                        _state.update { it.copy(
                            showSaveDialog = false,
                            saveCartName = "",
                            isSaving = false
                        )}
                        loadSavedCarts()
                    },
                    onFailure = { error ->
                        _state.update { it.copy(
                            error = error.message,
                            isSaving = false
                        )}
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message,
                    isSaving = false
                )}
            }
        }
    }

    // ============ DIALOG MANAGEMENT ============

    fun showCityDialog() {
        _state.update { it.copy(showCityDialog = true) }
    }

    fun hideCityDialog() {
        _state.update { it.copy(showCityDialog = false) }
    }

    fun showSaveDialog() {
        _state.update { it.copy(showSaveDialog = true, saveCartName = "") }
    }

    fun hideSaveDialog() {
        _state.update { it.copy(showSaveDialog = false, saveCartName = "") }
    }

    fun updateSaveCartName(name: String) {
        _state.update { it.copy(saveCartName = name) }
    }

    // ============ HELPER FUNCTIONS ============

    private fun loadSelectedCity() {
        // Load from preferences or use default
        val savedCity = "Tel Aviv" // TODO: Load from preferences
        _state.update { it.copy(selectedCity = savedCity) }
    }

    private fun calculateTotals() {
        val total = _state.value.cartItems.sumOf { it.price * it.quantity }
        _state.update { it.copy(totalPrice = total) }
    }

    private fun calculateStoresWithPrices(result: CheapestCartResult): List<StoreWithPrice> {
        // This would be calculated from the API response
        // For now, return mock data
        return listOf(
            StoreWithPrice(
                store = Store(
                    id = "1",
                    name = "Shufersal Deal",
                    address = "Dizengoff 123, Tel Aviv",
                    chain = "Shufersal",
                    city = "Tel Aviv"
                ),
                totalPrice = _state.value.totalPrice * 0.9 // 10% cheaper
            ),
            StoreWithPrice(
                store = Store(
                    id = "2",
                    name = "Victory Supermarket",
                    address = "Ibn Gabirol 89, Tel Aviv",
                    chain = "Victory",
                    city = "Tel Aviv"
                ),
                totalPrice = _state.value.totalPrice * 0.95
            ),
            StoreWithPrice(
                store = Store(
                    id = "3",
                    name = "Shufersal Express",
                    address = "Rothschild 45, Tel Aviv",
                    chain = "Shufersal",
                    city = "Tel Aviv"
                ),
                totalPrice = _state.value.totalPrice
            )
        )
    }

    private fun calculatePotentialSavings(stores: List<StoreWithPrice>): Double {
        if (stores.isEmpty()) return 0.0
        val cheapest = stores.minByOrNull { it.totalPrice }?.totalPrice ?: 0.0
        val current = _state.value.totalPrice
        return current - cheapest
    }

    private fun generateCartItemId(): String {
        return "cart_item_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun getProductEmoji(productName: String): String {
        val name = productName.lowercase()
        return when {
            name.contains("חלב") || name.contains("milk") -> "🥛"
            name.contains("לחם") || name.contains("bread") -> "🍞"
            name.contains("ביצה") || name.contains("egg") -> "🥚"
            name.contains("במבה") || name.contains("bamba") -> "🥜"
            name.contains("גבינה") || name.contains("cheese") -> "🧀"
            name.contains("עגבניה") || name.contains("tomato") -> "🍅"
            name.contains("תפוח") || name.contains("apple") -> "🍎"
            name.contains("בננה") || name.contains("banana") -> "🍌"
            else -> "📦"
        }
    }

    fun selectCity(city: String) {
        _state.update { it.copy(selectedCity = city) }
        // TODO: Save to preferences
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}