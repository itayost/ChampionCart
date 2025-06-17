package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.models.CheapestCartResult
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Local cart item - only fields we actually have
 * Based on server Product structure but for local cart management
 */
data class LocalCartItem(
    val id: String,                     // Generated locally for UI
    val itemName: String,               // From server: item_name
    val price: Double,                  // From server: price
    val quantity: Int,                  // Local UI state
    val chain: String? = null,          // From server: chain (optional)
    val storeId: String? = null,        // From server: store_id (optional)
    val timestamp: String? = null       // From server: timestamp (optional)
)

/**
 * Cart State - Only server-provided data
 */
data class CartState(
    val cartItems: List<LocalCartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val selectedCity: String = "Tel Aviv",
    val cheapestCartResult: CheapestCartResult? = null,
    val savedCarts: List<SavedCart> = emptyList(),

    // Loading states
    val isLoading: Boolean = false,
    val isFindingCheapest: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingSavedCarts: Boolean = false,

    // Error handling
    val error: String? = null,

    // Dialog states
    val showCityDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showSavedCartsDialog: Boolean = false,
    val saveCartName: String = ""
)

/**
 * CartViewModel - Only uses server-provided data structure
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    // In-memory cart storage using only server fields
    private val _cartItems = MutableStateFlow<List<LocalCartItem>>(emptyList())

    init {
        observeCartItems()
        loadSelectedCity()
        loadSavedCarts()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            _cartItems
                .catch { exception ->
                    _state.update {
                        it.copy(error = "Failed to load cart: ${exception.message}")
                    }
                }
                .collect { items ->
                    val total = items.sumOf { it.price * it.quantity }
                    _state.update {
                        it.copy(
                            cartItems = items,
                            totalPrice = total,
                            error = null
                        )
                    }
                }
        }
    }

    private fun loadSelectedCity() {
        // Load from preferences or use default
        val savedCity = "Tel Aviv" // tokenManager.getSelectedCity() when implemented
        _state.update { it.copy(selectedCity = savedCity) }
    }

    fun selectCity(city: String) {
        _state.update { it.copy(selectedCity = city) }
        // Save to preferences
        // tokenManager.saveSelectedCity(city)
    }

    // ============ CART OPERATIONS ============

    fun addToCart(product: Product, quantity: Int = 1) {
        val existingItem = _cartItems.value.find { it.itemName == product.itemName }

        if (existingItem != null) {
            // Update quantity of existing item
            updateQuantity(existingItem.id, existingItem.quantity + quantity)
        } else {
            // Add new item using only server fields
            val newItem = LocalCartItem(
                id = generateCartItemId(),
                itemName = product.itemName,           // Server: item_name
                price = product.price ?: 0.0,          // Server: price
                quantity = quantity,                   // Local UI state
                chain = product.chain,                 // Server: chain
                storeId = product.storeId,            // Server: store_id
                timestamp = product.timestamp          // Server: timestamp
            )

            _cartItems.update { currentItems ->
                currentItems + newItem
            }
        }
    }

    fun addToCartFromGroupedProduct(groupedProduct: GroupedProduct, storePrice: StorePrice, quantity: Int = 1) {
        val existingItem = _cartItems.value.find { it.itemName == groupedProduct.itemName }

        if (existingItem != null) {
            updateQuantity(existingItem.id, existingItem.quantity + quantity)
        } else {
            val newItem = LocalCartItem(
                id = generateCartItemId(),
                itemName = groupedProduct.itemName,
                price = storePrice.price,
                quantity = quantity,
                chain = storePrice.chain,
                storeId = storePrice.storeId,
                timestamp = storePrice.timestamp
            )

            _cartItems.update { currentItems ->
                currentItems + newItem
            }
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(itemId)
            return
        }

        _cartItems.update { currentItems ->
            currentItems.map { item ->
                if (item.id == itemId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }
    }

    fun removeItem(itemId: String) {
        _cartItems.update { currentItems ->
            currentItems.filter { it.id != itemId }
        }
    }

    fun clearCart() {
        _cartItems.update { emptyList() }
        _state.update {
            it.copy(
                cheapestCartResult = null,
                error = null
            )
        }
    }

    // ============ PRICE COMPARISON ============

    fun findCheapestCart() {
        val currentItems = _state.value.cartItems
        if (currentItems.isEmpty()) {
            _state.update {
                it.copy(error = "Cart is empty. Add some products first.")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isFindingCheapest = true, error = null) }

            try {
                // Convert to server format
                val cartProducts = currentItems.map {
                    CartProduct(it.itemName, it.quantity)
                }

                val result = priceRepository.findCheapestCart(
                    city = _state.value.selectedCity,
                    items = cartProducts
                )

                result.fold(
                    onSuccess = { cheapestCart ->
                        _state.update {
                            it.copy(
                                cheapestCartResult = cheapestCart,
                                isFindingCheapest = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isFindingCheapest = false,
                                error = "Failed to find cheapest cart: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isFindingCheapest = false,
                        error = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    // ============ SAVED CARTS ============

    fun loadSavedCarts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSavedCarts = true) }

            try {
                val result = authRepository.getUserSavedCarts()

                result.fold(
                    onSuccess = { savedCarts ->
                        _state.update {
                            it.copy(
                                savedCarts = savedCarts,
                                isLoadingSavedCarts = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoadingSavedCarts = false,
                                error = "Failed to load saved carts: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingSavedCarts = false,
                        error = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveCart(cartName: String) {
        if (cartName.isBlank()) {
            _state.update { it.copy(error = "Please enter a cart name") }
            return
        }

        val currentItems = _state.value.cartItems
        if (currentItems.isEmpty()) {
            _state.update { it.copy(error = "Cannot save empty cart") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Please login to save carts"
                        )
                    }
                    return@launch
                }

                // Convert to server request format
                val saveRequest = SaveCartRequest(
                    cartName = cartName.trim(),
                    email = user.email,
                    city = _state.value.selectedCity,
                    items = currentItems.map {
                        com.example.championcart.data.models.request.CartItem(
                            itemName = it.itemName,
                            quantity = it.quantity
                        )
                    }
                )

                val result = authRepository.saveUserCart(saveRequest)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                error = null,
                                saveCartName = ""
                            )
                        }
                        // Reload saved carts to show the new one
                        loadSavedCarts()
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isSaving = false,
                                error = "Failed to save cart: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadSavedCart(savedCart: SavedCart) {
        // Clear current cart
        clearCart()

        // Convert server SavedCart to LocalCartItem
        val cartItems = savedCart.items.map { savedItem ->
            LocalCartItem(
                id = generateCartItemId(),
                itemName = savedItem.itemName,        // Server: item_name
                price = savedItem.price,              // Server: price
                quantity = savedItem.quantity,        // Server: quantity
                chain = null,                         // Not available in saved cart
                storeId = null,                       // Not available in saved cart
                timestamp = null                      // Not available in saved cart
            )
        }

        _cartItems.update { cartItems }

        // Update selected city from server data
        selectCity(savedCart.city)
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

    fun showSavedCartsDialog() {
        _state.update { it.copy(showSavedCartsDialog = true) }
        loadSavedCarts() // Refresh when showing
    }

    fun hideSavedCartsDialog() {
        _state.update { it.copy(showSavedCartsDialog = false) }
    }

    fun updateSaveCartName(name: String) {
        _state.update { it.copy(saveCartName = name) }
    }

    // ============ ERROR HANDLING ============

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun retryLastAction() {
        // Could implement retry logic based on last failed action
        clearError()
    }

    // ============ HELPER FUNCTIONS ============

    private fun generateCartItemId(): String {
        return "cart_item_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    // ============ PUBLIC UTILITY FUNCTIONS ============

    /**
     * Get current cart summary for external use
     */
    fun getCartSummary(): CartSummary {
        val currentState = _state.value
        return CartSummary(
            itemCount = currentState.cartItems.size,
            totalPrice = currentState.totalPrice,
            selectedCity = currentState.selectedCity,
            isEmpty = currentState.cartItems.isEmpty()
        )
    }

    /**
     * Check if product is already in cart
     */
    fun isProductInCart(productName: String): Boolean {
        return _cartItems.value.any { it.itemName == productName }
    }

    /**
     * Get quantity of specific product in cart
     */
    fun getProductQuantityInCart(productName: String): Int {
        return _cartItems.value.find { it.itemName == productName }?.quantity ?: 0
    }

    /**
     * Convert cart items to CartProduct for API calls
     */
    fun getCartProductsForApi(): List<CartProduct> {
        return _cartItems.value.map {
            CartProduct(it.itemName, it.quantity)
        }
    }
}

/**
 * Cart summary data class for external use
 */
data class CartSummary(
    val itemCount: Int,
    val totalPrice: Double,
    val selectedCity: String,
    val isEmpty: Boolean
)

// ============ DOMAIN MODELS (using only server fields) ============

/**
 * User model - simplified to match server auth response
 */
data class User(
    val email: String,
    val isGuest: Boolean = false
)

/**
 * CheapestCartResult - matches server response exactly
 */
data class CheapestCartResult(
    val chain: String,                          // Server: chain
    val storeId: String,                        // Server: store_id
    val totalPrice: Double,                     // Server: total_price
    val worstPrice: Double,                     // Server: worst_price
    val savings: Double,                        // Server: savings
    val savingsPercent: Double,                 // Server: savings_percent
    val city: String,                           // Server: city
    val items: List<CartProduct>,               // Server: items (item_name, quantity)
    val itemPrices: Map<String, Double>,        // Server: item_prices
    val allStores: List<StoreOption>            // Server: all_stores
)

/**
 * StoreOption - matches server response exactly
 */
data class StoreOption(
    val chain: String,                          // Server: chain
    val storeId: String,                        // Server: store_id
    val totalPrice: Double                      // Server: total_price
)