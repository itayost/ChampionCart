package com.example.championcart.domain.usecase.cart

import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalculateCheapestStoreUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(city: String? = null): Flow<Result<CheapestStoreResult>> {
        return cartRepository.calculateCheapestStore(city)
    }
}