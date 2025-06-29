package com.example.championcart.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.TokenManager
import com.example.championcart.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Check if user has saved email (remember me)
        val savedEmail = tokenManager.getUserEmail()
        if (savedEmail != null && !tokenManager.isGuestMode()) {
            _uiState.update { it.copy(
                email = savedEmail,
                rememberMe = true
            ) }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(
            email = email,
            emailError = null
        ) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(
            password = password,
            passwordError = null
        ) }
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = rememberMe) }
    }

    fun login() {
        // Validate inputs
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authRepository.login(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).collect { result ->
                result.fold(
                    onSuccess = { success ->
                        if (success) {
                            // Save email if remember me is checked
                            if (_uiState.value.rememberMe) {
                                tokenManager.saveUserEmail(_uiState.value.email)
                            }

                            _uiState.update { it.copy(
                                isLoading = false,
                                loginSuccess = true
                            ) }
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = exception.message ?: "שגיאה בהתחברות"
                        ) }
                    }
                )
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            authRepository.setGuestMode(true)
            _uiState.update { it.copy(loginSuccess = true) }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Email validation
        if (_uiState.value.email.isBlank()) {
            _uiState.update { it.copy(emailError = "נא להזין כתובת מייל") }
            isValid = false
        } else if (!isValidEmail(_uiState.value.email)) {
            _uiState.update { it.copy(emailError = "כתובת מייל לא תקינה") }
            isValid = false
        }

        // Password validation
        if (_uiState.value.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "נא להזין סיסמה") }
            isValid = false
        } else if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(passwordError = "הסיסמה חייבת להכיל לפחות 6 תווים") }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}