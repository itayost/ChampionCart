package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import com.example.championcart.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val selectedCity: String = "Tel Aviv",
    val cheapestCartResult: CheapestCartResult? = null,
    val savedCarts: List<SavedCart> = emptyList(),
    val isLoading: Boolean = false,
    val isFindingCheapest: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingSavedCarts: Boolean = false,
    val error: String? = null,
    val showSaveDialog: Boolean = false,
    val showSavedCartsDialog: Boolean = false,
    val saveCartName: String = ""
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val priceRepository: PriceRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        observeCartItems()
        loadSavedCarts()

        // Get selected city
        val savedCity = "Tel Aviv" // Could be tokenManager.getSelectedCity()
        _state.update { it.copy(selectedCity = savedCity) }
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.getCartItems()
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

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                if (newQuantity <= 0) {
                    cartRepository.removeFromCart(itemId)
                } else {
                    cartRepository.updateQuantity(itemId, newQuantity)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update quantity: ${e.message}")
                }
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(itemId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to remove item: ${e.message}")
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                cartRepository.clearCart()
                _state.update {
                    it.copy(
                        cheapestCartResult = null,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to clear cart: ${e.message}")
                }
            }
        }
    }

    fun findCheapestCart() {
        val currentItems = _state.value.cartItems
        if (currentItems.isEmpty()) {
            _state.update {
                it.copy(error = "Cart is empty. Add some products first.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isFindingCheapest = true,
                    error = null
                )
            }

            try {
                // Convert cart items to API format
                val cartProducts = currentItems.map { cartItem ->
                    CartProduct(
                        itemName = cartItem.productName,
                        quantity = cartItem.quantity
                    )
                }

                // Call cheapest cart API
                val result = priceRepository.findCheapestCart(
                    city = _state.value.selectedCity,
                    items = cartProducts
                )

                result.fold(
                    onSuccess = { cheapestResult ->
                        _state.update {
                            it.copy(
                                cheapestCartResult = cheapestResult,
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
                        error = "Error finding cheapest cart: ${e.message}"
                    )
                }
            }
        }
    }

    fun showSaveDialog() {
        _state.update {
            it.copy(
                showSaveDialog = true,
                saveCartName = "My Cart ${System.currentTimeMillis() / 1000}" // Default name
            )
        }
    }

    fun hideSaveDialog() {
        _state.update {
            it.copy(
                showSaveDialog = false,
                saveCartName = ""
            )
        }
    }

    fun updateSaveCartName(name: String) {
        _state.update { it.copy(saveCartName = name) }
    }

    fun saveCart() {
        val currentState = _state.value
        val userEmail = tokenManager.getUserEmail()

        if (userEmail == null) {
            _state.update {
                it.copy(error = "Please log in to save carts")
            }
            return
        }

        if (currentState.cartItems.isEmpty()) {
            _state.update {
                it.copy(error = "Cart is empty. Add some products first.")
            }
            return
        }

        if (currentState.saveCartName.isBlank()) {
            _state.update {
                it.copy(error = "Please enter a cart name")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    error = null
                )
            }

            try {
                // Convert cart items to API format
                val cartProducts = currentState.cartItems.map { cartItem ->
                    CartProduct(
                        itemName = cartItem.productName,
                        quantity = cartItem.quantity
                    )
                }

                val saveRequest = SaveCartRequest(
                    cartName = currentState.saveCartName.trim(),
                    email = userEmail,
                    city = currentState.selectedCity,
                    items = cartProducts
                )

                val result = authRepository.saveUserCart(saveRequest)

                result.fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                showSaveDialog = false,
                                saveCartName = "",
                                error = null
                            )
                        }

                        // Reload saved carts
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
                        error = "Error saving cart: ${e.message}"
                    )
                }
            }
        }
    }

    fun showSavedCartsDialog() {
        _state.update { it.copy(showSavedCartsDialog = true) }
        loadSavedCarts()
    }

    fun hideSavedCartsDialog() {
        _state.update { it.copy(showSavedCartsDialog = false) }
    }

    fun loadSavedCarts() {
        val userEmail = tokenManager.getUserEmail() ?: return

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
                                savedCarts = emptyList(),
                                isLoadingSavedCarts = false,
                                error = "Failed to load saved carts: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        savedCarts = emptyList(),
                        isLoadingSavedCarts = false,
                        error = "Error loading saved carts: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadSavedCart(savedCart: SavedCart) {
        viewModelScope.launch {
            try {
                // Clear current cart
                cartRepository.clearCart()

                // Add items from saved cart
                savedCart.items.forEach { savedItem ->
                    cartRepository.addToCart(
                        productId = "saved-${savedItem.itemName.hashCode()}", // Generate ID
                        productName = savedItem.itemName,
                        price = savedItem.price,
                        quantity = savedItem.quantity,
                        storeChain = "Unknown", // Saved carts don't include store info
                        storeId = "000"
                    )
                }

                _state.update {
                    it.copy(
                        showSavedCartsDialog = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to load saved cart: ${e.message}")
                }
            }
        }
    }

    fun selectCity(city: String) {
        _state.update {
            it.copy(
                selectedCity = city,
                cheapestCartResult = null, // Clear previous results
                error = null
            )
        }

        // Save city preference
        viewModelScope.launch {
            // tokenManager.saveSelectedCity(city)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun retryLastAction() {
        // Retry the last failed action based on current state
        when {
            _state.value.cartItems.isNotEmpty() && _state.value.cheapestCartResult == null -> {
                findCheapestCart()
            }
            _state.value.savedCarts.isEmpty() -> {
                loadSavedCarts()
            }
            else -> {
                clearError()
            }
        }
    }
}