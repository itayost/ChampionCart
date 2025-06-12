package com.example.championcart.domain.repository

import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.CartProduct
import com.example.championcart.domain.models.CheapestCartResult

interface PriceRepository {
    suspend fun searchProducts(city: String, productName: String): Result<List<Product>>
    suspend fun getIdenticalProducts(city: String, productName: String): Result<List<Product>>
    suspend fun findCheapestCart(city: String, items: List<CartProduct>): Result<CheapestCartResult>
    suspend fun getCitiesList(): Result<List<String>>
}