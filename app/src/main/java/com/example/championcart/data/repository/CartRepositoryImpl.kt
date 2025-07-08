package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.mappers.toDomainModel
import com.example.championcart.data.models.cart.*
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
                    totalItems = cart.itemCount,
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

                cartManager.clearCart()

                cartDetails.items.forEach { cartItem ->
                    try {
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

                // Create a map of all store totals
                val storeTotals = mutableMapOf<String, Double>()
                response.allStores.forEach { store ->
                    storeTotals[store.chainDisplayName] = store.totalPrice
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
                    totalPrice = cheapestStore.totalPrice,
                    storeTotals = storeTotals,
                    missingItems = missingItems
                )

                Log.d(TAG, "Cheapest store: ${result.cheapestStore} - ₪${result.totalPrice}")
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

    // Additional method to compare a saved cart
    suspend fun compareSavedCart(cartId: String): Flow<Result<CheapestStoreResult>> = flow {
        try {
            Log.d(TAG, "Comparing saved cart with ID: $cartId")

            val response = cartApi.compareSavedCart(cartId.toInt())

            if (response.success) {
                val comparison = response.comparison
                val result = CheapestStoreResult(
                    cheapestStore = "${comparison.cheapestStore.chainName} - ${comparison.cheapestStore.branchName}",
                    totalPrice = comparison.cheapestStore.totalPrice,
                    storeTotals = mapOf(
                        comparison.cheapestStore.chainName to comparison.cheapestStore.totalPrice
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