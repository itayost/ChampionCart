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
                    isLoading = false
                )
                calculateTotals()
            },
            onFailure = {
                Log.e(TAG, "Failed to load cart items", it)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load cart items"
                )
            }
        )
    }

    private fun calculateTotals() {
        val items = _uiState.value.items
        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal < 100) 15.0 else 0.0 // Free delivery over 100
        val savings = _uiState.value.cheapestOption?.savings ?: 0.0
        val total = subtotal - savings + deliveryFee

        _uiState.value = _uiState.value.copy(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            savings = savings,
            total = total
        )
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeItem(itemId)
                return@launch
            }

            cartRepository.updateQuantity(itemId, newQuantity).fold(
                onSuccess = {
                    val updatedItems = _uiState.value.items.map { item ->
                        if (item.id == itemId) {
                            item.copy(quantity = newQuantity)
                        } else {
                            item
                        }
                    }
                    _uiState.value = _uiState.value.copy(items = updatedItems)
                    calculateTotals()
                },
                onFailure = {
                    Log.e(TAG, "Failed to update quantity", it)
                }
            )
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(itemId).fold(
                onSuccess = {
                    val updatedItems = _uiState.value.items.filter { it.id != itemId }
                    _uiState.value = _uiState.value.copy(items = updatedItems)
                    calculateTotals()
                },
                onFailure = {
                    Log.e(TAG, "Failed to remove item", it)
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
                        subtotal = 0.0,
                        savings = 0.0,
                        deliveryFee = 0.0,
                        total = 0.0
                    )
                },
                onFailure = {
                    Log.e(TAG, "Failed to clear cart", it)
                }
            )
        }
    }

    fun proceedToCheckout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Implement checkout logic
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                showCheckoutSuccess = true
            )
        }
    }

    fun dismissCheckoutSuccess() {
        _uiState.value = _uiState.value.copy(showCheckoutSuccess = false)
    }

    fun selectStore(storeId: String) {
        _uiState.value = _uiState.value.copy(selectedStoreId = storeId)
        // Recalculate based on selected store
        calculateTotals()
    }

    fun findCheapestStore() {
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
                            savings = cheapestCart.total - _uiState.value.subtotal
                        ),
                        availableStores = cheapestCart.items.map { item ->
                            StoreOption(
                                storeId = item.itemCode,
                                name = cheapestCart.store,
                                city = cheapestCart.city,
                                totalPrice = item.totalPrice,
                                savings = 0.0
                            )
                        },
                        isCalculating = false
                    )
                    calculateTotals()
                },
                onFailure = {
                    Log.e(TAG, "Failed to find cheapest store", it)
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        error = "Failed to find cheapest store"
                    )
                }
            )
        }
    }

    fun addRecommendedProduct(product: GroupedProduct) {
        viewModelScope.launch {
            val lowestPrice = product.prices.minByOrNull { it.price }
            if (lowestPrice != null) {
                val cartProduct = Product(
                    itemCode = product.itemCode,
                    itemName = product.itemName,
                    chain = lowestPrice.chain,
                    storeId = lowestPrice.storeId,
                    price = lowestPrice.price,
                    city = lowestPrice.city
                )
                cartRepository.addToCart(cartProduct).fold(
                    onSuccess = {
                        loadCartItems()
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to add product", it)
                    }
                )
            }
        }
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

    fun saveCart(cartName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val cart = Cart(
                name = cartName,
                items = _uiState.value.items,
                total = _uiState.value.total,
                store = _uiState.value.selectedStoreId,
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

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}

// Updated CartUiState with all required properties
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val savings: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val selectedStoreId: String? = null,
    val availableStores: List<StoreOption> = emptyList(),
    val cheapestOption: CheapestOption? = null,
    val savedCarts: List<Cart> = emptyList(),
    val recommendedProducts: List<GroupedProduct> = emptyList(),
    val isLoading: Boolean = false,
    val isCalculating: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val showCheckoutSuccess: Boolean = false,
    val error: String? = null
)

data class CheapestOption(
    val store: String,
    val city: String,
    val total: Double,
    val savings: Double
)

data class StoreOption(
    val storeId: String,
    val name: String,
    val city: String?,
    val totalPrice: Double,
    val savings: Double = 0.0
)