package com.example.championcart.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val selectedStore: Store? = null,
    val cheapestCartResult: CheapestCartResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSavingsAnimation: Boolean = false,
    val selectedCity: String = "Tel Aviv"
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val priceRepository: PriceRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadCart()
        observeCartChanges()
    }

    private fun loadCart() {
        viewModelScope.launch {
            // Load cart items from local storage
            val items = getMockCartItems() // In real app, load from repository
            updateCartState(items)
        }
    }

    private fun observeCartChanges() {
        // In real app, observe cart repository for changes
        viewModelScope.launch {
            state.map { it.cartItems }
                .distinctUntilChanged()
                .collect { items ->
                    calculateTotalPrice(items)
                }
        }
    }

    private fun updateCartState(items: List<CartItem>) {
        _state.update { currentState ->
            currentState.copy(
                cartItems = items,
                totalPrice = items.sumOf { it.price * it.quantity }
            )
        }
    }

    private fun calculateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf { it.price * it.quantity }
        _state.update { it.copy(totalPrice = total) }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val updatedItems = _state.value.cartItems.map { item ->
                if (item.id == itemId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
            updateCartState(updatedItems)
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val updatedItems = _state.value.cartItems.filter { it.id != itemId }
            updateCartState(updatedItems)

            // Reset cheapest result if cart changed
            _state.update { it.copy(cheapestCartResult = null, selectedStore = null) }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            updateCartState(emptyList())
            _state.update {
                it.copy(
                    cheapestCartResult = null,
                    selectedStore = null,
                    showSavingsAnimation = false
                )
            }
        }
    }

    fun findCheapestStore() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Simulate API call
                delay(1500)

                val cartItems = _state.value.cartItems
                if (cartItems.isEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Cart is empty"
                        )
                    }
                    return@launch
                }

                // Create mock result
                val result = createMockCheapestResult(cartItems)

                _state.update {
                    it.copy(
                        isLoading = false,
                        cheapestCartResult = result,
                        selectedStore = result.bestStore,
                        showSavingsAnimation = true
                    )
                }

                // Hide animation after delay
                delay(3000)
                _state.update { it.copy(showSavingsAnimation = false) }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to find cheapest store: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectStore(store: Store) {
        _state.update { it.copy(selectedStore = store) }
    }

    // Mock data functions
    private fun getMockCartItems(): List<CartItem> {
        return listOf(
            CartItem(
                id = "1",
                productId = "7290000042435",
                productName = "חלב תנובה 3% 1 ליטר",
                price = 6.90,
                quantity = 2,
                imageUrl = null
            ),
            CartItem(
                id = "2",
                productId = "7290000156378",
                productName = "לחם אחיד פרוס",
                price = 7.50,
                quantity = 1,
                imageUrl = null
            ),
            CartItem(
                id = "3",
                productId = "7290000234567",
                productName = "ביצים L 12 יחידות",
                price = 12.90,
                quantity = 1,
                imageUrl = null
            ),
            CartItem(
                id = "4",
                productId = "7290000345678",
                productName = "במבה אוסם 80 גרם",
                price = 4.50,
                quantity = 3,
                imageUrl = null
            )
        )
    }

    private fun createMockCheapestResult(items: List<CartItem>): CheapestCartResult {
        val bestStore = Store(
            id = "shufersal-001",
            chain = "Shufersal",
            name = "Shufersal Deal",
            address = "Dizengoff 123, Tel Aviv"
        )

        val itemsBreakdown = items.map { item ->
            CartItemBreakdown(
                itemName = item.productName,
                quantity = item.quantity,
                price = item.price * 0.85, // 15% cheaper at best store
                totalPrice = item.price * item.quantity * 0.85
            )
        }

        val totalPrice = itemsBreakdown.sumOf { it.totalPrice }
        val originalTotal = items.sumOf { it.price * it.quantity }
        val savings = originalTotal - totalPrice
        val savingsPercentage = savings / originalTotal

        return CheapestCartResult(
            bestStore = bestStore,
            totalPrice = totalPrice,
            savingsAmount = savings,
            savingsPercentage = savingsPercentage,
            itemsBreakdown = itemsBreakdown,
            allStores = listOf(
                bestStore,
                Store(
                    id = "victory-052",
                    chain = "Victory",
                    name = "Victory Supermarket",
                    address = "Ibn Gabirol 89, Tel Aviv"
                ),
                Store(
                    id = "shufersal-007",
                    chain = "Shufersal",
                    name = "Shufersal Express",
                    address = "Rothschild 45, Tel Aviv"
                )
            )
        )
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

// Cart item model
data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)

// Extension to domain model if needed
fun CartItemBreakdown.toCartItem(): CartItem {
    return CartItem(
        id = "", // Generate ID
        productId = "", // Would need product ID
        productName = itemName,
        price = price,
        quantity = quantity
    )
}

// Alternative ViewModel without Hilt for testing
class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val priceRepository: PriceRepository,
    private val tokenManager: TokenManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository, priceRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}