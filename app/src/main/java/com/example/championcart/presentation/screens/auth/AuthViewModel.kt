package com.example.championcart.presentation.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.repository.AuthRepository
import com.example.championcart.domain.models.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Authentication State
 * Comprehensive state management for login/register flows
 */
data class AuthState(
    // Form data
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // UI state
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,

    // Error handling
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Additional features
    val rememberMe: Boolean = false,
    val agreedToTerms: Boolean = false,
    val enableBiometrics: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // ============ PUBLIC METHODS ============

    fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailError = if (email.isNotBlank()) validateEmail(email) else null,
                error = null
            )
        }
    }

    fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = if (password.isNotBlank()) validatePassword(password) else null,
                error = null
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotBlank()) {
                    validateConfirmPassword(confirmPassword, _state.value.password)
                } else null,
                error = null
            )
        }
    }

    fun toggleMode() {
        _state.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                error = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                confirmPassword = "", // Clear confirm password when switching modes
                agreedToTerms = false // Reset terms agreement
            )
        }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(showPassword = !it.showPassword) }
    }

    fun toggleConfirmPasswordVisibility() {
        _state.update { it.copy(showConfirmPassword = !it.showConfirmPassword) }
    }

    fun toggleRememberMe() {
        _state.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun toggleTermsAgreement() {
        _state.update { it.copy(agreedToTerms = !it.agreedToTerms) }
    }

    fun toggleBiometrics() {
        _state.update { it.copy(enableBiometrics = !it.enableBiometrics) }
    }

    fun login() {
        val currentState = _state.value

        // Validate inputs
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        // Perform login
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = authRepository.login(
                    email = currentState.email.trim(),
                    password = currentState.password,
                    rememberMe = currentState.rememberMe
                )

                result.fold(
                    onSuccess = { user ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = getErrorMessage(exception)
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Login failed. Please check your connection and try again."
                    )
                }
            }
        }
    }

    fun register() {
        val currentState = _state.value

        // Validate inputs
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = validateConfirmPassword(
            currentState.confirmPassword,
            currentState.password
        )

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        // Check terms agreement
        if (!currentState.agreedToTerms) {
            _state.update {
                it.copy(error = "Please agree to the terms and conditions to continue")
            }
            return
        }

        // Perform registration
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = authRepository.register(
                    email = currentState.email.trim(),
                    password = currentState.password,
                    //enableBiometrics = currentState.enableBiometrics
                )

                result.fold(
                    onSuccess = { user ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = getErrorMessage(exception)
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Registration failed. Please check your connection and try again."
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetState() {
        _state.value = AuthState()
    }

    // ============ VALIDATION METHODS ============

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when (exception.message?.lowercase()) {
            "user not found", "invalid credentials" -> "Invalid email or password"
            "user already exists", "email already registered" -> "An account with this email already exists"
            "weak password" -> "Password is too weak. Please choose a stronger password"
            "network error", "timeout" -> "Network error. Please check your connection"
            "server error" -> "Server error. Please try again later"
            else -> exception.message ?: "An unexpected error occurred. Please try again"
        }
    }

    // ============ UTILITY METHODS ============

    fun isFormValid(): Boolean {
        val currentState = _state.value
        return if (currentState.isLoginMode) {
            currentState.email.isNotBlank() &&
                    currentState.password.isNotBlank() &&
                    currentState.emailError == null &&
                    currentState.passwordError == null
        } else {
            currentState.email.isNotBlank() &&
                    currentState.password.isNotBlank() &&
                    currentState.confirmPassword.isNotBlank() &&
                    currentState.emailError == null &&
                    currentState.passwordError == null &&
                    currentState.confirmPasswordError == null &&
                    currentState.agreedToTerms
        }
    }

    fun getPasswordStrength(): PasswordStrength {
        val password = _state.value.password
        return when {
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 8 -> PasswordStrength.MEDIUM
            password.length >= 8 &&
                    password.any { it.isDigit() } &&
                    password.any { it.isLetter() } &&
                    password.any { it.isUpperCase() } -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }
}

/**
 * Password strength indicator
 */
enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}