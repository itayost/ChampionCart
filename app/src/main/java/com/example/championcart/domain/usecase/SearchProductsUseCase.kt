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
     * Search with advanced filters
     */
    suspend fun searchWithFilters(
        searchQuery: SearchQuery,
        filters: SearchFilters
    ): Result<SearchResult> {
        // Validate search query
        if (searchQuery.query.isBlank()) {
            return Result.failure(Exception("Search query cannot be empty"))
        }

        if (searchQuery.city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.searchProductsWithFilters(searchQuery, filters)
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
        return when (val result = invoke(city, partialQuery, limit = limit)) {
            is Result.Success -> {
                val suggestions = result.getOrNull()
                    ?.map { it.itemName }
                    ?.distinct()
                    ?.take(limit)
                    ?: emptyList()
                Result.success(suggestions)
            }
            is Result.Failure -> Result.failure(result.exception)
        }
    }

    /**
     * Search for trending products
     */
    suspend fun getTrendingProducts(
        city: String,
        limit: Int = 20
    ): Result<List<GroupedProduct>> {
        if (city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.getTrendingProducts(city, limit)
    }

    /**
     * Search for products on sale
     */
    suspend fun getProductsOnSale(
        city: String,
        limit: Int = 50
    ): Result<List<GroupedProduct>> {
        if (city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.getProductsOnSale(city, limit)
    }

    /**
     * Get personalized recommendations
     */
    suspend fun getRecommendations(
        city: String,
        limit: Int = 10
    ): Result<List<GroupedProduct>> {
        if (city.isBlank()) {
            return Result.failure(Exception("City cannot be empty"))
        }

        return priceRepository.getRecommendations(city, limit)
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