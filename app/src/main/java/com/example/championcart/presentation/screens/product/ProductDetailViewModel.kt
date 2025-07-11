package com.example.championcart.presentation.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null,
    val isInCart: Boolean = false,
    val cartQuantity: Int = 0
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val cartManager: CartManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // First try to get by barcode if productId looks like a barcode
                if (productId.matches(Regex("\\d+"))) {
                    // Try barcode lookup first
                    priceRepository.getProductByBarcode(
                        barcode = productId,
                        city = preferencesManager.getSelectedCity()
                    ).collect { result ->
                        result.fold(
                            onSuccess = { productData ->
                                if (productData != null) {
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        product = productData,
                                        error = null
                                    ) }
                                    checkCartStatus(productData)
                                } else {
                                    // Product not available in this city
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        error = "המוצר לא זמין בעיר הנבחרת"
                                    ) }
                                }
                            },
                            onFailure = { error ->
                                // If barcode lookup fails, try regular product search
                                loadProductBySearch(productId)
                            }
                        )
                    }
                } else {
                    // Not a barcode, search by name/ID
                    loadProductBySearch(productId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "שגיאה בטעינת המוצר: ${e.message}"
                ) }
            }
        }
    }

    private suspend fun loadProductBySearch(productId: String) {
        priceRepository.getProductDetails(
            productId = productId,
            city = preferencesManager.getSelectedCity()
        ).collect { result ->
            result.fold(
                onSuccess = { product ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        product = product,
                        error = null
                    ) }
                    checkCartStatus(product)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "לא ניתן למצוא את המוצר"
                    ) }
                }
            )
        }
    }

    private fun checkCartStatus(product: Product) {
        viewModelScope.launch {
            cartManager.cartItems.collect { items ->
                val cartItem = items.find { it.product.id == product.id }
                _uiState.update { it.copy(
                    isInCart = cartItem != null,
                    cartQuantity = cartItem?.quantity ?: 0
                ) }
            }
        }
    }

    fun addToCart(product: Product) {
        cartManager.addToCart(product)
        _uiState.update { it.copy(
            isInCart = true,
            cartQuantity = it.cartQuantity + 1
        ) }
    }

    fun removeFromCart(product: Product) {
        cartManager.removeFromCart(product.id)
        _uiState.update { it.copy(
            isInCart = false,
            cartQuantity = 0
        ) }
    }

    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity > 0) {
            cartManager.updateQuantity(product.id, quantity)
            _uiState.update { it.copy(
                cartQuantity = quantity
            ) }
        } else {
            removeFromCart(product)
        }
    }
}