package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.local.CartManager
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.CartChangeEvent
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.CartSavings
import com.example.championcart.domain.repository.CartSummary
import com.example.championcart.domain.repository.CartValidationResult
import com.example.championcart.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartManager: CartManager,
    private val priceRepository: PriceRepository
) : CartRepository {

    private val _cartChangeEvents = MutableStateFlow<CartChangeEvent?>(null)

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartManager.cartItems.map { localCartItems ->
            localCartItems.map { localItem ->
                // Convert local CartItem to domain CartItem
                CartItem(
                    id = "${localItem.itemCode}-${System.currentTimeMillis()}",
                    productId = localItem.itemCode,
                    productName = localItem.itemName,
                    price = localItem.selectedPrice ?: 0.0,
                    quantity = localItem.quantity,
                    imageUrl = null,
                    selectedStore = localItem.selectedChain?.let { chain ->
                        Store.fromChainAndStoreId(chain, "001") // Default store ID
                    }
                )
            }
        }
    }

    override suspend fun getCartItemsList(): List<CartItem> {
        return cartManager.cartItems.value.map { localItem ->
            CartItem(
                id = "${localItem.itemCode}-${System.currentTimeMillis()}",
                productId = localItem.itemCode,
                productName = localItem.itemName,
                price = localItem.selectedPrice ?: 0.0,
                quantity = localItem.quantity,
                imageUrl = null,
                selectedStore = localItem.selectedChain?.let { chain ->
                    Store.fromChainAndStoreId(chain, "001")
                }
            )
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int): Result<Unit> {
        return try {
            // Add to cart using CartManager
            for (i in 0 until quantity) {
                cartManager.addToCart(
                    itemCode = product.itemCode,
                    itemName = product.itemName,
                    chain = product.chain,
                    price = product.price
                )
            }

            // Emit cart change event
            val cartItem = product.toCartItem(quantity)
            _cartChangeEvents.value = CartChangeEvent.ItemAdded(cartItem)

            Log.d("CartRepository", "Added ${product.itemName} x$quantity to cart")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Add to cart error", e)
            Result.failure(e)
        }
    }

    override suspend fun addCartItem(cartItem: CartItem): Result<Unit> {
        return try {
            for (i in 0 until cartItem.quantity) {
                cartManager.addToCart(
                    itemCode = cartItem.productId,
                    itemName = cartItem.productName,
                    chain = cartItem.selectedStore?.chain,
                    price = cartItem.price
                )
            }

            _cartChangeEvents.value = CartChangeEvent.ItemAdded(cartItem)
            Log.d("CartRepository", "Added cart item: ${cartItem.productName}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Add cart item error", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCartItemQuantity(itemId: String, quantity: Int): Result<Unit> {
        return try {
            val currentItems = getCartItemsList()
            val item = currentItems.find { it.id == itemId }

            if (item != null) {
                val oldQuantity = item.quantity

                if (quantity <= 0) {
                    removeFromCart(itemId)
                } else {
                    // Find by item name since CartManager uses item name
                    val localCartItem = cartManager.cartItems.value.find {
                        it.itemName == item.productName
                    }
                    localCartItem?.let {
                        cartManager.updateQuantity(it.itemCode, quantity)

                        val updatedItem = item.copy(quantity = quantity)
                        _cartChangeEvents.value = CartChangeEvent.ItemUpdated(updatedItem, oldQuantity)
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Cart item not found"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Update quantity error", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(itemId: String): Result<Unit> {
        return try {
            val currentItems = getCartItemsList()
            val item = currentItems.find { it.id == itemId }

            if (item != null) {
                val localCartItem = cartManager.cartItems.value.find {
                    it.itemName == item.productName
                }
                localCartItem?.let {
                    cartManager.removeFromCart(it.itemCode)
                    _cartChangeEvents.value = CartChangeEvent.ItemRemoved(itemId)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Cart item not found"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Remove from cart error", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFromCartByName(itemName: String): Result<Unit> {
        return try {
            val localCartItem = cartManager.cartItems.value.find { it.itemName == itemName }
            localCartItem?.let {
                cartManager.removeFromCart(it.itemCode)
                _cartChangeEvents.value = CartChangeEvent.ItemRemoved(it.itemCode)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Remove from cart by name error", e)
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartManager.clearCart()
            _cartChangeEvents.value = CartChangeEvent.CartCleared
            Log.d("CartRepository", "Cart cleared")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Clear cart error", e)
            Result.failure(e)
        }
    }

    override fun getCartItemsCount(): Flow<Int> {
        return cartManager.cartCount
    }

    override suspend fun getCurrentCartCount(): Int {
        return cartManager.cartItems.value.sumOf { it.quantity }
    }

    override suspend fun getCartTotalPrice(): Double {
        return cartManager.cartItems.value.sumOf {
            (it.selectedPrice ?: 0.0) * it.quantity
        }
    }

    override suspend fun getCartTotal(city: String): Double {
        return try {
            val cartProducts = getCartAsApiFormat()
            if (cartProducts.isEmpty()) return 0.0

            val result = priceRepository.findCheapestCart(city, cartProducts)
            result.getOrNull()?.totalPrice ?: 0.0
        } catch (e: Exception) {
            Log.e("CartRepository", "Get cart total error", e)
            0.0
        }
    }

    override suspend fun isProductInCart(itemCode: String): Boolean {
        return cartManager.cartItems.value.any { it.itemCode == itemCode }
    }

    override suspend fun isProductInCartByName(itemName: String): Boolean {
        return cartManager.cartItems.value.any { it.itemName == itemName }
    }

    override suspend fun getProductQuantity(itemCode: String): Int {
        return cartManager.cartItems.value.find { it.itemCode == itemCode }?.quantity ?: 0
    }

    override suspend fun saveCartState(): Result<Unit> {
        return try {
            // CartManager automatically saves to SharedPreferences
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadCartState(): Result<List<CartItem>> {
        return try {
            val cartItems = getCartItemsList()
            Result.success(cartItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartAsApiFormat(): List<CartProduct> {
        return cartManager.cartItems.value.map { localItem ->
            CartProduct(
                itemName = localItem.itemName,
                quantity = localItem.quantity
            )
        }
    }

    override suspend fun applyCheapestCartResult(result: CheapestCartResult): Result<Unit> {
        return try {
            // Update cart items with best store and prices
            val currentItems = cartManager.cartItems.value.toMutableList()

            currentItems.forEach { localItem ->
                val bestPrice = result.itemPrices[localItem.itemName]
                if (bestPrice != null) {
                    val index = currentItems.indexOf(localItem)
                    currentItems[index] = localItem.copy(
                        selectedChain = result.chain,
                        selectedPrice = bestPrice
                    )
                }
            }

            // Update CartManager (this would require extending CartManager to support this)
            // For now, we'll just emit the store selection event
            _cartChangeEvents.value = CartChangeEvent.StoreSelected(result.bestStore)

            Log.d("CartRepository", "Applied cheapest cart result: ${result.bestStore.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Apply cheapest cart result error", e)
            Result.failure(e)
        }
    }

    override suspend fun getCartSummary(): CartSummary {
        val items = getCartItemsList()
        return CartSummary(
            totalItems = items.sumOf { it.quantity },
            uniqueProducts = items.size,
            totalPrice = items.sumOf { it.getTotalPrice() },
            hasIncompleteItems = items.any { it.selectedStore == null },
            estimatedSavings = null // Would be calculated with cheapest cart API
        )
    }

    override fun observeCartChanges(): Flow<CartChangeEvent> {
        return _cartChangeEvents.asStateFlow().map { it ?: CartChangeEvent.CartCleared }
    }

    override suspend fun getCartItemsByStore(): Map<Store, List<CartItem>> {
        val items = getCartItemsList()
        return items.filter { it.selectedStore != null }
            .groupBy { it.selectedStore!! }
    }

    override suspend fun calculatePotentialSavings(city: String): Result<CartSavings> {
        return try {
            val cartProducts = getCartAsApiFormat()
            if (cartProducts.isEmpty()) {
                return Result.failure(Exception("Cart is empty"))
            }

            val cheapestResult = priceRepository.findCheapestCart(city, cartProducts)

            when (cheapestResult) {
                is Result.Success -> {
                    val result = cheapestResult.getOrNull()!!
                    val currentTotal = getCartTotalPrice()

                    val savings = CartSavings(
                        currentTotal = currentTotal,
                        bestTotal = result.totalPrice,
                        potentialSavings = currentTotal - result.totalPrice,
                        savingsPercentage = if (currentTotal > 0) {
                            ((currentTotal - result.totalPrice) / currentTotal) * 100
                        } else 0.0,
                        bestStore = result.bestStore
                    )
                    Result.success(savings)
                }
                is Result.Failure -> cheapestResult
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Calculate potential savings error", e)
            Result.failure(e)
        }
    }

    override suspend fun mergeCartItems(): Result<Unit> {
        return try {
            val items = cartManager.cartItems.value
            val mergedItems = items.groupBy { it.itemCode }
                .map { (_, group) ->
                    val first = group.first()
                    first.copy(quantity = group.sumOf { it.quantity })
                }

            // Clear and re-add merged items
            cartManager.clearCart()
            mergedItems.forEach { item ->
                for (i in 0 until item.quantity) {
                    cartManager.addToCart(
                        itemCode = item.itemCode,
                        itemName = item.itemName,
                        chain = item.selectedChain,
                        price = item.selectedPrice
                    )
                }
            }

            Log.d("CartRepository", "Cart items merged")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Merge cart items error", e)
            Result.failure(e)
        }
    }

    override suspend fun validateCartItems(city: String): Result<CartValidationResult> {
        return try {
            val cartItems = getCartItemsList()
            val validItems = mutableListOf<CartItem>()
            val invalidItems = mutableListOf<CartItem>()
            val updatedPrices = mutableListOf<CartItem>()
            val unavailableItems = mutableListOf<CartItem>()

            // For each cart item, try to find current pricing
            cartItems.forEach { cartItem ->
                try {
                    val searchResult = priceRepository.searchProducts(
                        city = city,
                        productName = cartItem.productName,
                        groupByCode = true,
                        limit = 1
                    )

                    when (searchResult) {
                        is Result.Success -> {
                            val products = searchResult.getOrNull()
                            if (products.isNullOrEmpty()) {
                                unavailableItems.add(cartItem)
                            } else {
                                val currentBestPrice = products.first().lowestPrice
                                if (currentBestPrice != null && currentBestPrice != cartItem.price) {
                                    updatedPrices.add(cartItem.copy(price = currentBestPrice))
                                } else {
                                    validItems.add(cartItem)
                                }
                            }
                        }
                        is Result.Failure -> invalidItems.add(cartItem)
                    }
                } catch (e: Exception) {
                    invalidItems.add(cartItem)
                }
            }

            val result = CartValidationResult(
                validItems = validItems,
                invalidItems = invalidItems,
                updatedPrices = updatedPrices,
                unavailableItems = unavailableItems
            )

            Result.success(result)
        } catch (e: Exception) {
            Log.e("CartRepository", "Validate cart items error", e)
            Result.failure(e)
        }
    }

    override suspend fun createShoppingList(name: String): Result<SavedCart> {
        return try {
            val cartItems = getCartItemsList()
            val savedCartItems = cartItems.map { cartItem ->
                SavedCartItem(
                    itemName = cartItem.productName,
                    quantity = cartItem.quantity,
                    price = cartItem.price
                )
            }

            val savedCart = SavedCart(
                cartName = name,
                city = "Tel Aviv", // Default city - should come from user preferences
                items = savedCartItems,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            Result.success(savedCart)
        } catch (e: Exception) {
            Log.e("CartRepository", "Create shopping list error", e)
            Result.failure(e)
        }
    }

    override suspend fun loadShoppingList(savedCart: SavedCart): Result<Unit> {
        return try {
            // Clear current cart
            clearCart()

            // Add items from saved cart
            savedCart.items.forEach { savedItem ->
                for (i in 0 until savedItem.quantity) {
                    cartManager.addToCart(
                        itemCode = savedItem.itemName.hashCode().toString(),
                        itemName = savedItem.itemName,
                        chain = null,
                        price = savedItem.price
                    )
                }
            }

            Log.d("CartRepository", "Loaded shopping list: ${savedCart.cartName}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Load shopping list error", e)
            Result.failure(e)
        }
    }
}