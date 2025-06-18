package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Register new user with email and password
     * Returns AuthResult with user and token on success
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String? = null
    ): AuthResult {
        // Validate input first
        val validation = validateRegistration(email, password, confirmPassword)
        if (validation is RegisterValidation.Invalid) {
            return AuthResult.Error(validation.message)
        }

        return try {
            // Attempt registration with repository
            authRepository.register(email, password).fold(
                onSuccess = { user ->
                    // Create AuthResponse for the result
                    val authResponse = AuthResponse(
                        token = user.token,
                        user = user,
                        isGuest = false
                    )
                    // Fixed: Use correct constructor parameters
                    AuthResult.Success(user = user, token = authResponse)
                },
                onFailure = { exception ->
                    AuthResult.Error(mapRegistrationError(exception))
                }
            )
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.message}")
        }
    }

    /**
     * Register with additional user information (stored locally only)
     * Note: Server only handles email/password, other info is for local use
     */
    suspend fun registerWithProfile(
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null
    ): AuthResult {
        // Server only supports basic email/password registration
        // Additional profile data would need to be handled locally
        return invoke(email, password)
    }

    /**
     * Validate registration input
     */
    private fun validateRegistration(
        email: String,
        password: String,
        confirmPassword: String?
    ): RegisterValidation {
        return when {
            email.isBlank() -> RegisterValidation.Invalid("Email cannot be empty")
            password.isBlank() -> RegisterValidation.Invalid("Password cannot be empty")
            !email.isValidEmail() -> RegisterValidation.Invalid("Invalid email format")
            password.length < 6 -> RegisterValidation.Invalid("Password must be at least 6 characters")
            confirmPassword != null && password != confirmPassword ->
                RegisterValidation.Invalid("Passwords do not match")
            else -> RegisterValidation.Valid
        }
    }

    /**
     * Create new user with basic info (matching simplified User model)
     */
    private fun createNewUser(email: String, token: String): User {
        return User(
            id = generateUserId(email),
            email = email,
            token = token,
            tokenType = "Bearer",
            name = email.substringBefore("@")
        )
    }

    /**
     * Generate consistent user ID from email
     */
    private fun generateUserId(email: String): String {
        return "user_${email.hashCode()}"
    }

    /**
     * Map registration errors to user-friendly messages
     */
    private fun mapRegistrationError(exception: Throwable): String {
        return when {
            exception.message?.contains("already registered", ignoreCase = true) == true ->
                "An account with this email already exists"
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection"
            exception.message?.contains("invalid email", ignoreCase = true) == true ->
                "Please enter a valid email address"
            else -> exception.message ?: "Registration failed. Please try again"
        }
    }
}