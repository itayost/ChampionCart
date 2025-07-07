package com.example.championcart.domain.repository

import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.models.SavedCart
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun saveCart(name: String): Flow<Result<String>>
    suspend fun getSavedCarts(): Flow<Result<List<SavedCart>>>
    suspend fun loadSavedCart(cartId: String): Flow<Result<Unit>>
    suspend fun calculateCheapestStore(city: String? = null): Flow<Result<CheapestStoreResult>>
}