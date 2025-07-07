package com.example.championcart.data.api

import com.example.championcart.data.models.cart.*
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface CartApi {

    // ===== SAVED CARTS ENDPOINTS =====

    @POST("api/saved-carts/save")
    suspend fun saveCart(
        @Body request: SaveCartRequest
    ): SaveCartResponse

    @GET("api/saved-carts/list")
    suspend fun getSavedCarts(): SavedCartsListResponse  // This is now a typealias for List<SavedCartSummary>

    @GET("api/saved-carts/{cart_id}")
    suspend fun getCartDetails(
        @Path("cart_id") cartId: Int
    ): CartDetailsResponse

    @GET("api/saved-carts/{cart_id}/compare")
    suspend fun compareCart(
        @Path("cart_id") cartId: Int
    ): CompareCartResponse

    @DELETE("api/saved-carts/{cart_id}")
    suspend fun deleteCart(
        @Path("cart_id") cartId: Int
    ): DeleteCartResponse

    // ===== PRICE COMPARISON ENDPOINTS =====

    @POST("api/cheapest-cart")
    suspend fun calculateCheapestCart(
        @Body request: CheapestCartRequest
    ): CheapestCartResponse

    // ===== CART SEARCH ENDPOINTS =====

    @GET("api/cart/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): CartSearchResponse

    @GET("api/cart/sample")
    suspend fun getSampleCart(): SampleCartResponse
}

// Additional response models for cart search
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