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
                onSuccess = { authResponse ->
                    // Save token to local storage (done by repository)
                    // Create new user with simple data (server only provides email/password auth)
                    val newUser = createNewUser(email)

                    AuthResult.Success(newUser, authResponse)
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
    private fun createNewUser(email: String): User {
        return User(
            id = generateUserId(email),
            email = email,
            isGuest = false
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

    // REMOVED: Complex user profile creation with preferences
    // Server doesn't support user preferences/profile storage
    // These features would need to be implemented locally
}