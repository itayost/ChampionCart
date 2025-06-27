package com.example.championcart.domain.usecase.product

import com.example.championcart.domain.models.Product
import com.example.championcart.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(query: String, city: String? = null): Flow<Result<List<Product>>> {
        return priceRepository.searchProducts(query, city)
    }
}