package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.PriceRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val priceRepository: PriceRepository
) {
    /**
     * Search for products with basic query
     */
    suspend operator fun invoke(
        city: String,
        query: String,
        groupByCode: Boolean = true,
        limit: Int? = 50
    ): Result<List<GroupedProduct>> {
        // Validate input
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        if (city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.searchProducts(
            city = city,
            productName = query.trim(),
            groupByCode = groupByCode,
            limit = limit
        )
    }

    /**
     * Get identical products across chains only
     */
    suspend fun getIdenticalProducts(
        city: String,
        productName: String,
        limit: Int? = 50
    ): Result<List<GroupedProduct>> {
        if (productName.isBlank()) {
            return Result.success(emptyList())
        }

        if (city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.getIdenticalProducts(
            city = city,
            productName = productName.trim(),
            limit = limit
        )
    }

    /**
     * Get search suggestions based on partial query
     */
    suspend fun getSearchSuggestions(
        city: String,
        partialQuery: String,
        limit: Int = 10
    ): Result<List<String>> {
        if (partialQuery.length < 2) {
            return Result.success(emptyList())
        }

        // Search for products and extract unique names as suggestions
        return invoke(city, partialQuery, limit = limit).fold(
            onSuccess = { products ->
                val suggestions = products
                    .map { it.itemName }
                    .distinct()
                    .take(limit)
                Result.success(suggestions)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}

/**
 * Search validation helper
 */
object SearchValidation {
    fun validateSearchQuery(query: String): SearchValidationResult {
        return when {
            query.isBlank() -> SearchValidationResult.Invalid("Search query cannot be empty")
            query.length < 2 -> SearchValidationResult.Invalid("Search query must be at least 2 characters")
            query.length > 100 -> SearchValidationResult.Invalid("Search query is too long")
            else -> SearchValidationResult.Valid
        }
    }

    fun validateCity(city: String): SearchValidationResult {
        return when {
            city.isBlank() -> SearchValidationResult.Invalid("City cannot be empty")
            city.length < 2 -> SearchValidationResult.Invalid("Invalid city name")
            else -> SearchValidationResult.Valid
        }
    }
}

/**
 * Search validation result
 */
sealed class SearchValidationResult {
    object Valid : SearchValidationResult()
    data class Invalid(val message: String) : SearchValidationResult()
}