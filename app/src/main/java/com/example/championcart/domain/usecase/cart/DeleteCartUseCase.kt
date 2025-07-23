package com.example.championcart.domain.usecase.cart

import com.example.championcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartId: String): Flow<Result<Unit>> {
        return cartRepository.deleteCart(cartId)
    }
}