package com.example.championcart.presentation.screens.savedcarts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.championcart.data.local.CartManager
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.data.models.response.SavedCart
import com.example.championcart.di.NetworkModule
import androidx.compose.material3.MaterialTheme
import com.example.championcart.ui.theme.extendedColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SavedCartsUiState(
    val savedCarts: List<SavedCart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCity: String = "Tel Aviv",
    val userEmail: String? = null
)

class SavedCartsViewModel(
    private val cartApi: CartApi,
    private val tokenManager: TokenManager,
    private val cartManager: CartManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedCartsUiState())
    val uiState: StateFlow<SavedCartsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            selectedCity = tokenManager.getSelectedCity(),
            userEmail = tokenManager.getUserEmail()
        )
        loadSavedCarts()
    }

    fun loadSavedCarts() {
        val email = _uiState.value.userEmail
        if (email == null) {
            _uiState.value = _uiState.value.copy(
                error = "Please login to view saved carts"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = cartApi.getSavedCarts(email, _uiState.value.selectedCity)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        savedCarts = response.body()?.savedCarts ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load saved carts"
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

    fun loadCart(savedCart: SavedCart) {
        // Clear current cart
        cartManager.clearCart()

        // Add items from saved cart
        savedCart.items.forEach { item ->
            cartManager.addToCart(
                itemCode = "", // We don't have item code in saved cart
                itemName = item.itemName,
                chain = null,
                price = item.price
            )

            // Update quantity if more than 1
            for (i in 1 until item.quantity) {
                cartManager.addToCart(
                    itemCode = "",
                    itemName = item.itemName,
                    chain = null,
                    price = item.price
                )
            }
        }
    }

    fun deleteCart(cartName: String) {
        // TODO: Implement delete API when available
        viewModelScope.launch {
            // For now, just reload the list
            loadSavedCarts()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCartsScreen(
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SavedCartsViewModel = remember {
        SavedCartsViewModel(
            cartApi = NetworkModule.cartApi,
            tokenManager = TokenManager(context),
            cartManager = CartManager.getInstance(context)
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Carts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadSavedCarts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.error ?: "An error occurred",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadSavedCarts() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.savedCarts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No saved carts yet",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Save your shopping carts to access them later",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.savedCarts) { savedCart ->
                        SavedCartItem(
                            savedCart = savedCart,
                            onLoad = {
                                viewModel.loadCart(savedCart)
                                onNavigateToCart()
                            },
                            onDelete = {
                                viewModel.deleteCart(savedCart.cartName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCartItem(
    savedCart: SavedCart,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Cart?") },
            text = { Text("Are you sure you want to delete '${savedCart.cartName}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = savedCart.cartName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${savedCart.items.size} items • ${savedCart.city}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Date - Fixed to avoid try-catch around composable
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val dateString = try {
                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            .parse(savedCart.createdAt)
                        date?.let { dateFormat.format(it) }
                    } catch (e: Exception) {
                        null
                    }

                    dateString?.let {
                        Text(
                            text = "Saved on $it",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Delete button
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items preview
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                savedCart.items.take(3).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.itemName} (${item.quantity})",
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₪${String.format("%.2f", item.price * item.quantity)}",
                            fontSize = 14.sp
                        )
                    }
                }

                if (savedCart.items.size > 3) {
                    Text(
                        text = "... and ${savedCart.items.size - 3} more items",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current total prices
            savedCart.currentTotal?.let { totals ->
                val bestPrice = totals.values.minOrNull() ?: 0.0
                val bestChain = totals.entries.minByOrNull { it.value }?.key ?: ""

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current best price:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₪${String.format("%.2f", bestPrice)} at $bestChain",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.savings
                        )
                    }

                    Button(
                        onClick = onLoad,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Load Cart")
                    }
                }
            }
        }
    }
}