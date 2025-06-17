package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.models.request.CartItem
import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.data.models.response.*
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.PriceRepository
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val api: ChampionCartApi
) : PriceRepository {

    override suspend fun searchProducts(
        city: String,
        productName: String,
        groupByCode: Boolean,
        limit: Int?
    ): Result<List<GroupedProduct>> {
        return try {
            val response = api.searchProducts(city, productName, groupByCode, limit)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Search response: ${Gson().toJson(responseBody)}")

                responseBody?.let { items ->
                    val groupedProducts = items.map { item ->
                        item.toDomainModel()
                    }
                    Result.success(groupedProducts)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Search failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Search error", e)
            Result.failure(e)
        }
    }

    override suspend fun getIdenticalProducts(
        city: String,
        productName: String,
        limit: Int?
    ): Result<List<GroupedProduct>> {
        return try {
            val response = api.getIdenticalProducts(city, productName, limit)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Identical products response: ${Gson().toJson(responseBody)}")

                responseBody?.let { items ->
                    val groupedProducts = items.map { item ->
                        item.toDomainModel()
                    }
                    Result.success(groupedProducts)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get identical products failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get identical products error", e)
            Result.failure(e)
        }
    }

    override suspend fun findCheapestCart(
        city: String,
        items: List<CartProduct>
    ): Result<CheapestCartResult> {
        return try {
            val requestItems = items.map { cartProduct ->
                CartItem(
                    itemName = cartProduct.itemName,
                    quantity = cartProduct.quantity
                )
            }

            val request = CheapestCartRequest(city, requestItems)
            val response = api.findCheapestCart(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Cheapest cart response: ${Gson().toJson(responseBody)}")

                responseBody?.let { cheapestResponse ->
                    val domainResult = cheapestResponse.toDomainModel()
                    Result.success(domainResult)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Find cheapest cart failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Find cheapest cart error", e)
            Result.failure(e)
        }
    }

    override suspend fun getCitiesList(): Result<List<String>> {
        return try {
            val response = api.getCitiesList()

            if (response.isSuccessful) {
                val cities = response.body()
                cities?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty cities list"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get cities failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get cities error", e)
            Result.failure(e)
        }
    }

    override suspend fun getCitiesWithStores(): Result<List<String>> {
        return try {
            val response = api.getCitiesWithStores()

            if (response.isSuccessful) {
                val cities = response.body()
                cities?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty cities with stores list"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get cities with stores failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get cities with stores error", e)
            Result.failure(e)
        }
    }

    override suspend fun getStoreProducts(dbName: String, snifKey: String): Result<List<Product>> {
        return try {
            val response = api.getStoreProducts(dbName, snifKey)

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let { products ->
                    val domainProducts = products.map { it.toDomainModel() }
                    Result.success(domainProducts)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get store products failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get store products error", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductByItemCode(dbName: String, itemCode: String): Result<List<Product>> {
        return try {
            val response = api.getProductByItemCode(dbName, itemCode)

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let { products ->
                    val domainProducts = products.map { it.toDomainModel() }
                    Result.success(domainProducts)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get product by item code failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get product by item code error", e)
            Result.failure(e)
        }
    }

    override suspend fun searchProductsWithFilters(
        query: SearchQuery,
        filters: SearchFilters
    ): Result<SearchResult> {
        // Use basic search and apply filters locally for now
        return try {
            val searchResult = searchProducts(query.city, query.query, query.groupByCode, query.limit)

            when (searchResult) {
                is Result.Success -> {
                    val filteredProducts = applyFilters(searchResult.getOrNull() ?: emptyList(), filters)
                    val result = SearchResult(
                        query = query,
                        products = filteredProducts,
                        totalResults = filteredProducts.size
                    )
                    Result.success(result)
                }
                is Result.Failure -> searchResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecommendations(city: String, limit: Int): Result<List<GroupedProduct>> {
        // Mock implementation - could be replaced with actual API endpoint
        return searchProducts(city, "חלב", true, limit)
    }

    override suspend fun getTrendingProducts(city: String, limit: Int): Result<List<GroupedProduct>> {
        // Mock implementation - could be replaced with actual API endpoint
        return searchProducts(city, "במבה", true, limit)
    }

    override suspend fun getProductsOnSale(city: String, limit: Int): Result<List<GroupedProduct>> {
        // Mock implementation - could be replaced with actual API endpoint
        return searchProducts(city, "מבצע", true, limit)
    }

    override suspend fun getProductPriceHistory(
        itemCode: String,
        city: String,
        days: Int
    ): Result<List<PriceHistoryPoint>> {
        // Mock implementation
        return Result.success(emptyList())
    }

    override suspend fun createPriceAlert(
        itemCode: String,
        city: String,
        targetPrice: Double,
        userEmail: String
    ): Result<PriceAlert> {
        // Mock implementation
        return Result.failure(Exception("Price alerts not implemented yet"))
    }

    override suspend fun getUserPriceAlerts(userEmail: String): Result<List<PriceAlert>> {
        // Mock implementation
        return Result.success(emptyList())
    }

    override suspend fun deletePriceAlert(alertId: String): Result<Unit> {
        // Mock implementation
        return Result.success(Unit)
    }

    // Helper functions
    private fun applyFilters(products: List<GroupedProduct>, filters: SearchFilters): List<GroupedProduct> {
        var filtered = products

        // Apply chain filter
        if (filters.chains.isNotEmpty()) {
            filtered = filtered.filter { product ->
                product.prices.any { price ->
                    filters.chains.contains(price.chain)
                }
            }
        }

        // Apply price filter
        if (filters.minPrice != null || filters.maxPrice != null) {
            filtered = filtered.filter { product ->
                val lowestPrice = product.lowestPrice
                lowestPrice != null &&
                        (filters.minPrice == null || lowestPrice >= filters.minPrice) &&
                        (filters.maxPrice == null || lowestPrice <= filters.maxPrice)
            }
        }

        // Apply on sale filter
        if (filters.showOnSaleOnly) {
            // This would need more data from the API to determine if items are on sale
            // For now, just return all products
        }

        // Apply sorting
        return when (filters.sortBy) {
            SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.lowestPrice ?: Double.MAX_VALUE }
            SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.lowestPrice ?: 0.0 }
            SortOption.NAME_A_TO_Z -> filtered.sortedBy { it.itemName }
            SortOption.NAME_Z_TO_A -> filtered.sortedByDescending { it.itemName }
            SortOption.SAVINGS_HIGH_TO_LOW -> filtered.sortedByDescending { it.savings }
            SortOption.RELEVANCE -> filtered // Keep original order for relevance
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ApiErrorResponse::class.java)
                errorResponse.detail
            } else {
                "Unknown error"
            }
        } catch (e: Exception) {
            errorBody ?: "Unknown error"
        }
    }
}

// Extension functions to convert API models to domain models
private fun GroupedProductResponse.toDomainModel(): GroupedProduct {
    return GroupedProduct(
        itemCode = itemCode,
        itemName = itemName,
        prices = prices.map { it.toDomainModel() },
        crossChain = crossChain,
        relevanceScore = relevanceScore,
        priceComparison = priceComparison?.toDomainModel(),
        weight = weight,
        unit = unit,
        pricePerUnit = pricePerUnit
    )
}

private fun StorePriceResponse.toDomainModel(): StorePrice {
    return StorePrice(
        chain = chain,
        storeId = storeId,
        price = price,
        originalName = originalName,
        timestamp = timestamp
    )
}

private fun PriceComparisonResponse.toDomainModel(): PriceComparison {
    return PriceComparison(
        bestDeal = bestDeal.toDomainModel(),
        worstDeal = worstDeal.toDomainModel(),
        savings = savings,
        savingsPercent = savingsPercent,
        identicalProduct = identicalProduct
    )
}

private fun PriceDealResponse.toDomainModel(): PriceDeal {
    return PriceDeal(
        chain = chain,
        price = price,
        storeId = storeId
    )
}

private fun ProductResponse.toDomainModel(): Product {
    return Product(
        itemCode = itemCode,
        itemName = itemName,
        chain = chain,
        storeId = storeId,
        price = price,
        timestamp = timestamp,
        relevanceScore = relevanceScore,
        weight = weight,
        unit = unit,
        pricePerUnit = pricePerUnit
    )
}

private fun CheapestCartResponse.toDomainModel(): CheapestCartResult {
    return CheapestCartResult(
        chain = chain,
        storeId = storeId,
        totalPrice = totalPrice,
        worstPrice = worstPrice,
        savings = savings,
        savingsPercent = savingsPercent,
        city = city,
        items = items.map { CartProduct(it.itemName, it.quantity) },
        itemPrices = itemPrices,
        allStores = allStores.map {
            StoreOption(it.chain, it.storeId, it.totalPrice)
        }
    )
}