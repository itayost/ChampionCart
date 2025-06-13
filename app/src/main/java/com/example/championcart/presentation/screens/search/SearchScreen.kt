package com.example.championcart.presentation.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.ProductStorePrice
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.CityIndicator
import com.example.championcart.presentation.components.rememberCitySelectionDialog
import androidx.compose.material3.MaterialTheme
import com.example.championcart.presentation.theme.extendedColors

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val viewModel: SearchViewModel = viewModel(factory = ViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // City selection
    val tokenManager = remember { TokenManager(context) }
    val (currentCity, showCityDialog) = rememberCitySelectionDialog(
        tokenManager = tokenManager,
        onCitySelected = { city ->
            viewModel.onCityChange(city)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // City indicator - clickable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Searching in",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CityIndicator(
                city = uiState.selectedCity,
                onClick = showCityDialog
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for products...") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.searchProducts()
                    },
                    enabled = uiState.searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    viewModel.searchProducts()
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Results
        if (!uiState.isLoading && uiState.groupedProducts.isNotEmpty()) {
            Text(
                text = "Found ${uiState.groupedProducts.size} products",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.groupedProducts) { groupedProduct ->
                    GroupedProductCard(
                        groupedProduct = groupedProduct,
                        cartQuantity = uiState.cartItemsCount[groupedProduct.itemCode] ?: 0,
                        onAddToCart = { viewModel.addToCart(groupedProduct) }
                    )
                }
            }
        }

        // Empty state
        if (!uiState.isLoading && uiState.searchQuery.isNotBlank() && uiState.groupedProducts.isEmpty() && uiState.error == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products found for \"${uiState.searchQuery}\" in $currentCity",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun GroupedProductCard(
    groupedProduct: GroupedProduct,
    cartQuantity: Int,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product Name
            Text(
                text = groupedProduct.itemName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Best Price Badge and Add to Cart
            if (groupedProduct.lowestPrice != null && groupedProduct.highestPrice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price Range
                    Column {
                        Row {
                            Text(
                                text = "₪${String.format("%.2f", groupedProduct.lowestPrice)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.extendedColors.savings
                            )
                            if (groupedProduct.highestPrice != groupedProduct.lowestPrice) {
                                Text(
                                    text = " - ₪${String.format("%.2f", groupedProduct.highestPrice)}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Savings
                        if (groupedProduct.savings > 0) {
                            Text(
                                text = "Save ₪${String.format("%.2f", groupedProduct.savings)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.extendedColors.savings
                            )
                        }
                    }

                    // Add to Cart Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (cartQuantity > 0) {
                            Badge {
                                Text(text = cartQuantity.toString())
                            }
                        }

                        Button(
                            onClick = onAddToCart,
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Store Prices
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Available at:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                groupedProduct.storePrices.forEach { storePrice ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = storePrice.chain.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = if (storePrice.price == groupedProduct.lowestPrice)
                                FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₪${String.format("%.2f", storePrice.price)}",
                            fontSize = 14.sp,
                            fontWeight = if (storePrice.price == groupedProduct.lowestPrice)
                                FontWeight.Bold else FontWeight.Normal,
                            color = if (storePrice.price == groupedProduct.lowestPrice)
                                MaterialTheme.extendedColors.savings else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Item Code
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Code: ${groupedProduct.itemCode}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}