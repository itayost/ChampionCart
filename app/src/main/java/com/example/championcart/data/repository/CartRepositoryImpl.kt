package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.mappers.toDomainModel
import com.example.championcart.data.models.cart.CartItemRequest
import com.example.championcart.data.models.cart.CheapestCartRequest
import com.example.championcart.data.models.cart.SaveCartItemRequest
import com.example.championcart.data.models.cart.SaveCartRequest
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val priceApi: PriceApi,
    private val cartManager: CartManager,
    private val preferencesManager: PreferencesManager
) : CartRepository {

    companion object {
        private const val TAG = "CartRepository"
    }

    override suspend fun saveCart(name: String): Flow<Result<String>> = flow {
        try {
            val cartItems = cartManager.cartItems.value
            val city = preferencesManager.getSelectedCity()
            Log.d(TAG, "Saving cart '$name' with ${cartItems.size} items for city: $city")

            val request = SaveCartRequest(
                cartName = name,
                city = city,
                items = cartItems.map { item ->
                    SaveCartItemRequest(
                        barcode = item.product.barcode ?: item.product.id, // Use barcode if available
                        quantity = item.quantity,
                        name = item.product.name
                    )
                }
            )

            val response = cartApi.saveCart(request)

            if (response.success) {
                Log.d(TAG, "Cart saved successfully with ID: ${response.cartId}")
                emit(Result.success(response.cartId.toString()))
            } else {
                Log.e(TAG, "Save cart failed: ${response.message}")
                emit(Result.failure(Exception(response.message ?: "Failed to save cart")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Save cart error", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun getSavedCarts(): Flow<Result<List<SavedCart>>> = flow {
        try {
            Log.d(TAG, "Fetching saved carts")

            val savedCarts = cartApi.getSavedCarts() // Returns List<SavedCartSummary>

            val domainCarts = savedCarts.map { cart ->
                SavedCart(
                    id = cart.cartId.toString(),
                    name = cart.cartName,
                    itemCount = cart.itemCount,
                    totalItems = cart.itemCount, // Server doesn't provide total quantity
                    createdAt = cart.createdAt
                )
            }

            Log.d(TAG, "Found ${domainCarts.size} saved carts")
            emit(Result.success(domainCarts))
        } catch (e: Exception) {
            Log.e(TAG, "Get saved carts error", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun loadSavedCart(cartId: String): Flow<Result<Unit>> = flow {
        try {
            Log.d(TAG, "Loading saved cart with ID: $cartId")

            // Get cart details from server
            val response = cartApi.getCartDetails(cartId.toInt())

            if (response.success) {
                val cartDetails = response.cart

                // Clear current cart
                cartManager.clearCart()

                // For each item in the saved cart, search for the product by name
                // since we don't have barcode lookup implemented
                cartDetails.items.forEach { cartItem ->
                    try {
                        // Search for the product to get current prices
                        val productResponse = priceApi.searchProductPrices(
                            city = cartDetails.city,
                            itemName = cartItem.name
                        )

                        if (productResponse.success && productResponse.data != null && productResponse.data.isNotEmpty()) {
                            val product = productResponse.data.first().toDomainModel()
                            cartManager.addToCart(product, cartItem.quantity)
                        } else {
                            Log.w(TAG, "Product not found: ${cartItem.name}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading product: ${cartItem.name}", e)
                    }
                }

                Log.d(TAG, "Cart loaded successfully")
                emit(Result.success(Unit))
            } else {
                Log.e(TAG, "Failed to get cart details")
                emit(Result.failure(Exception("Failed to load cart")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Load cart error", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun calculateCheapestStore(
        city: String?
    ): Flow<Result<CheapestStoreResult>> = flow {
        try {
            val cartItems = cartManager.cartItems.value
            val searchCity = city ?: preferencesManager.getSelectedCity()

            Log.d(TAG, "Calculating cheapest store for ${cartItems.size} items in $searchCity")

            // Using the /api/cheapest-cart endpoint with item names
            val request = CheapestCartRequest(
                city = searchCity,
                items = cartItems.map { item ->
                    CartItemRequest(
                        itemName = item.product.name,
                        quantity = item.quantity,
                        category = item.product.category
                    )
                }
            )

            val response = cartApi.calculateCheapestCart(request)

            if (response.success && response.data != null) {
                val result = CheapestStoreResult(
                    cheapestStore = response.data.cheapestStore,
                    totalPrice = response.data.totalPrice,
                    storeTotals = response.data.storeTotals,
                    missingItems = response.data.missingItems ?: emptyList()
                )
                Log.d(TAG, "Cheapest store: ${result.cheapestStore} - ₪${result.totalPrice}")
                emit(Result.success(result))
            } else {
                Log.e(TAG, "Calculate cheapest failed: ${response.message}")
                emit(Result.failure(Exception(response.message ?: "Failed to calculate")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Calculate cheapest error", e)
            emit(Result.failure(e))
        }
    }

    // Additional method to compare a saved cart
    suspend fun compareSavedCart(cartId: String): Flow<Result<CheapestStoreResult>> = flow {
        try {
            Log.d(TAG, "Comparing saved cart with ID: $cartId")

            val response = cartApi.compareCart(cartId.toInt())

            if (response.success) {
                val comparison = response.comparison
                val result = CheapestStoreResult(
                    cheapestStore = "${comparison.cheapestStore.chainName} - ${comparison.cheapestStore.branchName}",
                    totalPrice = comparison.cheapestStore.totalPrice,
                    storeTotals = mapOf(
                        comparison.cheapestStore.chainName to comparison.cheapestStore.totalPrice
                        // Server doesn't provide other store totals in this endpoint
                    ),
                    missingItems = if (comparison.cheapestStore.missingItems > 0) {
                        listOf("${comparison.cheapestStore.missingItems} פריטים חסרים")
                    } else {
                        emptyList()
                    }
                )
                emit(Result.success(result))
            } else {
                emit(Result.failure(Exception("Failed to compare cart")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Compare cart error", e)
            emit(Result.failure(e))
        }
    }
}