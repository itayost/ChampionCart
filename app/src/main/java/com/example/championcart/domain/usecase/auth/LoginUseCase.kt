package com.example.championcart.domain.usecase.auth

import com.example.championcart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<Result<Boolean>> {
        return authRepository.login(email, password)
    }
}