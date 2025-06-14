package com.example.championcart.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false,
    val showPassword: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null // Clear error when user types
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null // Clear error when user types
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showPassword = !_uiState.value.showPassword
        )
    }

    fun login() {
        // Don't proceed if already loading or fields are empty
        if (_uiState.value.isLoading ||
            _uiState.value.email.isBlank() ||
            _uiState.value.password.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            // Add a small delay for better UX (shows loading state)
            delay(500)

            loginUseCase(_uiState.value.email, _uiState.value.password)
                .fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
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

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("Invalid email or password") == true ->
                "Invalid email or password. Please try again."
            exception.message?.contains("Network") == true ->
                "Network error. Please check your connection."
            exception.message?.contains("timeout") == true ->
                "Connection timeout. Please try again."
            exception.message?.contains("Email and password cannot be empty") == true ->
                "Please enter both email and password."
            exception.message?.contains("Invalid email format") == true ->
                "Please enter a valid email address."
            else ->
                exception.message ?: "An unexpected error occurred. Please try again."
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}