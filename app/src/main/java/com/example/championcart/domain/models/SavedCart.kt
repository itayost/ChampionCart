package com.example.championcart.domain.models

/**
 * Domain model for a saved cart
 * This represents the summary information about a saved cart
 * The actual items are loaded separately when needed
 */
data class SavedCart(
    val id: String,
    val name: String,
    val itemCount: Int,        // Number of different items in the cart
    val totalItems: Int,        // Total quantity of all items
    val createdAt: String       // ISO date string
)