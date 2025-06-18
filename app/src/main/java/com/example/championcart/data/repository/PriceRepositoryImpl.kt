package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.models.request.CartItem
import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.data.models.response.*
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.PriceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val api: ChampionCartApi
) : PriceRepository {

    companion object {
        private const val TAG = "PriceRepository"
    }

    override suspend fun searchProducts(
        query: String,
        city: String?,
        limit: Int
    ): Result<List<GroupedProduct>> {
        return try {
            Log.d(TAG, "Searching products: query=$query, city=$city, limit=$limit")
            val response = api.searchProducts(query, city, limit)
            Log.d(TAG, "Search response received: ${response.size} products")

            val products = response.map { it.toDomainModel() }
            Log.d(TAG, "Successfully mapped ${products.size} products")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products", e)
            Result.failure(e)
        }
    }

    override suspend fun getCheapestCart(
        items: List<CartItem>,
        city: String
    ): Result<CheapestCart> {
        return try {
            Log.d(TAG, "Getting cheapest cart for ${items.size} items in $city")
            val request = CheapestCartRequest(items = items, city = city)
            val response = api.getCheapestCart(request)
            Log.d(TAG, "Cheapest cart response: store=${response.store}, total=${response.total}")
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cheapest cart", e)
            Result.failure(e)
        }
    }

    override suspend fun getSavedCarts(): Result<List<Cart>> {
        return try {
            Log.d(TAG, "Fetching saved carts")
            val response = api.getSavedCarts()
            Log.d(TAG, "Received ${response.size} saved carts")
            val carts = response.map { it.toDomainModel() }
            Result.success(carts)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching saved carts", e)
            Result.failure(e)
        }
    }

    override suspend fun saveCart(cart: Cart): Result<Unit> {
        return try {
            Log.d(TAG, "Saving cart: ${cart.name}")
            val request = cart.toRequest()
            api.saveCart(request)
            Log.d(TAG, "Cart saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cart", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCart(cartName: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting cart: $cartName")
            api.deleteCart(cartName)
            Log.d(TAG, "Cart deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting cart", e)
            Result.failure(e)
        }
    }
}

// Extension functions for converting between data and domain models

private fun GroupedProductResponse.toDomainModel(): GroupedProduct {
    // Check if this is a single product (has direct price fields) or grouped product (has prices array)
    val pricesList = if (prices != null) {
        // This is a grouped product with multiple prices
        prices.map { it.toDomainModel() }
    } else if (chain != null && storeId != null && price != null) {
        // This is a single product with direct price fields
        listOf(
            StorePrice(
                chain = chain,
                storeId = storeId,
                price = price,
                city = city,
                storeName = storeName
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
        quantity = quantity,
        unitOfMeasure = unitOfMeasure,
        manufacturer = manufacturer,
        priceComparison = priceComparison?.toDomainModel()
    )
}

private fun StorePriceResponse.toDomainModel(): StorePrice {
    return StorePrice(
        chain = chain,
        storeId = storeId,
        price = price,
        city = city,
        storeName = storeName,
        lastUpdated = lastUpdated
    )
}

private fun PriceComparisonResponse.toDomainModel(): PriceComparison {
    return PriceComparison(
        bestDeal = bestDeal?.toDomainModel() ?: PriceDeal("", 0.0),
        worstDeal = worstDeal?.toDomainModel() ?: PriceDeal("", 0.0),
        savings = savings ?: 0.0,
        savingsPercent = savingsPercent ?: 0.0,
        identicalProduct = identicalProduct ?: false,
        priceRange = priceRange?.toDomainModel()
    )
}

private fun PriceDealResponse.toDomainModel(): PriceDeal {
    return PriceDeal(
        store = store,
        price = price,
        city = city
    )
}

private fun PriceRangeResponse.toDomainModel(): PriceRange {
    return PriceRange(
        min = min ?: 0.0,
        max = max ?: 0.0,
        avg = avg ?: 0.0
    )
}

private fun CheapestCartResponse.toDomainModel(): CheapestCart {
    return CheapestCart(
        store = store,
        city = city,
        total = total,
        items = items.map { it.toDomainModel() }
    )
}

private fun CheapestCartItemResponse.toDomainModel(): CheapestCartItem {
    return CheapestCartItem(
        itemCode = itemCode,
        itemName = itemName,
        requestedQuantity = requestedQuantity,
        availableQuantity = availableQuantity,
        price = price,
        totalPrice = totalPrice
    )
}

private fun CartResponse.toDomainModel(): Cart {
    return Cart(
        name = name,
        items = items.map { it.toDomainModel() },
        total = total ?: 0.0,
        store = store,
        city = city
    )
}

private fun CartItemResponse.toDomainModel(): CartItem {
    return CartItem(
        productName = productName,
        quantity = quantity,
        price = price ?: 0.0
    )
}

private fun Cart.toRequest(): com.example.championcart.data.models.request.CartRequest {
    return com.example.championcart.data.models.request.CartRequest(
        name = name,
        items = items.map {
            com.example.championcart.data.models.request.CartItem(
                productName = it.productName,
                quantity = it.quantity
            )
        }
    )
}