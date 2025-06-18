package com.example.championcart.presentation.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val priceRepository: PriceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CartViewModel"
    }

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow<String>("תל אביב")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadCities()
            loadCartItems()
            loadSavedCarts()
        }
    }

    private suspend fun loadCities() {
        priceRepository.getCities().fold(
            onSuccess = { citiesList ->
                _cities.value = citiesList
                if (citiesList.isNotEmpty() && !citiesList.contains(_selectedCity.value)) {
                    _selectedCity.value = citiesList.first()
                }
            },
            onFailure = {
                Log.e(TAG, "Failed to load cities", it)
            }
        )
    }

    private suspend fun loadCartItems() {
        cartRepository.getCartItems().fold(
            onSuccess = { items ->
                _uiState.value = _uiState.value.copy(
                    items = items,
                    totalPrice = calculateTotal(items)
                )
            },
            onFailure = {
                Log.e(TAG, "Failed to load cart items", it)
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load cart items"
                )
            }
        )
    }

    private suspend fun loadSavedCarts() {
        priceRepository.getSavedCarts().fold(
            onSuccess = { carts ->
                _uiState.value = _uiState.value.copy(savedCarts = carts)
            },
            onFailure = {
                Log.e(TAG, "Failed to load saved carts", it)
            }
        )
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
        // Recalculate cheapest cart for new city
        findCheapestCart()
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(productId)
            return
        }

        viewModelScope.launch {
            val updatedItems = _uiState.value.items.map { item ->
                if (item.productId == productId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }

            _uiState.value = _uiState.value.copy(
                items = updatedItems,
                totalPrice = calculateTotal(updatedItems)
            )

            // Update in repository
            cartRepository.updateQuantity(productId, newQuantity)
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId).fold(
                onSuccess = {
                    val updatedItems = _uiState.value.items.filter { it.productId != productId }
                    _uiState.value = _uiState.value.copy(
                        items = updatedItems,
                        totalPrice = calculateTotal(updatedItems)
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to remove item", it)
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to remove item"
                    )
                }
            )
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        items = emptyList(),
                        totalPrice = 0.0,
                        cheapestOption = null
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to clear cart", it)
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to clear cart"
                    )
                }
            )
        }
    }

    private fun calculateTotal(items: List<CartItem>): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun findCheapestCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculating = true)

            val cartProducts = _uiState.value.items.map { item ->
                CartProduct(
                    itemName = item.productName,
                    quantity = item.quantity
                )
            }

            priceRepository.getCheapestCartForProducts(
                products = cartProducts,
                city = _selectedCity.value
            ).fold(
                onSuccess = { cheapestCart ->
                    _uiState.value = _uiState.value.copy(
                        cheapestOption = CheapestOption(
                            store = cheapestCart.store,
                            city = cheapestCart.city,
                            total = cheapestCart.total,
                            savings = _uiState.value.totalPrice - cheapestCart.total
                        ),
                        isCalculating = false
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to calculate cheapest cart", it)
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        error = "Failed to find cheapest option"
                    )
                }
            )
        }
    }

    fun saveCart(cartName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Please login to save cart"
                )
                return@launch
            }

            val cart = Cart(
                name = cartName,
                items = _uiState.value.items,
                city = _selectedCity.value
            )

            priceRepository.saveCart(cart).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                    loadSavedCarts()
                },
                onFailure = {
                    Log.e(TAG, "Failed to save cart", it)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Failed to save cart"
                    )
                }
            )
        }
    }

    fun loadSavedCart(cart: Cart) {
        viewModelScope.launch {
            // Convert saved cart items to current cart items
            val cartItems = cart.items.map { item ->
                CartItem(
                    productName = item.productName,
                    quantity = item.quantity,
                    price = item.price
                )
            }

            _uiState.value = _uiState.value.copy(
                items = cartItems,
                totalPrice = calculateTotal(cartItems)
            )
        }
    }

    fun deleteSavedCart(cartName: String) {
        viewModelScope.launch {
            priceRepository.deleteCart(cartName).fold(
                onSuccess = {
                    loadSavedCarts()
                },
                onFailure = {
                    Log.e(TAG, "Failed to delete cart", it)
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to delete cart"
                    )
                }
            )
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val cheapestOption: CheapestOption? = null,
    val savedCarts: List<Cart> = emptyList(),
    val isCalculating: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

data class CheapestOption(
    val store: String,
    val city: String,
    val total: Double,
    val savings: Double
)