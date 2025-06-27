package com.example.championcart.domain.models

data class StorePrice(
    val storeName: String,
    val price: Double,
    val priceLevel: PriceLevel
)

enum class PriceLevel {
    BEST,
    MID,
    HIGH
}