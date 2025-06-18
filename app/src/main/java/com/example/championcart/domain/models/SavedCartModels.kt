// Add this to your domain/models package if SavedCart doesn't exist:

package com.example.championcart.domain.models

/**
 * Saved cart summary for display in lists
 */
data class SavedCart(
    val id: String,
    val name: String,
    val itemCount: Int,
    val totalPrice: Double,
    val lastUpdated: Long,
    val city: String
)

/**
 * Item in a saved cart
 */
data class SavedCartItem(
    val productName: String,
    val quantity: Int,
    val price: Double,
    val store: String? = null
)