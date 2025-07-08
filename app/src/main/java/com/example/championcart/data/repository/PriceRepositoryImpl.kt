package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.api.ProductApi
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
    private val productApi: ProductApi,
    private val preferencesManager: PreferencesManager
) : PriceRepository {

    companion object {
        private const val TAG = "PriceRepository"
    }

    override suspend fun searchProducts(
        query: String,
        city: String?
    ): Flow<Result<List<Product>>> = flow {
        val searchCity = city ?: preferencesManager.getSelectedCity()
        Log.d(TAG, "Searching for '$query' in city: $searchCity")

        try {
            // Use the product search endpoint that returns barcodes
            val products = productApi.searchProducts(
                query = query,
                city = searchCity,
                limit = 20
            )

            val domainProducts = products.map { it.toDomainModel() }
            Log.d(TAG, "Found ${domainProducts.size} products with barcodes")
            emit(Result.success(domainProducts))

        } catch (e: Exception) {
            Log.e(TAG, "Search error for query: $query", e)

            // Fallback to the old price API if product search fails
            try {
                Log.d(TAG, "Falling back to price API")
                val response = priceApi.searchProductPrices(
                    city = searchCity,
                    itemName = query
                )

                if (response.success && response.data != null) {
                    val products = response.data.map { it.toDomainModel() }
                    Log.d(TAG, "Found ${products.size} products (no barcodes)")
                    emit(Result.success(products))
                } else {
                    Log.e(TAG, "Search failed: ${response.message}")
                    emit(Result.failure(Exception(response.message ?: "Search failed")))
                }
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Fallback search also failed", fallbackError)
                emit(Result.failure(fallbackError))
            }
        }
    }

    override suspend fun getProductDetails(
        productId: String,
        city: String?
    ): Flow<Result<Product>> = flow {
        val searchCity = city ?: preferencesManager.getSelectedCity()
        Log.d(TAG, "Getting details for product: $productId in city: $searchCity")

        try {
            // If productId is a barcode, search by barcode
            // Otherwise search by name
            val products = if (productId.matches(Regex("\\d+"))) {
                // Looks like a barcode - search for products and filter
                productApi.searchProducts(
                    query = "",  // Empty query to get all products
                    city = searchCity,
                    limit = 100
                ).filter { it.barcode == productId }
            } else {
                // Search by name
                productApi.searchProducts(
                    query = productId,
                    city = searchCity,
                    limit = 1
                )
            }

            if (products.isNotEmpty()) {
                val product = products.first().toDomainModel()
                emit(Result.success(product))
            } else {
                emit(Result.failure(Exception("Product not found")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product details", e)
            emit(Result.failure(e))
        }
    }

    override suspend fun getProductByBarcode(barcode: String, city: String?): Flow<Result<Product?>> = flow {
        try {
            val selectedCity = city ?: preferencesManager.getSelectedCity()
            Log.d(TAG, "Getting product by barcode: $barcode in city: $selectedCity")

            val response = productApi.getProductByBarcode(barcode, selectedCity)

            if (response.available && response.allPrices.isNotEmpty()) {
                val product = response.toDomainModel()
                Log.d(TAG, "Product found by barcode: ${product.name}")
                emit(Result.success(product))
            } else {
                Log.d(TAG, "Product not found by barcode")
                emit(Result.success(null))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product by barcode", e)
            emit(Result.failure(e))
        }
    }
}