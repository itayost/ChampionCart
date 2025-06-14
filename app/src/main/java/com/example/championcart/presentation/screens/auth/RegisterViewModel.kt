package com.example.championcart.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = if (email.isBlank()) null else validateEmail(email),
            error = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = if (password.isBlank()) null else validatePassword(password),
            confirmPasswordError = if (_uiState.value.confirmPassword.isNotEmpty()) {
                validatePasswordMatch(password, _uiState.value.confirmPassword)
            } else null,
            error = null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = if (confirmPassword.isBlank()) null else
                validatePasswordMatch(_uiState.value.password, confirmPassword),
            error = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showPassword = !_uiState.value.showPassword
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showConfirmPassword = !_uiState.value.showConfirmPassword
        )
    }

    fun isFormValid(): Boolean {
        val state = _uiState.value
        return state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null
    }

    fun register() {
        // Validate all fields first
        if (!isFormValid()) {
            // Trigger validation for all fields
            onEmailChange(_uiState.value.email)
            onPasswordChange(_uiState.value.password)
            onConfirmPasswordChange(_uiState.value.confirmPassword)

            // Show general error if fields are empty
            if (_uiState.value.email.isBlank() ||
                _uiState.value.password.isBlank() ||
                _uiState.value.confirmPassword.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    error = "Please fill in all fields"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            // Add a small delay for better UX
            delay(500)

            registerUseCase(_uiState.value.email, _uiState.value.password)
                .fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = getErrorMessage(exception)
                        )
                    }
                )
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Please enter a valid email address"
            !email.contains("@") -> "Email must contain @"
            !email.contains(".") -> "Email must contain a domain"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isLetterOrDigit() } ->
                "Password must contain letters or numbers"
            else -> null
        }
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> null
            password != confirmPassword -> "Passwords don't match"
            else -> null
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("already registered") == true ->
                "This email is already registered. Please login instead."
            exception.message?.contains("network") == true ->
                "Network error. Please check your connection and try again."
            exception.message?.contains("timeout") == true ->
                "Connection timeout. Please try again."
            exception.message?.contains("Invalid email format") == true ->
                "Please enter a valid email address."
            exception.message?.contains("Password must be at least") == true ->
                "Password must be at least 6 characters long."
            else ->
                exception.message ?: "Registration failed. Please try again."
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}