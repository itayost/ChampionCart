package com.example.championcart.domain.models

/**
 * Login request model
 * Matches: POST /login request
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Register request model
 * Matches: POST /register request
 */
data class RegisterRequest(
    val email: String,
    val password: String
)

/**
 * Auth response model
 * Matches: POST /login response
 */
data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "bearer"
) {
    val fullToken: String
        get() = "$tokenType $accessToken"
}

/**
 * Auth result wrapper for use cases
 */
sealed class AuthResult {
    data class Success(val user: User, val token: AuthResponse) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

/**
 * Login validation result
 */
sealed class LoginValidation {
    object Valid : LoginValidation()
    data class Invalid(val message: String) : LoginValidation()
}

/**
 * Registration validation result
 */
sealed class RegisterValidation {
    object Valid : RegisterValidation()
    data class Invalid(val message: String) : RegisterValidation()
}

/**
 * Password strength enum
 */
enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG
}

/**
 * Auth error types
 */
enum class AuthError {
    INVALID_EMAIL,
    INVALID_PASSWORD,
    EMAIL_ALREADY_EXISTS,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

// Extension functions for validation

/**
 * Validate email format
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Validate password strength
 */
fun String.getPasswordStrength(): PasswordStrength {
    val hasUpperCase = any { it.isUpperCase() }
    val hasLowerCase = any { it.isLowerCase() }
    val hasDigit = any { it.isDigit() }
    val hasSpecialChar = any { !it.isLetterOrDigit() }
    val isLongEnough = length >= 8

    val score = listOf(hasUpperCase, hasLowerCase, hasDigit, hasSpecialChar, isLongEnough).count { it }

    return when {
        score >= 4 -> PasswordStrength.STRONG
        score >= 2 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.WEAK
    }
}

/**
 * Validate login input
 */
fun validateLogin(email: String, password: String): LoginValidation {
    return when {
        email.isBlank() -> LoginValidation.Invalid("Email cannot be empty")
        password.isBlank() -> LoginValidation.Invalid("Password cannot be empty")
        !email.isValidEmail() -> LoginValidation.Invalid("Invalid email format")
        else -> LoginValidation.Valid
    }
}

/**
 * Validate registration input
 */
fun validateRegistration(email: String, password: String, confirmPassword: String? = null): RegisterValidation {
    return when {
        email.isBlank() -> RegisterValidation.Invalid("Email cannot be empty")
        password.isBlank() -> RegisterValidation.Invalid("Password cannot be empty")
        !email.isValidEmail() -> RegisterValidation.Invalid("Invalid email format")
        password.length < 6 -> RegisterValidation.Invalid("Password must be at least 6 characters")
        confirmPassword != null && password != confirmPassword -> RegisterValidation.Invalid("Passwords do not match")
        password.getPasswordStrength() == PasswordStrength.WEAK -> RegisterValidation.Invalid("Password is too weak")
        else -> RegisterValidation.Valid
    }
}