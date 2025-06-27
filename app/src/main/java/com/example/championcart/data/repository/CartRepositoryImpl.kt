package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.models.cart.CartItemRequest
import com.example.championcart.data.models.cart.CheapestCartRequest
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
    private val cartManager: CartManager,
    private val preferencesManager: PreferencesManager
) : CartRepository {

    companion object {
        private const val TAG = "CartRepository"
    }

    override suspend fun saveCart(name: String): Flow<Result<String>> = flow {
        try {
            val cartItems = cartManager.cartItems.value
            Log.d(TAG, "Saving cart '$name' with ${cartItems.size} items")

            val request = SaveCartRequest(
                name = name,
                items = cartItems.map { item ->
                    CartItemRequest(
                        itemName = item.product.name,
                        quantity = item.quantity,
                        category = item.product.category
                    )
                }
            )

            val response = cartApi.saveCart(request)

            if (response.success && response.cartId != null) {
                Log.d(TAG, "Cart saved successfully with ID: ${response.cartId}")
                emit(Result.success(response.cartId))
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

            val response = cartApi.getSavedCarts()

            if (response.success && response.carts != null) {
                val savedCarts = response.carts.map { cart ->
                    SavedCart(
                        id = cart.id,
                        name = cart.name,
                        itemCount = cart.items.size,
                        totalItems = cart.items.sumOf { it.quantity },
                        createdAt = cart.createdAt
                    )
                }
                Log.d(TAG, "Found ${savedCarts.size} saved carts")
                emit(Result.success(savedCarts))
            } else {
                emit(Result.success(emptyList()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get saved carts error", e)
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
}