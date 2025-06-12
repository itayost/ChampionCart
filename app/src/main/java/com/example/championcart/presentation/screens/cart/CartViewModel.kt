package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.CartItem as ApiCartItem
import com.example.championcart.data.repository.PriceRepositoryImpl
import com.example.championcart.di.NetworkModule
import com.example.championcart.domain.models.CheapestCartResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<com.example.championcart.data.local.CartItem> = emptyList(),
    val totalItems: Int = 0,
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val cheapestCartResult: CheapestCartResult? = null,
    val error: String? = null,
    val selectedCity: String = "Tel Aviv"
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

        // Observe cart items
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                _uiState.value = _uiState.value.copy(
                    cartItems = items,
                    totalItems = items.sumOf { it.quantity },
                    cheapestCartResult = null // Reset when cart changes
                )
            }
        }
    }

    fun updateQuantity(itemCode: String, quantity: Int) {
        cartManager.updateQuantity(itemCode, quantity)
    }

    fun removeFromCart(itemCode: String) {
        cartManager.removeFromCart(itemCode)
    }

    fun clearCart() {
        cartManager.clearCart()
    }

    fun findCheapestStore() {
        if (_uiState.value.cartItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, error = null)

            try {
                val cartProducts = _uiState.value.cartItems.map { cartItem ->
                    com.example.championcart.domain.models.CartProduct(
                        itemName = cartItem.itemName,
                        quantity = cartItem.quantity
                    )
                }

                priceRepository?.findCheapestCart(
                    city = _uiState.value.selectedCity,
                    items = cartProducts
                )?.fold(
                    onSuccess = { result ->
                        _uiState.value = _uiState.value.copy(
                            isAnalyzing = false,
                            cheapestCartResult = result
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isAnalyzing = false,
                            error = exception.message ?: "Failed to find cheapest store"
                        )
                    }
                ) ?: run {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        error = "Service not available"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(cheapestCartResult = null)
    }
}