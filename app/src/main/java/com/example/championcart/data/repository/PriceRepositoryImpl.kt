package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.models.request.CartItem
import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.PriceRepository
import com.google.gson.Gson

class PriceRepositoryImpl(
    private val priceApi: PriceApi
) : PriceRepository {

    override suspend fun searchProducts(city: String, productName: String): Result<List<Product>> {
        return try {
            val response = priceApi.searchProducts(city, productName)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Raw response: ${Gson().toJson(responseBody)}")

                responseBody?.let { items ->
                    // Check if the response is already flat or nested
                    val products = mutableListOf<Product>()

                    items.forEach { item ->
                        when {
                            // Case 1: Response has nested prices array (grouped by item_code)
                            item.prices != null && item.prices.isNotEmpty() -> {
                                item.prices.forEach { storePrice ->
                                    if (item.itemCode != null && item.itemName != null &&
                                        storePrice.price != null && storePrice.chain != null) {
                                        products.add(
                                            Product(
                                                itemCode = item.itemCode,
                                                itemName = item.itemName,
                                                price = storePrice.price,
                                                chainName = storePrice.chain,
                                                storeName = "Store ${storePrice.storeId ?: "Unknown"}",
                                                storeAddress = "${storePrice.chain} - ${storePrice.storeId ?: "Unknown"}",
                                                lastUpdated = storePrice.timestamp ?: ""
                                            )
                                        )
                                    }
                                }
                            }
                            // Case 2: Response is already flat (each item is a store price)
                            // This handles the case where the API might return flat results
                            item.itemCode != null && item.itemName != null -> {
                                // Try to parse as a flat response structure
                                // You might need to adjust based on actual API response
                                products.add(
                                    Product(
                                        itemCode = item.itemCode,
                                        itemName = item.itemName,
                                        price = 0.0, // Will need to extract from actual response
                                        chainName = "Unknown",
                                        storeName = "Unknown",
                                        storeAddress = "Unknown",
                                        lastUpdated = ""
                                    )
                                )
                            }
                        }
                    }

                    Log.d("PriceRepository", "Parsed ${products.size} products")

                    // Sort by price to show cheapest first
                    val sortedProducts = products.sortedBy { it.price }
                    Result.success(sortedProducts)
                } ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PriceRepository", "Search failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Search failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Network error", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun getIdenticalProducts(city: String, productName: String): Result<List<Product>> {
        return try {
            val response = priceApi.getIdenticalProducts(city, productName)

            if (response.isSuccessful) {
                response.body()?.let { items ->
                    val products = mutableListOf<Product>()

                    // Process identical products similarly
                    items.forEach { item ->
                        item.prices?.forEach { storePrice ->
                            if (item.itemCode != null && item.itemName != null &&
                                storePrice.price != null && storePrice.chain != null) {
                                products.add(
                                    Product(
                                        itemCode = item.itemCode,
                                        itemName = item.itemName,
                                        price = storePrice.price,
                                        chainName = storePrice.chain,
                                        storeName = "Store ${storePrice.storeId ?: "Unknown"}",
                                        storeAddress = "${storePrice.chain} - ${storePrice.storeId ?: "Unknown"}",
                                        lastUpdated = storePrice.timestamp ?: ""
                                    )
                                )
                            }
                        }
                    }

                    Result.success(products.sortedBy { it.price })
                } ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Failed to get identical products: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun findCheapestCart(
        city: String,
        items: List<CartProduct>
    ): Result<CheapestCartResult> {
        return try {
            val request = CheapestCartRequest(
                city = city,
                items = items.map { CartItem(it.itemName, it.quantity) }
            )

            Log.d("PriceRepository", "Finding cheapest cart: ${Gson().toJson(request)}")

            val response = priceApi.findCheapestCart(request)

            if (response.isSuccessful) {
                // Log raw response first
                val rawResponse = response.body()
                val rawJson = Gson().toJson(rawResponse)
                Log.d("PriceRepository", "Raw cheapest cart response: $rawJson")

                response.body()?.let { cartResponse ->
                    // Check if we have the required fields
                    if (cartResponse.chain == null || cartResponse.storeId == null) {
                        Log.e("PriceRepository", "Missing chain or store_id in response")
                        return@let Result.failure<CheapestCartResult>(Exception("Invalid response from server"))
                    }

                    // Build items breakdown from item_prices
                    val itemsBreakdown = mutableListOf<CartItemBreakdown>()
                    cartResponse.items?.forEach { item ->
                        val price = cartResponse.itemPrices?.get(item.itemName) ?: 0.0
                        itemsBreakdown.add(
                            CartItemBreakdown(
                                itemName = item.itemName,
                                quantity = item.quantity,
                                price = price,
                                totalPrice = price * item.quantity
                            )
                        )
                    }

                    val result = CheapestCartResult(
                        bestStore = Store(
                            chainName = cartResponse.chain,
                            storeName = "Store ${cartResponse.storeId}",
                            address = "${cartResponse.chain} - ${cartResponse.city}"
                        ),
                        totalPrice = cartResponse.totalPrice,
                        savingsAmount = cartResponse.savings,
                        savingsPercentage = cartResponse.savingsPercent,
                        itemsBreakdown = itemsBreakdown
                    )
                    Result.success(result)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PriceRepository", "Failed to find cheapest cart: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to find cheapest cart: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Network error", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun getCitiesList(): Result<List<String>> {
        return try {
            val response = priceApi.getCitiesList()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get cities"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}