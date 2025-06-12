package com.example.championcart.domain.models

data class Product(
    val itemCode: String,
    val itemName: String,
    val price: Double,
    val chainName: String,
    val storeName: String,
    val storeAddress: String,
    val lastUpdated: String
)

data class CartProduct(
    val itemName: String,
    val quantity: Int
)

data class CheapestCartResult(
    val bestStore: Store,
    val totalPrice: Double,
    val savingsAmount: Double,
    val savingsPercentage: Double,
    val itemsBreakdown: List<CartItemBreakdown>
)

data class Store(
    val chainName: String,
    val storeName: String,
    val address: String
)

data class CartItemBreakdown(
    val itemName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)