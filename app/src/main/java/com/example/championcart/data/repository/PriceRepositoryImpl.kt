package com.example.championcart.data.repository

import android.util.Log
import com.example.championcart.data.api.ChampionCartApi
import com.example.championcart.data.models.request.CartItem as RequestCartItem
import com.example.championcart.data.models.request.CheapestCartRequest
import com.example.championcart.data.models.response.*
import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.CartRepository
import com.example.championcart.domain.repository.PriceRepository
import javax.inject.Inject
import javax.inject.Singleton
import com.example.championcart.domain.models.StoreInfo

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val api: ChampionCartApi
) : PriceRepository, CartRepository {

    companion object {
        private const val TAG = "PriceRepository"
    }

    override suspend fun searchProducts(
        query: String,
        city: String?
    ): Result<List<GroupedProduct>> {
        return try {
            Log.d(TAG, "Searching products: query=$query, city=$city")
            val response = api.searchProducts(query, city, 20)
            Log.d(TAG, "Search response received: ${response.size} products")

            val products = response.map { it.toDomainModel() }
            Log.d(TAG, "Successfully mapped ${products.size} products")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products", e)
            Result.failure(e)
        }
    }

    override suspend fun searchProductsV2(
        query: String,
        city: String?,
        store: String?,
        exactMatch: Boolean
    ): Result<List<Product>> {
        return try {
            Log.d(TAG, "Searching products V2: query=$query, city=$city, store=$store")
            val response = api.searchProductsV2(query, city, store, exactMatch)
            Log.d(TAG, "Search V2 response received: ${response.size} products")

            // Convert grouped products to single products
            val products = mutableListOf<Product>()
            for (grouped in response) {
                val prices = grouped.prices
                if (prices != null) {
                    for (price in prices) {
                        products.add(
                            Product(
                                itemCode = grouped.itemCode,
                                itemName = grouped.itemName,
                                chain = price.chain,
                                storeId = price.storeId,
                                price = price.price,
                                city = price.city,
                                lowestPrice = grouped.priceComparison?.bestDeal?.price
                            )
                        )
                    }
                }
            }
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products V2", e)
            Result.failure(e)
        }
    }

    override suspend fun getCheapestCart(
        items: List<RequestCartItem>,
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
            if (response.isSuccessful) {
                val carts = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Log.d(TAG, "Received ${carts.size} saved carts")
                Result.success(carts)
            } else {
                Log.e(TAG, "Failed to get saved carts: ${response.code()}")
                Result.failure(Exception("Failed to get saved carts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching saved carts", e)
            Result.failure(e)
        }
    }

    override suspend fun saveCart(cart: Cart): Result<Unit> {
        return try {
            Log.d(TAG, "Saving cart: ${cart.name}")
            val request = cart.toRequest()
            val response = api.saveCart(request)
            if (response.isSuccessful) {
                Log.d(TAG, "Cart saved successfully")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to save cart: ${response.code()}")
                Result.failure(Exception("Failed to save cart: ${response.code()}"))
            }
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

    override suspend fun getCheapestCartForProducts(
        products: List<CartProduct>,
        city: String
    ): Result<CheapestCart> {
        return try {
            val items = products.map {
                RequestCartItem(
                    itemName = it.itemName,
                    quantity = it.quantity
                )
            }
            getCheapestCart(items, city)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cheapest cart for products", e)
            Result.failure(e)
        }
    }

    override suspend fun getCities(): Result<List<String>> {
        return try {
            Log.d(TAG, "Fetching cities list")
            val cities = api.getCities()
            Log.d(TAG, "Received ${cities.size} cities")
            Result.success(cities)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cities", e)
            Result.failure(e)
        }
    }

    // CartRepository implementation
    private val cartItems = mutableListOf<CartItem>()

    override suspend fun getCartItems(): Result<List<CartItem>> {
        return Result.success(cartItems.toList())
    }

    override suspend fun addToCart(product: Product): Result<Unit> {
        return try {
            val existingItem = cartItems.find { it.productId == product.itemCode }
            if (existingItem != null) {
                val index = cartItems.indexOf(existingItem)
                cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                cartItems.add(
                    CartItem(
                        id = product.itemCode,
                        productId = product.itemCode,
                        productName = product.itemName,
                        quantity = 1,
                        price = product.price,
                        selectedStore = StoreInfo(
                            chain = product.chain,
                            storeId = product.storeId,
                            city = product.city
                        )
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(productId: String): Result<Unit> {
        return try {
            cartItems.removeAll { it.id == productId || it.productId == productId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int): Result<Unit> {
        return try {
            val index = cartItems.indexOfFirst { it.id == productId || it.productId == productId }
            if (index != -1) {
                cartItems[index] = cartItems[index].copy(quantity = quantity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartItems.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemCount(): Result<Int> {
        return Result.success(cartItems.sumOf { it.quantity })
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
            RequestCartItem(
                itemName = it.productName,
                quantity = it.quantity
            )
        }
    )
}