package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

// Each product with its prices across different stores (for grouped responses)
data class PriceItem(
    @SerializedName("item_code")
    val itemCode: String?,
    @SerializedName("item_name")
    val itemName: String?,
    @SerializedName("prices")
    val prices: List<StorePrice>?,  // Array of prices from different stores
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

// Individual store price within a product (for grouped responses)
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

// Price comparison info (for grouped responses)
data class PriceComparison(
    @SerializedName("best_deal")
    val bestDeal: PriceDeal?,
    @SerializedName("worst_deal")
    val worstDeal: PriceDeal?,
    @SerializedName("savings")
    val savings: Double?,
    @SerializedName("savings_percent")
    val savingsPercent: Double?,
    @SerializedName("identical_product")
    val identicalProduct: Boolean?
)

data class PriceDeal(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("store_id")
    val storeId: String?
)

// For cheapest cart API - matches actual API response
data class CheapestCartResponse(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("store_id")
    val storeId: String?,
    @SerializedName("total_price")
    val totalPrice: Double = 0.0,
    @SerializedName("worst_price")
    val worstPrice: Double = 0.0,
    @SerializedName("savings")
    val savings: Double = 0.0,
    @SerializedName("savings_percent")
    val savingsPercent: Double = 0.0,
    @SerializedName("city")
    val city: String?,
    @SerializedName("items")
    val items: List<com.example.championcart.data.models.request.CartItem>? = null,
    @SerializedName("item_prices")
    val itemPrices: Map<String, Double>? = null,
    @SerializedName("all_stores")
    val allStores: List<StoreOption>? = null
)

data class StoreOption(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("store_id")
    val storeId: String?,
    @SerializedName("total_price")
    val totalPrice: Double
)

// Legacy models kept for compatibility
data class StoreInfo(
    @SerializedName("chain_name")
    val chainName: String? = null,
    @SerializedName("store_name")
    val storeName: String? = null,
    @SerializedName("address")
    val address: String? = null,
    // Alternative field names the server might use
    @SerializedName("chain")
    val chain: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("store_address")
    val storeAddress: String? = null
) {
    // Helper methods to get the actual values
    fun getActualChainName(): String = chainName ?: chain ?: "Unknown"
    fun getActualStoreName(): String = storeName ?: name ?: "Unknown"
    fun getActualAddress(): String = address ?: storeAddress ?: "No address"
}

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