package com.example.championcart.domain.models

/**
 * Saved cart from server
 * Matches: GET /savedcarts/{email} response
 */
data class SavedCart(
    val cartName: String,
    val city: String,
    val items: List<SavedCartItem>
)

/**
 * Saved cart item with price
 */
data class SavedCartItem(
    val itemName: String,
    val quantity: Int,
    val price: Double
)