package com.example.championcart.data.repository

import com.example.championcart.data.api.PriceApi
import com.example.championcart.data.models.request.CartItem
import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.PriceRepository

class PriceRepositoryImpl(
    private val priceApi: PriceApi
) : PriceRepository {

    override suspend fun searchProducts(city: String, productName: String): Result<List<Product>> {
        return try {
            val response = priceApi.searchProducts(city, productName)
            if (response.isSuccessful) {
                response.body()?.let { items ->
                    // Flatten the grouped products into individual store products
                    val products = mutableListOf<Product>()

                    items.forEach { item ->
                        // For each product, create entries for each store price
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
                                        storeAddress = "${storePrice.chain} - ${storePrice.storeId ?: "Unknown location"}",
                                        lastUpdated = storePrice.timestamp ?: ""
                                    )
                                )
                            }
                        }
                    }

                    // Sort by price to show cheapest first
                    val sortedProducts = products.sortedBy { it.price }
                    Result.success(sortedProducts)
                } ?: Result.success(emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Search failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Keep other methods as they are...
    override suspend fun getIdenticalProducts(city: String, productName: String): Result<List<Product>> {
        return searchProducts(city, productName) // For now, use the same logic
    }

    override suspend fun findCheapestCart(
        city: String,
        items: List<CartProduct>
    ): Result<CheapestCartResult> {
        // Keep existing implementation
        return try {
            val request = CheapestCartRequest(
                city = city,
                items = items.map { CartItem(it.itemName, it.quantity) }
            )
            val response = priceApi.findCheapestCart(request)
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    val result = CheapestCartResult(
                        bestStore = Store(
                            chainName = cartResponse.bestStore.chainName ?: "Unknown",
                            storeName = cartResponse.bestStore.storeName ?: "Unknown",
                            address = cartResponse.bestStore.address ?: "No address"
                        ),
                        totalPrice = cartResponse.totalPrice,
                        savingsAmount = cartResponse.savingsAmount,
                        savingsPercentage = cartResponse.savingsPercentage,
                        itemsBreakdown = cartResponse.itemsBreakdown.map { breakdown ->
                            CartItemBreakdown(
                                itemName = breakdown.itemName,
                                quantity = breakdown.quantity,
                                price = breakdown.price,
                                totalPrice = breakdown.totalPrice
                            )
                        }
                    )
                    Result.success(result)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to find cheapest cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.failure(e)
        }
    }
}