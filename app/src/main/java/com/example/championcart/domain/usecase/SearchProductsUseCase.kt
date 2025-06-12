package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.PriceRepository

class SearchProductsUseCase(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(city: String, query: String): Result<List<Product>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return priceRepository.searchProducts(city, query.trim())
    }
}