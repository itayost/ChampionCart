package com.example.championcart.data.models.cart

import com.google.gson.annotations.SerializedName

// ===== REQUEST MODELS =====

// Save Cart Request - matches /api/saved-carts/save
data class SaveCartRequest(
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<SaveCartItemRequest>
)

data class SaveCartItemRequest(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("name")
    val name: String
)

// Calculate Cheapest Cart Request - matches /api/cheapest-cart
data class CheapestCartRequest(
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("category")
    val category: String? = null
)

// ===== RESPONSE MODELS =====

// Save Cart Response
data class SaveCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("message")
    val message: String?
)

// List Saved Carts Response - matches /api/saved-carts/list
// Note: Server returns array directly, not wrapped in an object
typealias SavedCartsListResponse = List<SavedCartSummary>

data class SavedCartSummary(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

// Get Cart Details Response - matches /api/saved-carts/{cart_id}
data class CartDetailsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("cart")
    val cart: CartDetails
)

data class CartDetails(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<CartItem>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class CartItem(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("name")
    val name: String
)

// Compare Saved Cart Response - matches /api/saved-carts/{cart_id}/compare
data class CompareCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("cart_info")
    val cartInfo: CartInfo,
    @SerializedName("items")
    val items: List<CompareItem>,
    @SerializedName("comparison")
    val comparison: ComparisonResult
)

data class CartInfo(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("cart_name")
    val cartName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class CompareItem(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("prices")
    val prices: Map<String, Double>? // Store prices by chain
)

data class ComparisonResult(
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("cheapest_store")
    val cheapestStore: CheapestStore,
    @SerializedName("comparison_time")
    val comparisonTime: String
)

data class CheapestStore(
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("chain_name")
    val chainName: String,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("available_items")
    val availableItems: Int,
    @SerializedName("missing_items")
    val missingItems: Int
)

// Cheapest Cart Response - matches /api/cheapest-cart
data class CheapestCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: CheapestCartData?,
    @SerializedName("message")
    val message: String?
)

data class CheapestCartData(
    @SerializedName("cheapest_store")
    val cheapestStore: String,
    @SerializedName("total_price")
    val totalPrice: Double,
    @SerializedName("store_totals")
    val storeTotals: Map<String, Double>,
    @SerializedName("missing_items")
    val missingItems: List<String>?
)

// Delete Cart Response
data class DeleteCartResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)

// Cart Search Response
data class CartSearchResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("query")
    val query: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("products")
    val products: List<CartSearchProduct>
)

data class CartSearchProduct(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("availability")
    val availability: Int
)

// Sample Cart Response
data class SampleCartResponse(
    @SerializedName("city")
    val city: String,
    @SerializedName("items")
    val items: List<SampleCartItem>
)

data class SampleCartItem(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("name")
    val name: String
)