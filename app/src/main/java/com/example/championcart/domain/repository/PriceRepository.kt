package com.example.championcart.domain.repository

import com.example.championcart.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    suspend fun searchProducts(query: String, city: String? = null): Flow<Result<List<Product>>>
    suspend fun getProductDetails(productId: String, city: String? = null): Flow<Result<Product>>
}