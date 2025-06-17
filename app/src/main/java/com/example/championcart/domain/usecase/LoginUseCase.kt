package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Login user with email and password
     * Returns AuthResult with user and token on success
     */
    suspend operator fun invoke(email: String, password: String): AuthResult {
        // Validate input first
        val validation = validateLogin(email, password)
        if (validation is LoginValidation.Invalid) {
            return AuthResult.Error(validation.message)
        }

        return try {
            // Attempt login with repository
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    val authResponse = result.getOrNull()!!

                    // Save token to local storage
                    authRepository.saveAuthToken(authResponse)

                    // Get user profile
                    when (val userResult = authRepository.getUserProfile()) {
                        is Result.Success -> {
                            val user = userResult.getOrNull()!!
                            AuthResult.Success(user, authResponse)
                        }
                        is Result.Failure -> {
                            // Create basic user from email if profile fetch fails
                            val basicUser = User(
                                id = generateUserId(email),
                                email = email,
                                isGuest = false
                            )
                            AuthResult.Success(basicUser, authResponse)
                        }
                    }
                }
                is Result.Failure -> {
                    AuthResult.Error(mapAuthError(result.exception))
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Login failed: ${e.message}")
        }
    }

    /**
     * Login with stored credentials (auto-login)
     */
    suspend fun loginWithStoredToken(): AuthResult {
        return try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrBlank()) {
                return AuthResult.Error("No stored credentials found")
            }

            // Verify token is still valid by getting user profile
            when (val userResult = authRepository.getUserProfile()) {
                is Result.Success -> {
                    val user = userResult.getOrNull()!!
                    val authResponse = AuthResponse(accessToken = token)
                    AuthResult.Success(user, authResponse)
                }
                is Result.Failure -> {
                    // Token might be expired, clear it
                    authRepository.clearAuthToken()
                    AuthResult.Error("Session expired, please login again")
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Auto-login failed: ${e.message}")
        }
    }

    /**
     * Continue as guest user
     */
    suspend fun continueAsGuest(): AuthResult {
        return try {
            val guestUser = User(
                id = "guest-${System.currentTimeMillis()}",
                email = "guest@championcart.com",
                isGuest = true,
                preferences = UserPreferences()
            )

            val guestToken = AuthResponse(accessToken = "guest-token")
            AuthResult.Success(guestUser, guestToken)
        } catch (e: Exception) {
            AuthResult.Error("Failed to create guest session: ${e.message}")
        }
    }

    /**
     * Validate login input
     */
    private fun validateLogin(email: String, password: String): LoginValidation {
        return when {
            email.isBlank() -> LoginValidation.Invalid("Email cannot be empty")
            password.isBlank() -> LoginValidation.Invalid("Password cannot be empty")
            !email.isValidEmail() -> LoginValidation.Invalid("Please enter a valid email address")
            password.length < 3 -> LoginValidation.Invalid("Password is too short")
            else -> LoginValidation.Valid
        }
    }

    /**
     * Map server errors to user-friendly messages
     */
    private fun mapAuthError(exception: Throwable): String {
        return when {
            exception.message?.contains("401") == true -> "Invalid email or password"
            exception.message?.contains("404") == true -> "User not found"
            exception.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection"
            exception.message?.contains("timeout", ignoreCase = true) == true -> "Request timed out. Please try again"
            else -> "Login failed. Please try again"
        }
    }

    /**
     * Generate user ID from email for fallback cases
     */
    private fun generateUserId(email: String): String {
        return "user-${email.hashCode().toString().replace("-", "")}"
    }
}