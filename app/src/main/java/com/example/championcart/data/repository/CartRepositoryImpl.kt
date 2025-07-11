package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.api.ProductApi
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.mappers.toDomainModel
import com.example.championcart.data.models.cart.*
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val priceApi: PriceApi,
    private val productApi: ProductApi,
    private val priceRepository: PriceRepository,
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
                        barcode = item.product.barcode ?: item.product.id,
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

            val savedCarts = cartApi.getSavedCarts()

            val domainCarts = savedCarts.map { cart ->
                SavedCart(
                    id = cart.cartId.toString(),
                    name = cart.cartName,
                    itemCount = cart.itemCount,
                    totalItems = cart.itemCount, // API doesn't provide total quantity in list, only item count
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

            val response = cartApi.getCartDetails(cartId.toInt())

            if (response.success) {
                val cartDetails = response.cart

                // Clear current cart
                cartManager.clearCart()

                // Update the city to match the saved cart's city
                preferencesManager.setSelectedCity(cartDetails.city)
                Log.d(TAG, "Updated city to: ${cartDetails.city}")

                // Load each item from the saved cart using barcode
                var successCount = 0
                var failCount = 0

                cartDetails.items.forEach { cartItem ->
                    try {
                        // Use barcode to get product details
                        Log.d(TAG, "Loading product by barcode: ${cartItem.barcode}")

                        val productResponse = productApi.getProductByBarcode(
                            barcode = cartItem.barcode,
                            city = cartDetails.city
                        )

                        if (productResponse.available && productResponse.allPrices.isNotEmpty()) {
                            // Convert to domain model and add to cart
                            val product = productResponse.toDomainModel()
                            cartManager.addToCart(product, cartItem.quantity)
                            successCount++
                            Log.d(TAG, "Added product to cart: ${product.name} x${cartItem.quantity}")
                        } else {
                            failCount++
                            Log.w(TAG, "Product not available in city: ${cartItem.name} (barcode: ${cartItem.barcode})")
                        }
                    } catch (e: Exception) {
                        failCount++
                        Log.e(TAG, "Error loading product by barcode: ${cartItem.barcode}", e)
                    }
                }

                Log.d(TAG, "Cart loaded: $successCount items loaded successfully, $failCount failed")
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

            // Filter out items without barcodes
            val itemsWithBarcodes = cartItems.filter { !it.product.barcode.isNullOrEmpty() }

            if (itemsWithBarcodes.isEmpty()) {
                emit(Result.failure(Exception("No items with barcodes in cart")))
                return@flow
            }

            val searchCity = city ?: preferencesManager.getSelectedCity()

            Log.d(TAG, "Calculating cheapest store for ${itemsWithBarcodes.size} items in $searchCity")

            // Create request for /api/cart/compare endpoint
            val request = CartCompareRequest(
                city = searchCity,
                items = itemsWithBarcodes.map { item ->
                    CartCompareItem(
                        barcode = item.product.barcode!!, // We already filtered nulls
                        quantity = item.quantity,
                        name = item.product.name
                    )
                }
            )

            val response = cartApi.compareCart(request)

            if (response.success) {
                val cheapestStore = response.cheapestStore

                // Create a map of all store totals with proper display names
                val storeTotals = mutableMapOf<String, Double>()
                response.allStores.forEach { store ->
                    // Use the same format as cheapestStore for consistency
                    val storeKey = "${store.chainDisplayName} - ${store.branchName}"
                    storeTotals[storeKey] = store.totalPrice
                }

                // Get missing items
                val missingItems = mutableListOf<String>()
                cheapestStore.itemsDetail
                    .filter { !it.available }
                    .forEach { item ->
                        missingItems.add(item.name)
                    }

                val result = CheapestStoreResult(
                    cheapestStore = "${cheapestStore.chainDisplayName} - ${cheapestStore.branchName}",
                    address = cheapestStore.branchAddress, // Now properly mapped!
                    totalPrice = cheapestStore.totalPrice,
                    storeTotals = storeTotals,
                    missingItems = missingItems,
                    availableItems = cheapestStore.availableItems,
                    totalMissingItems = cheapestStore.missingItems
                )

                Log.d(TAG, "Cheapest store: ${result.cheapestStore} at ${result.address} - ₪${result.totalPrice}")
                emit(Result.success(result))
            } else {
                Log.e(TAG, "Calculate cheapest failed")
                emit(Result.failure(Exception("Failed to compare cart prices")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Calculate cheapest error", e)
            emit(Result.failure(e))
        }
    }
}