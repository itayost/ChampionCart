package com.example.championcart.domain.usecase.cart

import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.domain.models.SavedCart
import com.example.championcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for saving the current cart
 */
class SaveCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(name: String): Flow<Result<String>> {
        return cartRepository.saveCart(name)
    }
}
/**
 * Use case for getting saved carts
 */
class GetSavedCartsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<SavedCart>>> {
        return cartRepository.getSavedCarts()
    }
}

/**
 * Use case for loading a saved cart into the current cart
 */
class LoadSavedCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartId: String): Flow<Result<Unit>> {
        return cartRepository.loadSavedCart(cartId)
    }
}