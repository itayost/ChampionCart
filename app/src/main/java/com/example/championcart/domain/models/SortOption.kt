package com.example.championcart.domain.models

/**
 * Sort options for search results - app level enum
 */
enum class SortOption(val displayName: String) {
    RELEVANCE("Relevance"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    NAME_A_TO_Z("Name: A to Z"),
    NAME_Z_TO_A("Name: Z to A"),
    SAVINGS_HIGH_TO_LOW("Highest Savings")
}