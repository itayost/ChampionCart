package com.example.championcart.data.models.response

import com.google.gson.annotations.SerializedName

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