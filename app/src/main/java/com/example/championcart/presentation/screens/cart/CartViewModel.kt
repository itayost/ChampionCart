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
import com.example.championcart.data.models.request.CartItem

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
 * Store with calculated total price for cart items
 */
data class StoreWithPrice(
    val store: Store,
    val totalPrice: Double
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
    val availableCities: List<String> = emptyList(),
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
        loadAvailableCities()
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
                chain = product.chain,
                storeId = product.storeId,
                timestamp = product.timestamp,
                emoji = getProductEmoji(product.itemName)
            )

            _state.update {
                it.copy(cartItems = it.cartItems + newItem)
            }
            calculateTotals()
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(itemId)
        } else {
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
    }

    fun removeFromCart(itemId: String) {
        _state.update { state ->
            state.copy(cartItems = state.cartItems.filter { it.id != itemId })
        }
        calculateTotals()
    }

    fun clearCart() {
        _state.update {
            it.copy(
                cartItems = emptyList(),
                totalPrice = 0.0,
                selectedStore = null,
                availableStores = emptyList(),
                cheapestCartResult = null,
                potentialSavings = 0.0
            )
        }
    }

    // ============ CITY OPERATIONS ============

    /**
     * Load available cities from the API
     */
    fun loadAvailableCities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val result = priceRepository.getCitiesList()

                result.fold(
                    onSuccess = { cities ->
                        _state.update {
                            it.copy(
                                availableCities = cities,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                error = exception.message ?: "Failed to load cities",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error loading cities: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectCity(city: String) {
        _state.update { it.copy(selectedCity = city) }
        // TODO: Save to preferences
    }

    fun showCityDialog() {
        _state.update { it.copy(showCityDialog = true) }
    }

    fun hideCityDialog() {
        _state.update { it.copy(showCityDialog = false) }
    }

    // ============ PRICE COMPARISON ============

    fun findCheapestPrices() {
        if (_state.value.cartItems.isEmpty()) {
            _state.update { it.copy(error = "Cart is empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isFindingCheapest = true, error = null) }

            try {
                // Convert cart items to API format
                val cartProducts = _state.value.cartItems.map { item ->
                    CartProduct(
                        itemName = item.itemName,
                        quantity = item.quantity
                    )
                }

                val result = priceRepository.findCheapestCart(
                    city = _state.value.selectedCity,
                    items = cartProducts
                )

                result.fold(
                    onSuccess = { cheapestResult ->
                        val storesWithPrices = calculateStoresWithPrices(cheapestResult)
                        val potentialSavings = calculatePotentialSavings(storesWithPrices)

                        _state.update {
                            it.copy(
                                cheapestCartResult = cheapestResult,
                                availableStores = storesWithPrices,
                                potentialSavings = potentialSavings,
                                isFindingCheapest = false,
                                showStoreSelector = true
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                error = exception.message ?: "Failed to find prices",
                                isFindingCheapest = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error finding prices: ${e.message}",
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

    fun saveCart(name: String? = null) {
        val cartName = name ?: _state.value.saveCartName.ifEmpty { "My Cart ${System.currentTimeMillis()}" }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val email = tokenManager.getUserEmail() ?: throw Exception("User not logged in")

                val saveRequest = SaveCartRequest(
                    email = email,
                    cartName = cartName,
                    city = _state.value.selectedCity,
                    items = _state.value.cartItems.map { item ->
                        CartItem(
                            itemName = item.itemName,
                            quantity = item.quantity
                        )
                    }
                )

                val result = authRepository.saveUserCart(saveRequest)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                showSaveDialog = false,
                                saveCartName = ""
                            )
                        }
                        loadSavedCarts()
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                error = exception.message ?: "Failed to save cart",
                                isSaving = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error saving cart: ${e.message}",
                        isSaving = false
                    )
                }
            }
        }
    }

    fun loadSavedCart(savedCart: SavedCart) {
        val cartItems = savedCart.items.map { savedItem ->
            LocalCartItem(
                id = generateCartItemId(),
                itemName = savedItem.itemName,
                price = savedItem.price,
                quantity = savedItem.quantity,
                emoji = getProductEmoji(savedItem.itemName)
            )
        }

        _state.update {
            it.copy(
                cartItems = cartItems,
                selectedCity = savedCart.city
            )
        }

        calculateTotals()
    }

    fun showSaveDialog() {
        _state.update { it.copy(showSaveDialog = true) }
    }

    fun hideSaveDialog() {
        _state.update { it.copy(showSaveDialog = false, saveCartName = "") }
    }

    fun updateSaveCartName(name: String) {
        _state.update { it.copy(saveCartName = name) }
    }

    private fun loadSavedCarts() {
        viewModelScope.launch {
            try {
                val email = tokenManager.getUserEmail() ?: return@launch

                val result = authRepository.getUserSavedCarts()

                result.fold(
                    onSuccess = { carts ->
                        _state.update { it.copy(savedCarts = carts) }
                    },
                    onFailure = {
                        // Silently fail - saved carts are not critical
                    }
                )
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    // ============ HELPER FUNCTIONS ============

    private fun loadSelectedCity() {
        // TODO: Load from preferences
        // For now, using default
    }

    private fun calculateTotals() {
        val total = _state.value.cartItems.sumOf { it.price * it.quantity }
        _state.update { it.copy(totalPrice = total) }
    }

    private fun calculateStoresWithPrices(result: CheapestCartResult): List<StoreWithPrice> {
        return result.allStores.map { storeOption ->
            StoreWithPrice(
                store = storeOption.toStore(),
                totalPrice = storeOption.totalPrice
            )
        }.sortedBy { it.totalPrice }
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
            name.contains("בשר") || name.contains("meat") -> "🥩"
            name.contains("עוף") || name.contains("chicken") -> "🍗"
            name.contains("דג") || name.contains("fish") -> "🐟"
            name.contains("מים") || name.contains("water") -> "💧"
            name.contains("יין") || name.contains("wine") -> "🍷"
            name.contains("בירה") || name.contains("beer") -> "🍺"
            else -> "📦"
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}