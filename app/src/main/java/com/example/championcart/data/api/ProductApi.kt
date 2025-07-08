package com.example.championcart.data.api

import com.example.championcart.data.models.product.ProductBarcodeResponse
import com.example.championcart.data.models.product.ProductSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("city") city: String,
        @Query("limit") limit: Int = 20
    ): List<ProductSearchResponse>

    @GET("api/products/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("city") city: String
    ): ProductBarcodeResponse
}