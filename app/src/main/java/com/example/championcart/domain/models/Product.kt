package com.example.championcart.domain.models

data class Product(
    val id: String,
    val barcode: String? = null,  // Barcode from product search API
    val name: String,
    val category: String,
    val bestPrice: Double,
    val bestStore: String,
    val stores: List<StorePrice>,
    val imageUrl: String? = null
)