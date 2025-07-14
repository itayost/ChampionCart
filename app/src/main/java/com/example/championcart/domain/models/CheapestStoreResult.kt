package com.example.championcart.domain.models

data class CheapestStoreResult(
    val cheapestStore: String,
    val totalPrice: Double,
    val storeTotals: Map<String, Double>,
    val missingItems: List<String>,
    val address: String? = null,
    val availableItems: Int? = null,
    val totalMissingItems: Int? = null,
    // NEW: Add detailed store information
    val storeDetails: List<StoreDetail>? = null
)

data class StoreDetail(
    val storeName: String,
    val branchName: String,
    val chainName: String,
    val totalPrice: Double,
    val availableItems: Int,
    val missingItems: Int,
    val address: String? = null
)