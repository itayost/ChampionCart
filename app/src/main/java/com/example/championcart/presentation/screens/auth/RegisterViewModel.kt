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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(
            fullName = name,
            fullNameError = null
        ) }
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
            passwordError = null,
            confirmPasswordError = if (it.confirmPassword.isNotEmpty() && password != it.confirmPassword) {
                "הסיסמאות אינן תואמות"
            } else null
        ) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = if (confirmPassword != it.password) {
                "הסיסמאות אינן תואמות"
            } else null
        ) }
    }

    fun onAcceptTermsChange(accepted: Boolean) {
        _uiState.update { it.copy(
            acceptedTerms = accepted,
            termsError = null
        ) }
    }

    fun register() {
        // Validate inputs
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authRepository.register(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password,
                name = _uiState.value.fullName.trim()
            ).collect { result ->
                result.fold(
                    onSuccess = { success ->
                        if (success) {
                            _uiState.update { it.copy(
                                isLoading = false,
                                registerSuccess = true
                            ) }
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = exception.message ?: "שגיאה בהרשמה"
                        ) }
                    }
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Full name validation
        if (_uiState.value.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "נא להזין שם מלא") }
            isValid = false
        } else if (_uiState.value.fullName.length < 2) {
            _uiState.update { it.copy(fullNameError = "השם חייב להכיל לפחות 2 תווים") }
            isValid = false
        }

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

        // Confirm password validation
        if (_uiState.value.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "נא לאמת את הסיסמה") }
            isValid = false
        } else if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "הסיסמאות אינן תואמות") }
            isValid = false
        }

        // Terms validation
        if (!_uiState.value.acceptedTerms) {
            _uiState.update { it.copy(termsError = "יש לאשר את תנאי השימוש") }
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

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptedTerms: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: Boolean = false
) {
    val isRegisterEnabled: Boolean
        get() = fullName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                acceptedTerms
}