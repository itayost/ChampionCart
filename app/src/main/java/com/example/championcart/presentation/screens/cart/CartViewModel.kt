package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.models.CartItem
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.usecase.cart.CalculateCheapestStoreUseCase
import com.example.championcart.domain.usecase.cart.SaveCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val cheapestStoreResult: CheapestStoreResult? = null,
    val potentialSavings: Double? = null,
    val isLoading: Boolean = false,
    val isCalculating: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager,
    private val tokenManager: TokenManager,
    private val calculateCheapestStoreUseCase: CalculateCheapestStoreUseCase,
    private val saveCartUseCase: SaveCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCartChanges()
    }

    private fun observeCartChanges() {
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                val totalPrice = items.sumOf { it.product.bestPrice * it.quantity }
                _uiState.update { state ->
                    state.copy(
                        cartItems = items,
                        totalPrice = totalPrice
                    )
                }
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        cartManager.updateQuantity(productId, quantity)

        // Clear cheapest store result as cart changed
        _uiState.update { it.copy(cheapestStoreResult = null, potentialSavings = null) }
    }

    fun removeFromCart(productId: String) {
        cartManager.removeFromCart(productId)

        // Clear cheapest store result as cart changed
        _uiState.update { it.copy(cheapestStoreResult = null, potentialSavings = null) }

        _uiState.update { it.copy(message = "המוצר הוסר מהעגלה") }
    }

    fun clearCart() {
        cartManager.clearCart()
        _uiState.update {
            it.copy(
                cheapestStoreResult = null,
                potentialSavings = null,
                message = "העגלה נוקתה"
            )
        }
    }

    fun calculateCheapestStore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true) }

            calculateCheapestStoreUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            message = "שגיאה בחישוב החנות הזולה: ${error.message}"
                        )
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { cheapestResult ->
                            val currentTotal = _uiState.value.totalPrice
                            val savings = currentTotal - cheapestResult.totalPrice

                            _uiState.update {
                                it.copy(
                                    isCalculating = false,
                                    cheapestStoreResult = cheapestResult,
                                    potentialSavings = if (savings > 0) savings else 0.0,
                                    message = "נמצאה החנות הזולה ביותר: ${cheapestResult.cheapestStore}"
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    isCalculating = false,
                                    message = "לא ניתן לחשב את החנות הזולה: ${error.message}"
                                )
                            }
                        }
                    )
                }
        }
    }

    fun saveCart(name: String) {
        if (!tokenManager.isLoggedIn()) {
            _uiState.update {
                it.copy(message = "יש להתחבר כדי לשמור עגלות")
            }
            return
        }

        if (name.isBlank()) {
            _uiState.update {
                it.copy(message = "יש להזין שם לעגלה")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            saveCartUseCase(name)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            message = "שגיאה בשמירת העגלה: ${error.message}"
                        )
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { cartId ->
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    message = "העגלה נשמרה בהצלחה!"
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    message = "לא ניתן לשמור את העגלה: ${error.message}"
                                )
                            }
                        }
                    )
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}