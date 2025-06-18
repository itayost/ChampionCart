package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.repository.PriceRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(
        query: String,
        selectedCity: String? = null
    ): Result<List<GroupedProduct>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            // Call the repository with the correct parameters
            val searchResult = priceRepository.searchProducts(
                query = query.trim(),
                city = selectedCity
            )

            searchResult.map { products ->
                // Sort products by relevance or price
                products.sortedBy { product ->
                    product.priceComparison?.bestDeal?.price ?: product.prices.minByOrNull { it.price }?.price ?: Double.MAX_VALUE
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Alternative method if you need to search with more parameters
    suspend fun searchAdvanced(
        query: String,
        city: String?,
        store: String?,
        exactMatch: Boolean = false
    ): Result<List<com.example.championcart.domain.models.Product>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return priceRepository.searchProductsV2(
            query = query.trim(),
            city = city,
            store = store,
            exactMatch = exactMatch
        )
    }
}