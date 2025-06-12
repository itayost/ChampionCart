package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

// This is what the API actually returns - a product with multiple store prices
data class PriceItem(
    @SerializedName("item_code")
    val itemCode: String?,
    @SerializedName("item_name")
    val itemName: String?,
    @SerializedName("prices")
    val prices: List<StorePrice>?,  // This is the key difference - array of prices
    @SerializedName("cross_chain")
    val crossChain: Boolean?,
    @SerializedName("relevance_score")
    val relevanceScore: Double?,
    @SerializedName("price_comparison")
    val priceComparison: PriceComparison?,
    @SerializedName("price_per_unit")
    val pricePerUnit: Double?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("weight")
    val weight: Double?
)

// Individual store price within a product
data class StorePrice(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("store_id")
    val storeId: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("timestamp")
    val timestamp: String?
)

// Price comparison info
data class PriceComparison(
    @SerializedName("best_deal")
    val bestDeal: BestDeal?,
    @SerializedName("worst_deal")
    val worstDeal: WorstDeal?,
    @SerializedName("savings")
    val savings: Double?,
    @SerializedName("savings_percent")
    val savingsPercent: Double?,
    @SerializedName("identical_product")
    val identicalProduct: Boolean?
)

data class BestDeal(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("store_id")
    val storeId: String?
)

data class WorstDeal(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("store_id")
    val storeId: String?
)

// Keep these as they are for cart operations
data class StoreInfo(
    @SerializedName("chain_name")
    val chainName: String?,
    @SerializedName("store_name")
    val storeName: String?,
    @SerializedName("address")
    val address: String?
)

data class CheapestCartResponse(
    @SerializedName("best_store")
    val bestStore: StoreInfo,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("savings_amount")
    val savingsAmount: Double,
    @SerializedName("savings_percentage")
    val savingsPercentage: Double,
    @SerializedName("items_breakdown")
    val itemsBreakdown: List<ItemPriceBreakdown>
)

data class ItemPriceBreakdown(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("total_price")
    val totalPrice: Double
)