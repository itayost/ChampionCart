package com.example.championcart.domain.models

data class Store(
    val id: String,           // For UI purposes
    val chain: String,        // "shufersal", "victory"
    val storeId: String,      // "001", "052", etc.
    val name: String,         // Display name for UI
    val address: String       // Store address
) {
    companion object {
        /**
         * Create store from API chain/storeId response
         */
        fun fromChainAndStoreId(
            chain: String,
            storeId: String,
            name: String = getDefaultStoreName(chain, storeId),
            address: String = "Store Address" // Would come from store lookup
        ): Store {
            return Store(
                id = "${chain}-${storeId}",
                chain = chain,
                storeId = storeId,
                name = name,
                address = address
            )
        }

        private fun getDefaultStoreName(chain: String, storeId: String): String {
            return when (chain.lowercase()) {
                "shufersal" -> "Shufersal ${storeId}"
                "victory" -> "Victory ${storeId}"
                else -> "${chain.capitalize()} ${storeId}"
            }
        }
    }
}