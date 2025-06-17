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

/**
 * Save cart request
 * Matches: POST /save-cart request format
 */
data class SaveCartRequest(
    val cartName: String,
    val email: String,
    val city: String,
    val items: List<CartProduct>
)