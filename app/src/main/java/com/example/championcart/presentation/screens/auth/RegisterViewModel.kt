package com.example.championcart.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email),
            error = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = if (_uiState.value.confirmPassword.isNotEmpty()) {
                validatePasswordMatch(password, _uiState.value.confirmPassword)
            } else null,
            error = null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validatePasswordMatch(_uiState.value.password, confirmPassword),
            error = null
        )
    }

    fun isFormValid(): Boolean {
        return _uiState.value.email.isNotBlank() &&
                _uiState.value.password.isNotBlank() &&
                _uiState.value.confirmPassword.isNotBlank() &&
                _uiState.value.emailError == null &&
                _uiState.value.passwordError == null &&
                _uiState.value.confirmPasswordError == null
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            registerUseCase(_uiState.value.email, _uiState.value.password)
                .fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = when {
                                exception.message?.contains("already registered") == true ->
                                    "This email is already registered. Please login instead."
                                exception.message?.contains("network") == true ->
                                    "Network error. Please check your connection."
                                else -> exception.message ?: "Registration failed"
                            }
                        )
                    }
                )
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null
            password.length < 6 -> "Password must be at least 6 characters"
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
}