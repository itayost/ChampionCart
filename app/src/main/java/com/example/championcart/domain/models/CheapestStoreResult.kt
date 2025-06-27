package com.example.championcart.domain.models

data class CheapestStoreResult(
    val cheapestStore: String,
    val totalPrice: Double,
    val storeTotals: Map<String, Double>,
    val missingItems: List<String>
)