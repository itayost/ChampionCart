package com.example.championcart.data.mappers

import com.example.championcart.data.models.price.GroupedProductResponse
import com.example.championcart.data.models.price.StorePriceResponse
import com.example.championcart.domain.models.PriceLevel
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.StorePrice

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
        name = itemName,
        category = category ?: "כללי",
        bestPrice = sortedPrices.firstOrNull()?.price ?: 0.0,
        bestStore = sortedPrices.firstOrNull()?.storeName ?: "",
        stores = updatedPrices,
        imageUrl = null // Server doesn't provide images yet
    )
}

fun StorePriceResponse.toDomainModel(): StorePrice {
    return StorePrice(
        storeName = storeName,
        price = price,
        priceLevel = PriceLevel.MID // Will be updated by parent mapper
    )
}