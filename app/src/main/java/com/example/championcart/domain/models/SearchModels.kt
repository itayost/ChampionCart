package com.example.championcart.domain.models

data class GroupedProduct(
    val itemCode: String,
    val itemName: String,
    val storePrices: List<ProductStorePrice>,
    val lowestPrice: Double?,
    val highestPrice: Double?,
    val savings: Double
)

data class ProductStorePrice(
    val chain: String,
    val storeId: String,
    val price: Double
)