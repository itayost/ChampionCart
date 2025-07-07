package com.example.championcart.data.mappers

import com.example.championcart.data.models.price.GroupedProductResponse
import com.example.championcart.data.models.price.StorePriceResponse
import com.example.championcart.data.models.product.ProductSearchResponse
import com.example.championcart.domain.models.PriceLevel
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.StorePrice

/**
 * Maps GroupedProductResponse from price API to Product domain model
 * This is used by the /api/prices/by-item endpoint which doesn't return barcodes
 */
fun GroupedProductResponse.toDomainModel(): Product {
    val storePrices = prices.map { it.toDomainModel() }
    val sortedPrices = storePrices.sortedBy { it.price }

    // Determine price levels
    val updatedPrices = when {
        sortedPrices.size == 1 -> {
            listOf(sortedPrices[0].copy(priceLevel = PriceLevel.BEST))
        }
        sortedPrices.size == 2 -> {
            listOf(
                sortedPrices[0].copy(priceLevel = PriceLevel.BEST),
                sortedPrices[1].copy(priceLevel = PriceLevel.HIGH)
            )
        }
        else -> {
            sortedPrices.mapIndexed { index, storePrice ->
                when {
                    index == 0 -> storePrice.copy(priceLevel = PriceLevel.BEST)
                    index == sortedPrices.lastIndex -> storePrice.copy(priceLevel = PriceLevel.HIGH)
                    else -> storePrice.copy(priceLevel = PriceLevel.MID)
                }
            }
        }
    }

    return Product(
        id = itemName.replace(" ", "-").lowercase(),
        barcode = null, // Price API doesn't provide barcode
        name = itemName,
        category = category ?: "כללי",
        bestPrice = sortedPrices.firstOrNull()?.price ?: 0.0,
        bestStore = sortedPrices.firstOrNull()?.storeName ?: "",
        stores = updatedPrices,
        imageUrl = null
    )
}

/**
 * Maps StorePriceResponse to StorePrice domain model
 */
fun StorePriceResponse.toDomainModel(): StorePrice {
    return StorePrice(
        storeName = storeName,
        price = price,
        priceLevel = PriceLevel.MID // Will be updated by parent mapper
    )
}

/**
 * Maps ProductSearchResponse from product search API to Product domain model
 * This is used by the /api/products/search endpoint which includes barcodes
 */
fun ProductSearchResponse.toDomainModel(): Product {
    val storePrices = pricesByStore.map { storePriceInfo ->
        StorePrice(
            storeName = storePriceInfo.chainDisplayName,
            price = storePriceInfo.price,
            priceLevel = when {
                storePriceInfo.isCheapest -> PriceLevel.BEST
                storePriceInfo.price == priceStats.maxPrice -> PriceLevel.HIGH
                else -> PriceLevel.MID
            }
        )
    }.sortedBy { it.price }

    // Find the best store
    val bestStore = pricesByStore.find { it.isCheapest }
        ?: pricesByStore.minByOrNull { it.price }

    return Product(
        id = barcode, // Using barcode as ID for products from search API
        barcode = barcode,
        name = name,
        category = extractCategory(name),
        bestPrice = priceStats.minPrice,
        bestStore = bestStore?.chainDisplayName ?: "",
        stores = storePrices,
        imageUrl = null
    )
}

/**
 * Helper function to extract category from product name
 * This is a temporary solution until the API provides categories
 */
private fun extractCategory(productName: String): String {
    return when {
        productName.contains("חלב") -> "מוצרי חלב"
        productName.contains("לחם") -> "מאפים"
        productName.contains("ביצים") -> "ביצים"
        productName.contains("גבינה") -> "מוצרי חלב"
        productName.contains("יוגורט") -> "מוצרי חלב"
        productName.contains("עוף") -> "בשר ועוף"
        productName.contains("בשר") -> "בשר ועוף"
        productName.contains("דג") -> "דגים"
        productName.contains("ירק") || productName.contains("עגבני") || productName.contains("מלפפון") -> "ירקות"
        productName.contains("פרי") || productName.contains("תפוח") || productName.contains("בננה") -> "פירות"
        productName.contains("שוקולד") || productName.contains("ממתק") -> "חטיפים וממתקים"
        productName.contains("קפה") || productName.contains("תה") -> "משקאות חמים"
        productName.contains("מיץ") || productName.contains("משקה") -> "משקאות"
        productName.contains("אורז") || productName.contains("פסטה") -> "מזון יבש"
        productName.contains("שמן") || productName.contains("תבלין") -> "בישול ואפייה"
        else -> "כללי"
    }
}