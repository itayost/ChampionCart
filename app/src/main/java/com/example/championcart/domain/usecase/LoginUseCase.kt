package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.AuthResponse
import com.example.championcart.domain.models.AuthResult
import com.example.championcart.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): AuthResult {
        return try {
            val result = authRepository.login(email, password)

            result.fold(
                onSuccess = { user ->
                    val authResponse = AuthResponse(
                        token = user.token,
                        user = user,
                        isGuest = false
                    )
                    // Fixed: AuthResult.Success expects (user, authResponse) not (authResponse, token)
                    AuthResult.Success(user = user, token = authResponse)
                },
                onFailure = { exception ->
                    AuthResult.Error(exception.message ?: "Login failed")
                }
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun loginAsGuest(): AuthResult {
        return try {
            val guestUser = com.example.championcart.domain.models.User(
                id = "guest",
                email = "guest@championcart.com",
                token = "",
                tokenType = "Bearer",
                name = "Guest User"
            )
            val authResponse = AuthResponse(
                token = "",
                user = guestUser,
                isGuest = true
            )
            // Fixed: AuthResult.Success expects (user, authResponse)
            AuthResult.Success(user = guestUser, token = authResponse)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to login as guest")
        }
    }
}