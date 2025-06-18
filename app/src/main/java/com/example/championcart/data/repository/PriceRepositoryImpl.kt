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
                    val groupedProducts = items.map { it.toDomainModel() }
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
                    val groupedProducts = items.map { it.toDomainModel() }
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
            val request = CheapestCartRequest(
                city = city,
                items = items.map { CartItem(it.itemName, it.quantity) }
            )

            val response = api.findCheapestCart(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Cheapest cart response: ${Gson().toJson(responseBody)}")

                responseBody?.let { cartResponse ->
                    val result = cartResponse.toDomainModel()
                    Result.success(result)
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
                val cities = response.body() ?: emptyList()
                Log.d("PriceRepository", "Cities list response: $cities")
                Result.success(cities)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get cities list failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get cities list error", e)
            Result.failure(e)
        }
    }

    override suspend fun getCitiesWithStores(): Result<List<String>> {
        return try {
            val response = api.getCitiesWithStores()

            if (response.isSuccessful) {
                val cities = response.body() ?: emptyList()
                Log.d("PriceRepository", "Cities with stores response: $cities")
                Result.success(cities)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Get cities with stores failed: ${parseErrorMessage(errorBody)}"))
            }
        } catch (e: Exception) {
            Log.e("PriceRepository", "Get cities with stores error", e)
            Result.failure(e)
        }
    }

    override suspend fun getStoreProducts(
        dbName: String,
        snifKey: String
    ): Result<List<Product>> {
        return try {
            val response = api.getStoreProducts(dbName, snifKey)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Store products response: ${Gson().toJson(responseBody)}")

                responseBody?.let { items ->
                    val products = items.map { it.toDomainModel() }
                    Result.success(products)
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

    override suspend fun getProductByItemCode(
        dbName: String,
        itemCode: String
    ): Result<List<Product>> {
        return try {
            val response = api.getProductByItemCode(dbName, itemCode)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PriceRepository", "Product by item code response: ${Gson().toJson(responseBody)}")

                responseBody?.let { items ->
                    val products = items.map { it.toDomainModel() }
                    Result.success(products)
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

    // ============ HELPER FUNCTIONS ============

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

// ============ EXTENSION FUNCTIONS TO CONVERT API MODELS TO DOMAIN MODELS ============

private fun GroupedProductResponse.toDomainModel(): GroupedProduct {
    // Handle both grouped products and single products
    val pricesList = if (prices != null) {
        // This is a grouped product with prices array
        prices.map { it.toDomainModel() }
    } else if (chain != null && storeId != null && price != null) {
        // This is a single product - create a single price entry
        listOf(
            StorePrice(
                chain = chain,
                storeId = storeId,
                price = price,
                originalName = itemName,
                timestamp = timestamp ?: ""
            )
        )
    } else {
        // No price information available
        emptyList()
    }

    return GroupedProduct(
        itemCode = itemCode,
        itemName = itemName,
        prices = pricesList,
        crossChain = crossChain ?: false,
        relevanceScore = relevanceScore ?: 0.0,
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
        bestDeal = bestDeal?.toDomainModel() ?: PriceDeal("", 0.0, ""), // Add null safety
        worstDeal = worstDeal?.toDomainModel() ?: PriceDeal("", 0.0, ""), // Add null safety
        savings = savings ?: 0.0,
        savingsPercent = savingsPercent ?: 0.0,
        identicalProduct = identicalProduct ?: false
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
        chain = chain ?: "",
        storeId = storeId ?: snifKey ?: "",
        price = price ?: itemPrice,
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