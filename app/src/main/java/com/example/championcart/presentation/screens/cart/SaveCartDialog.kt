package com.example.championcart.presentation.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.api.CartApi
import com.example.championcart.data.local.CartItem
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.request.SaveCartRequest
import com.example.championcart.di.NetworkModule
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SaveCartUiState(
    val cartName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
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

    fun saveCart(items: List<CartItem>, city: String, onSuccess: () -> Unit) {
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
                    cartName = _uiState.value.cartName.trim(),
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
                        isSaved = true
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to save cart"
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
        SaveCartViewModel(NetworkModule.cartApi, tokenManager)
    }
    val uiState by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = ComponentShapes.Dialog
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.paddingLarge)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Save Cart",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

                // Cart name input
                OutlinedTextField(
                    value = uiState.cartName,
                    onValueChange = viewModel::onCartNameChange,
                    label = {
                        Text(
                            "Cart Name",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            "e.g., Weekly Shopping",
                            style = AppTextStyles.searchHint
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    isError = uiState.error != null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = ComponentShapes.TextField
                )

                // Error message
                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

                // Cart preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = ComponentShapes.Card
                ) {
                    Column(
                        modifier = Modifier.padding(Dimensions.paddingMedium)
                    ) {
                        Text(
                            text = "Cart Contents (${cartItems.size} items)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
                        ) {
                            items(cartItems) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.itemName} (${item.quantity})",
                                        style = if (isHebrewText(item.itemName)) {
                                            AppTextStyles.hebrewText
                                        } else {
                                            MaterialTheme.typography.bodyMedium
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    item.selectedPrice?.let { price ->
                                        Text(
                                            text = "₪${String.format("%.2f", price * item.quantity)}",
                                            style = AppTextStyles.priceDisplaySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

                // City info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "City:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = city,
                        style = if (isHebrewText(city)) {
                            AppTextStyles.hebrewTextBold
                        } else {
                            MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading,
                        shape = ComponentShapes.Button
                    ) {
                        Text(
                            "Cancel",
                            style = AppTextStyles.buttonText
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveCart(cartItems, city) {
                                onSaved()
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading && uiState.cartName.isNotBlank(),
                        shape = ComponentShapes.Button
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Save Cart",
                                style = AppTextStyles.buttonText
                            )
                        }
                    }
                }
            }
        }
    }
}



// Helper function to detect Hebrew text
private fun isHebrewText(text: String): Boolean {
    return text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }
}