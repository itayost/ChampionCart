package com.example.championcart.domain.usecase

import com.example.championcart.domain.models.*
import com.example.championcart.domain.repository.AuthRepository
import java.time.LocalDateTime
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
            when (val result = authRepository.register(email, password)) {
                is Result.Success -> {
                    val authResponse = result.getOrNull()!!

                    // Save token to local storage
                    authRepository.saveAuthToken(authResponse)

                    // Create new user with default preferences
                    val newUser = createNewUser(email)

                    // Try to save user profile
                    authRepository.updateUserProfile(newUser)

                    AuthResult.Success(newUser, authResponse)
                }
                is Result.Failure -> {
                    AuthResult.Error(mapRegistrationError(result.exception))
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.message}")
        }
    }

    /**
     * Register with additional user information
     */
    suspend fun registerWithProfile(
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        preferences: UserPreferences = UserPreferences()
    ): AuthResult {
        // First register with basic info
        when (val basicResult = invoke(email, password)) {
            is AuthResult.Success -> {
                // Update user with additional profile information
                val updatedUser = basicResult.user.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    preferences = preferences
                )

                // Save updated profile
                return try {
                    authRepository.updateUserProfile(updatedUser)
                    AuthResult.Success(updatedUser, basicResult.token)
                } catch (e: Exception) {
                    // Even if profile update fails, registration succeeded
                    AuthResult.Success(basicResult.user, basicResult.token)
                }
            }
            is AuthResult.Error -> return basicResult
            is AuthResult.Loading -> return basicResult
        }
    }

    /**
     * Check if email is already registered
     */
    suspend fun checkEmailAvailability(email: String): EmailAvailabilityResult {
        if (!email.isValidEmail()) {
            return EmailAvailabilityResult.Invalid("Invalid email format")
        }

        return try {
            // Try to register with a dummy password to check if email exists
            // This is a workaround since the API doesn't have a dedicated email check endpoint
            when (authRepository.register(email, "dummy-check-password")) {
                is Result.Success -> EmailAvailabilityResult.Available
                is Result.Failure -> {
                    val error = authRepository.register(email, "dummy").exceptionOrNull()
                    if (error?.message?.contains("already registered") == true) {
                        EmailAvailabilityResult.Taken
                    } else {
                        EmailAvailabilityResult.Available
                    }
                }
            }
        } catch (e: Exception) {
            EmailAvailabilityResult.Unknown("Unable to check email availability")
        }
    }

    /**
     * Get password strength for UI feedback
     */
    fun getPasswordStrength(password: String): PasswordStrengthResult {
        val strength = password.getPasswordStrength()
        val suggestions = getPasswordSuggestions(password)

        return PasswordStrengthResult(
            strength = strength,
            suggestions = suggestions,
            isValid = strength != PasswordStrength.WEAK && password.length >= 6
        )
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
            !email.isValidEmail() -> RegisterValidation.Invalid("Please enter a valid email address")
            password.length < 6 -> RegisterValidation.Invalid("Password must be at least 6 characters long")
            confirmPassword != null && password != confirmPassword -> RegisterValidation.Invalid("Passwords do not match")
            password.getPasswordStrength() == PasswordStrength.WEAK -> RegisterValidation.Invalid("Password is too weak. Please include uppercase, lowercase, numbers, and special characters")
            else -> RegisterValidation.Valid
        }
    }

    /**
     * Create new user with default settings
     */
    private fun createNewUser(email: String): User {
        return User(
            id = generateUserId(email),
            email = email,
            firstName = null,
            lastName = null,
            phone = null,
            preferences = UserPreferences(
                defaultCity = "Tel Aviv",
                language = Language.HEBREW,
                currency = Currency.ILS,
                theme = ThemePreference.SYSTEM,
                notificationsEnabled = true,
                priceAlertsEnabled = true,
                marketingEmailsEnabled = false
            ),
            isGuest = false,
            createdAt = LocalDateTime.now(),
            lastLoginAt = LocalDateTime.now()
        )
    }

    /**
     * Map server errors to user-friendly messages
     */
    private fun mapRegistrationError(exception: Throwable): String {
        return when {
            exception.message?.contains("already registered") == true -> "This email is already registered. Please try logging in instead"
            exception.message?.contains("400") == true -> "Invalid registration data. Please check your information"
            exception.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection"
            exception.message?.contains("timeout", ignoreCase = true) == true -> "Request timed out. Please try again"
            else -> "Registration failed. Please try again"
        }
    }

    /**
     * Generate user ID from email
     */
    private fun generateUserId(email: String): String {
        return "user-${email.hashCode().toString().replace("-", "")}-${System.currentTimeMillis()}"
    }

    /**
     * Get password improvement suggestions
     */
    private fun getPasswordSuggestions(password: String): List<String> {
        val suggestions = mutableListOf<String>()

        if (!password.any { it.isUpperCase() }) {
            suggestions.add("Add uppercase letters")
        }
        if (!password.any { it.isLowerCase() }) {
            suggestions.add("Add lowercase letters")
        }
        if (!password.any { it.isDigit() }) {
            suggestions.add("Add numbers")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            suggestions.add("Add special characters")
        }
        if (password.length < 8) {
            suggestions.add("Make it at least 8 characters long")
        }

        return suggestions
    }
}

/**
 * Email availability check result
 */
sealed class EmailAvailabilityResult {
    object Available : EmailAvailabilityResult()
    object Taken : EmailAvailabilityResult()
    data class Invalid(val message: String) : EmailAvailabilityResult()
    data class Unknown(val message: String) : EmailAvailabilityResult()
}

/**
 * Password strength result with suggestions
 */
data class PasswordStrengthResult(
    val strength: PasswordStrength,
    val suggestions: List<String>,
    val isValid: Boolean
)