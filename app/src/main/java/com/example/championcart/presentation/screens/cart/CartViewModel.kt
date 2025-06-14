package com.example.championcart.presentation.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.domain.models.CheapestCartResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class CartUiState(
    val cartItems: List<com.example.championcart.data.local.CartItem> = emptyList(),
    val totalItems: Int = 0,
    val estimatedTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val cheapestCartResult: CheapestCartResult? = null,
    val error: String? = null,
    val selectedCity: String = "Tel Aviv",
    val isSaveEnabled: Boolean = false,
    val analyzeProgress: Float = 0f // For progress animation
)

class CartViewModel(
    private val cartManager: CartManager,
    private val tokenManager: TokenManager? = null,
    private val priceRepository: PriceRepositoryImpl? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        // Load selected city
        val city = tokenManager?.getSelectedCity() ?: "Tel Aviv"
        _uiState.value = _uiState.value.copy(selectedCity = city)

        // Check if user is logged in for save functionality
        val isLoggedIn = tokenManager?.getToken() != null
        _uiState.value = _uiState.value.copy(isSaveEnabled = isLoggedIn)

        // Observe cart items
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                val total = items.sumOf { item ->
                    (item.selectedPrice ?: 0.0) * item.quantity
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        cartItems = items,
                        totalItems = items.sumOf { it.quantity },
                        estimatedTotal = total,
                        cheapestCartResult = null, // Reset when cart changes
                        error = null
                    )
                }
            }
        }
    }

    fun updateQuantity(itemCode: String, quantity: Int) {
        Log.d("CartViewModel", "Updating quantity for $itemCode to $quantity")

        if (quantity <= 0) {
            removeFromCart(itemCode)
        } else {
            cartManager.updateQuantity(itemCode, quantity)
        }
    }

    fun removeFromCart(itemCode: String) {
        Log.d("CartViewModel", "Removing item $itemCode from cart")
        cartManager.removeFromCart(itemCode)

        // Clear error when item is removed
        _uiState.update { it.copy(error = null) }
    }

    fun clearCart() {
        Log.d("CartViewModel", "Clearing entire cart")
        cartManager.clearCart()

        _uiState.update { currentState ->
            currentState.copy(
                error = null,
                cheapestCartResult = null,
                analyzeProgress = 0f
            )
        }
    }

    fun findCheapestStore() {
        if (_uiState.value.cartItems.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    error = "Your cart is empty. Add some items first!"
                )
            }
            return
        }

        if (_uiState.value.isAnalyzing) {
            Log.d("CartViewModel", "Already analyzing, skipping...")
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isAnalyzing = true,
                    error = null,
                    analyzeProgress = 0f
                )
            }

            // Simulate progress for better UX
            launch {
                var progress = 0f
                while (progress < 0.9f && _uiState.value.isAnalyzing) {
                    delay(100)
                    progress += 0.1f
                    _uiState.update { it.copy(analyzeProgress = progress) }
                }
            }

            try {
                val cartProducts = _uiState.value.cartItems.map { cartItem ->
                    com.example.championcart.domain.models.CartProduct(
                        itemName = cartItem.itemName,
                        quantity = cartItem.quantity
                    )
                }

                Log.d("CartViewModel", "Finding cheapest store for ${cartProducts.size} items in ${_uiState.value.selectedCity}")

                val result = priceRepository?.findCheapestCart(
                    city = _uiState.value.selectedCity,
                    items = cartProducts
                )

                result?.fold(
                    onSuccess = { cheapestResult ->
                        Log.d("CartViewModel", "Found cheapest store: ${cheapestResult.bestStore.chainName}")

                        // Update cart items with best prices
                        updateCartWithBestPrices(cheapestResult)

                        _uiState.update { currentState ->
                            currentState.copy(
                                isAnalyzing = false,
                                cheapestCartResult = cheapestResult,
                                error = null,
                                analyzeProgress = 1f
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("CartViewModel", "Failed to find cheapest store", exception)
                        val errorMessage = getErrorMessage(exception)

                        _uiState.update { currentState ->
                            currentState.copy(
                                isAnalyzing = false,
                                error = errorMessage,
                                analyzeProgress = 0f
                            )
                        }
                    }
                ) ?: run {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isAnalyzing = false,
                            error = "Service not available. Please try again later.",
                            analyzeProgress = 0f
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error in findCheapestStore", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isAnalyzing = false,
                        error = "Unexpected error: ${e.message}",
                        analyzeProgress = 0f
                    )
                }
            }
        }
    }

    private fun updateCartWithBestPrices(result: CheapestCartResult) {
        // Update cart items with the best store and prices
        result.itemsBreakdown.forEach { breakdown ->
            val cartItem = _uiState.value.cartItems.find {
                it.itemName == breakdown.itemName
            }
            cartItem?.let {
                // This is a simplified update - in reality, you'd update the cart manager
                Log.d("CartViewModel", "Best price for ${breakdown.itemName}: ₪${breakdown.price} at ${result.bestStore.chainName}")
            }
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("No stores found") == true ->
                "No stores found with all the items in your cart. Try removing some items or searching in a different city."
            exception.message?.contains("Network") == true ->
                "Network error. Please check your connection and try again."
            exception.message?.contains("404") == true ->
                "Some items in your cart were not found. Please update your cart."
            exception.message?.contains("timeout") == true ->
                "Request timed out. Please try again."
            else ->
                exception.message ?: "Failed to find cheapest store. Please try again."
        }
    }

    fun onCityChange(city: String) {
        Log.d("CartViewModel", "City changed to: $city")

        tokenManager?.saveSelectedCity(city)
        _uiState.update { currentState ->
            currentState.copy(
                selectedCity = city,
                cheapestCartResult = null, // Reset result when city changes
                error = null,
                analyzeProgress = 0f
            )
        }
    }

    fun dismissResult() {
        Log.d("CartViewModel", "Dismissing cheapest cart result")

        _uiState.update { currentState ->
            currentState.copy(
                cheapestCartResult = null,
                analyzeProgress = 0f
            )
        }
    }

    fun dismissError() {
        _uiState.update { currentState ->
            currentState.copy(error = null)
        }
    }

    // Helper function to check if cart can be saved
    fun canSaveCart(): Boolean {
        return _uiState.value.isSaveEnabled && _uiState.value.cartItems.isNotEmpty()
    }

    // Helper function to get cart summary for display
    fun getCartSummary(): String {
        val itemCount = _uiState.value.totalItems
        val uniqueItems = _uiState.value.cartItems.size

        return when {
            itemCount == 0 -> "Empty cart"
            itemCount == 1 -> "1 item"
            uniqueItems == itemCount -> "$itemCount items"
            else -> "$itemCount items ($uniqueItems unique)"
        }
    }
}