package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.local.PreferencesManager
import com.example.championcart.data.mappers.toDomainModel
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val priceApi: PriceApi,
    private val preferencesManager: PreferencesManager
) : PriceRepository {

    companion object {
        private const val TAG = "PriceRepository"
    }

    override suspend fun searchProducts(
        query: String,
        city: String?
    ): Flow<Result<List<Product>>> = flow {
        try {
            val searchCity = city ?: preferencesManager.getSelectedCity()
            Log.d(TAG, "Searching for '$query' in city: $searchCity")

            val response = priceApi.searchProductPrices(
                city = searchCity,
                itemName = query
            )

            if (response.success && response.data != null) {
                val products = response.data.map { it.toDomainModel() }
                Log.d(TAG, "Found ${products.size} products")
                emit(Result.success(products))
            } else {
                Log.e(TAG, "Search failed: ${response.message}")
                emit(Result.failure(Exception(response.message ?: "Search failed")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Search error for query: $query", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun getProductDetails(
        productId: String,
        city: String?
    ): Flow<Result<Product>> = flow {
        try {
            val searchCity = city ?: preferencesManager.getSelectedCity()
            Log.d(TAG, "Getting details for product: $productId in city: $searchCity")

            // For now, we'll search by product name/id since there's no direct product detail endpoint
            val response = priceApi.searchProductPrices(
                city = searchCity,
                itemName = productId
            )

            if (response.success && response.data != null && response.data.isNotEmpty()) {
                val product = response.data.first().toDomainModel()
                emit(Result.success(product))
            } else {
                emit(Result.failure(Exception("Product not found")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product details", e)
            emit(Result.failure(e))
        }
    }
}