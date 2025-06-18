package com.example.championcart.domain.repository

import com.example.championcart.domain.models.*

interface PriceRepository {
    suspend fun searchProducts(
        query: String,
        city: String?
    ): Result<List<GroupedProduct>>

    suspend fun searchProductsV2(
        query: String,
        city: String?,
        store: String?,
        exactMatch: Boolean
    ): Result<List<Product>>

    suspend fun getCheapestCart(
        items: List<com.example.championcart.data.models.request.CartItem>,
        city: String
    ): Result<CheapestCart>

    suspend fun getSavedCarts(): Result<List<Cart>>

    suspend fun saveCart(cart: Cart): Result<Unit>

    suspend fun deleteCart(cartName: String): Result<Unit>

    suspend fun getCheapestCartForProducts(
        products: List<CartProduct>,
        city: String
    ): Result<CheapestCart>

    suspend fun getCities(): Result<List<String>>
}