package com.example.championcart.presentation.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailError = if (email.isNotBlank()) validateEmail(email) else null
            )
        }
    }

    fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = if (password.isNotBlank()) validatePassword(password) else null
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotBlank())
                    validateConfirmPassword(confirmPassword, _state.value.password) else null
            )
        }
    }

    // MISSING FUNCTION - This was being called in LoginRegisterScreen
    fun toggleAuthMode() {
        _state.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                error = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                confirmPassword = ""
            )
        }
    }

    // Alias for backwards compatibility (if needed)
    fun toggleMode() = toggleAuthMode()

    fun login() {
        val currentState = _state.value

        // Validate inputs
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }

        if (emailError != null || passwordError != null) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = authRepository.login(
                    email = currentState.email.trim(),
                    password = currentState.password
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
                                error = when {
                                    exception.message?.contains("401") == true ->
                                        "Invalid email or password"
                                    exception.message?.contains("network") == true ->
                                        "Network error. Please check your connection"
                                    else -> "Login failed. Please try again"
                                }
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "An unexpected error occurred"
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

        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = authRepository.register(
                    email = currentState.email.trim(),
                    password = currentState.password
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
                                error = when {
                                    exception.message?.contains("already") == true ->
                                        "This email is already registered"
                                    exception.message?.contains("network") == true ->
                                        "Network error. Please check your connection"
                                    else -> "Registration failed. Please try again"
                                }
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetState() {
        _state.update {
            AuthState()
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != password -> "Passwords don't match"
            else -> null
        }
    }
}

// Alternative ViewModel without Hilt for testing
class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}