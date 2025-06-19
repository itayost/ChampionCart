package com.example.championcart.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.presentation.components.CityInfo
import com.example.championcart.presentation.components.CitySelectionDialog
import com.example.championcart.ui.theme.GlassmorphicShapes
import com.example.championcart.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val haptics = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val extendedColors = LocalExtendedColors.current

    // City selection dialog state
    var showCityDialog by remember { mutableStateOf(false) }

    // Check if city is selected, if not, show dialog immediately
    LaunchedEffect(selectedCity, uiState.availableCities) {
        if (selectedCity == null && uiState.availableCities.isNotEmpty()) {
            delay(300) // Small delay for smoother UX
            showCityDialog = true
        }
    }

    // Request focus on search field when screen opens
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                selectedCity = selectedCity,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
                onCityClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showCityDialog = true
                },
                onBackClick = {
                    keyboardController?.hide()
                    navController.navigateUp()
                },
                onClearClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.clearSearch()
                },
                focusRequester = focusRequester
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error ?: "An error occurred",
                        onRetry = {
                            viewModel.onSearchQueryChanged(searchQuery)
                        }
                    )
                }
                uiState.searchResults.isNotEmpty() -> {
                    SearchResults(
                        products = uiState.searchResults,
                        sortOrder = uiState.sortOrder,
                        onSortClick = { viewModel.toggleSortOrder() },
                        onProductClick = { product ->
                            // Navigate to product detail
                            navController.navigate("product/${product.itemCode}")
                        },
                        onAddToCart = { product ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addToCart(product)
                        }
                    )
                }
                uiState.hasSearched && searchQuery.isNotEmpty() -> {
                    EmptySearchState(query = searchQuery)
                }
                else -> {
                    SearchSuggestions(
                        recentSearches = uiState.recentSearches,
                        popularSearches = uiState.popularSearches,
                        onSuggestionClick = { suggestion ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onSearchQueryChanged(suggestion)
                        }
                    )
                }
            }

            // Cart addition confirmation
            AnimatedVisibility(
                visible = uiState.showAddedToCart,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = extendedColors.electricMint
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.lastAddedProduct} added to cart",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        TextButton(
                            onClick = { navController.navigate("cart") },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text("VIEW CART")
                        }
                    }
                }
            }
        }

        // City selection dialog
        if (showCityDialog) {
            CitySelectionDialog(
                cities = uiState.availableCities.map { city ->
                    CityInfo(
                        name = city,
                        nameHebrew = translateCityName(city),
                        totalStores = 0,
                        storeBreakdown = emptyMap(),
                        isPopular = isPopularCity(city)
                    )
                },
                currentCity = selectedCity ?: "",
                recentCities = emptyList(), // Could be stored in preferences
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    showCityDialog = false
                },
                onDismiss = {
                    // If no city is selected and we're dismissing, navigate back
                    if (selectedCity == null) {
                        navController.navigateUp()
                    } else {
                        showCityDialog = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    selectedCity: String?,
    onSearchQueryChange: (String) -> Unit,
    onCityClick: () -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    focusRequester: FocusRequester
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Search field
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(0.5f)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            "Search for products...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearClick) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // City selector
            if (selectedCity != null) {
                CityChip(
                    city = selectedCity,
                    onClick = onCityClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CityChip(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = extendedColors.electricMint.copy(alpha = 0.1f),
        border = BorderStroke(
            width = 1.dp,
            color = extendedColors.electricMint.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = extendedColors.electricMint
            )
            Text(
                text = city,
                style = MaterialTheme.typography.labelLarge,
                color = extendedColors.electricMint
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change city",
                modifier = Modifier.size(18.dp),
                tint = extendedColors.electricMint
            )
        }
    }
}

@Composable
private fun SearchResults(
    products: List<GroupedProduct>,
    sortOrder: SortOrder,
    onSortClick: () -> Unit,
    onProductClick: (GroupedProduct) -> Unit,
    onAddToCart: (GroupedProduct) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Results header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${products.size} products found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sort chip
                    FilterChip(
                        selected = false,
                        onClick = onSortClick,
                        label = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (sortOrder) {
                                        SortOrder.PRICE_LOW_TO_HIGH -> Icons.AutoMirrored.Filled.TrendingDown
                                        SortOrder.PRICE_HIGH_TO_LOW -> Icons.AutoMirrored.Filled.TrendingUp
                                        else -> Icons.Default.SortByAlpha
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = when (sortOrder) {
                                        SortOrder.PRICE_LOW_TO_HIGH -> "Price ↑"
                                        SortOrder.PRICE_HIGH_TO_LOW -> "Price ↓"
                                        SortOrder.NAME_A_TO_Z -> "A-Z"
                                        SortOrder.NAME_Z_TO_A -> "Z-A"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    )
                }
            }
        }

        // Product cards
        items(products) { product ->
            ProductSearchCard(
                product = product,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) }
            )
        }
    }
}

@Composable
private fun ProductSearchCard(
    product: GroupedProduct,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product name
            Text(
                text = product.itemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price comparison
            val bestPrice = product.prices.minByOrNull { it.price }
            if (bestPrice != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₪${bestPrice.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.electricMint
                        )
                        Text(
                            text = "${bestPrice.chain} - ${bestPrice.city}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val worstPrice = product.prices.maxByOrNull { it.price }
                    if (worstPrice != null && worstPrice.price > bestPrice.price) {
                        val savingsPercent = ((worstPrice.price - bestPrice.price) / worstPrice.price * 100).toInt()
                        if (savingsPercent > 0) {
                            Surface(
                                shape = CircleShape,
                                color = extendedColors.electricMint.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Save $savingsPercent%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = extendedColors.electricMint,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Store prices
            if (product.prices.size > 1) {
                Text(
                    text = "Available at ${product.prices.size} stores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(product.prices.take(3)) { price ->
                        StorePriceChip(
                            chain = price.chain,
                            price = price.price,
                            isLowest = price.price == product.prices.minByOrNull { it.price }?.price
                        )
                    }

                    if (product.prices.size > 3) {
                        item {
                            Text(
                                text = "+${product.prices.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClick) {
                    Text("COMPARE PRICES")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.electricMint
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD TO CART")
                }
            }
        }
    }
}

@Composable
private fun StorePriceChip(
    chain: String,
    price: Double,
    isLowest: Boolean
) {
    val extendedColors = LocalExtendedColors.current

    Surface(
        shape = CircleShape,
        color = if (isLowest) {
            extendedColors.electricMint.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (isLowest) {
            BorderStroke(
                width = 1.dp,
                color = extendedColors.electricMint
            )
        } else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = chain,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isLowest) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "₪${price}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isLowest) {
                    extendedColors.electricMint
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun SearchSuggestions(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Recent searches
        if (recentSearches.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    recentSearches.forEach { search ->
                        SuggestionItem(
                            text = search,
                            icon = Icons.Default.History,
                            onClick = { onSuggestionClick(search) }
                        )
                    }
                }
            }
        }

        // Popular searches
        item {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = extendedColors.electricMint,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Popular Right Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                popularSearches.forEach { search ->
                    SuggestionItem(
                        text = search,
                        icon = Icons.Default.TrendingUp,
                        iconTint = extendedColors.electricMint,
                        onClick = { onSuggestionClick(search) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                imageVector = Icons.Default.NorthWest,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    val extendedColors = LocalExtendedColors.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = extendedColors.electricMint
            )
            Text(
                text = "Searching for the best prices...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptySearchState(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No results found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "We couldn't find any products matching \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Try:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "• Check your spelling",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Use more general terms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Try different keywords",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = extendedColors.electricMint
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("TRY AGAIN")
            }
        }
    }
}

// Helper functions
private fun translateCityName(cityName: String): String {
    return when (cityName.lowercase()) {
        "tel aviv", "tel-aviv" -> "תל אביב"
        "jerusalem" -> "ירושלים"
        "haifa" -> "חיפה"
        "beer sheva", "beersheba" -> "באר שבע"
        "rishon lezion", "rishon le zion" -> "ראשון לציון"
        "petah tikva" -> "פתח תקווה"
        "ashdod" -> "אשדוד"
        "netanya" -> "נתניה"
        "holon" -> "חולון"
        "bnei brak" -> "בני ברק"
        "ramat gan" -> "רמת גן"
        "bat yam" -> "בת ים"
        "rehovot" -> "רחובות"
        "ashkelon" -> "אשקלון"
        "herzliya" -> "הרצליה"
        else -> cityName
    }
}

private fun isPopularCity(cityName: String): Boolean {
    val popularCities = listOf(
        "tel aviv", "tel-aviv", "jerusalem", "haifa",
        "beer sheva", "beersheba", "rishon lezion"
    )
    return popularCities.any { it.equals(cityName, ignoreCase = true) }
}