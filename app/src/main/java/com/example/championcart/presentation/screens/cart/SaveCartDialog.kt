package com.example.championcart.presentation.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.local.CartItem
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SaveCartUiState(
    val cartName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class SaveCartViewModel(
    private val cartApi: CartApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveCartUiState())
    val uiState: StateFlow<SaveCartUiState> = _uiState.asStateFlow()

    fun onCartNameChange(name: String) {
        _uiState.value = _uiState.value.copy(cartName = name, error = null)
    }

    fun saveCart(city: String, items: List<CartItem>) {
        val email = tokenManager.getUserEmail()
        if (email == null) {
            _uiState.value = _uiState.value.copy(
                error = "Please login to save carts"
            )
            return
        }

        if (_uiState.value.cartName.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a cart name"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val request = SaveCartRequest(
                    cartName = _uiState.value.cartName,
                    email = email,
                    city = city,
                    items = items.map {
                        com.example.championcart.data.models.request.CartItem(
                            itemName = it.itemName,
                            quantity = it.quantity
                        )
                    }
                )

                val response = cartApi.saveCart(request)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to save cart: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveCartDialog(
    cartItems: List<CartItem>,
    city: String,
    tokenManager: TokenManager,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel = remember {
        SaveCartViewModel(
            cartApi = NetworkModule.cartApi,
            tokenManager = tokenManager
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    // Handle success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaved()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Save Cart",
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Save your current cart (${cartItems.size} items) to access it later",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = uiState.cartName,
                    onValueChange = viewModel::onCartNameChange,
                    label = { Text("Cart Name") },
                    placeholder = { Text("e.g., Weekly Groceries") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.error != null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveCart(city, cartItems)
                },
                enabled = uiState.cartName.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !uiState.isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}