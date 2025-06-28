package com.example.championcart.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    initialQuery: String?,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery ?: "") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<ProductSearchResult>>(emptyList()) }

    val config = ChampionCartTheme.config

    // Simulate search results
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            delay(500) // Simulate API call
            searchResults = generateMockSearchResults(searchQuery)
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Modern Search Bar
        ModernSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { /* Search is automatic on query change */ },
            onBackClick = onNavigateBack,
            suggestions = getSearchSuggestions(searchQuery),
            showSuggestions = searchQuery.isNotEmpty() && searchResults.isEmpty(),
            isLoading = isSearching,
            modifier = Modifier.padding(top = SpacingTokens.M)
        )

        // Content
        if (searchQuery.isEmpty()) {
            // Empty state - show search prompt
            EmptySearchState(
                onSuggestionClick = { searchQuery = it }
            )
        } else if (isSearching) {
            // Loading state
            SearchLoadingState()
        } else if (searchResults.isNotEmpty()) {
            // Search results
            SearchResultsList(
                results = searchResults,
                onProductClick = onNavigateToProduct
            )
        } else {
            // No results state
            NoResultsState(
                query = searchQuery,
                onSuggestionClick = { searchQuery = it }
            )
        }
    }
}

@Composable
private fun EmptySearchState(
    onSuggestionClick: (String) -> Unit
) {
    val popularSearches = remember {
        listOf("חלב", "לחם", "ביצים", "במבה", "קוטג'", "עגבניות")
    }

    val categories = remember {
        listOf(
            SearchCategory("dairy", "מוצרי חלב", Icons.Default.LocalDrink, ChampionCartColors.Category.Dairy),
            SearchCategory("bakery", "מאפים", Icons.Default.Cake, ChampionCartColors.Category.Bakery),
            SearchCategory("produce", "פירות וירקות", Icons.Default.Spa, ChampionCartColors.Category.Produce),
            SearchCategory("meat", "בשר ודגים", Icons.Default.Restaurant, ChampionCartColors.Category.Meat)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacingTokens.L)
            .padding(bottom = 80.dp + SpacingTokens.L)
    ) {
        Spacer(modifier = Modifier.height(SpacingTokens.XL))

        // Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.05f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(SpacingTokens.XXL),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = ChampionCartColors.Brand.ElectricMint
                )

                Text(
                    text = "חיפוש מוצרים",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "חפש והשווה מחירים בין כל הסופרים",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingTokens.XXL))

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            GlassButton(
                text = "סרוק ברקוד",
                onClick = { /* TODO */ },
                icon = {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                },
                modifier = Modifier.weight(1f)
            )

            GlassButton(
                text = "חיפוש קולי",
                onClick = { /* TODO */ },
                icon = {
                    Icon(Icons.Default.Mic, contentDescription = null)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(SpacingTokens.XXL))

        // Popular Searches
        Text(
            text = "חיפושים פופולריים",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = SpacingTokens.M)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            items(popularSearches) { search ->
                ElectricChipButton(
                    text = search,
                    onClick = { onSuggestionClick(search) },
                    selected = false
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingTokens.XXL))

        // Categories
        Text(
            text = "חפש לפי קטגוריה",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = SpacingTokens.M)
        )

        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                row.forEach { category ->
                    CategoryCard(
                        title = category.name,
                        icon = category.icon,
                        itemCount = 0, // Hide count in search
                        onClick = { onSuggestionClick(category.name) },
                        gradient = listOf(
                            category.color,
                            category.color.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(SpacingTokens.M))
        }
    }
}

@Composable
private fun SearchLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpacingTokens.XXL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = ChampionCartColors.Brand.ElectricMint,
                strokeWidth = 3.dp
            )

            Text(
                text = "מחפש...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchResultsList(
    results: List<ProductSearchResult>,
    onProductClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = SpacingTokens.L,
            vertical = SpacingTokens.L,
            bottom = 80.dp + SpacingTokens.L
        ),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "נמצאו ${results.size} תוצאות",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                ElectricTextButton(
                    text = "סינון",
                    icon = {
                        Icon(Icons.Default.FilterList, contentDescription = null)
                    },
                    onClick = { /* TODO: Filter */ }
                )
            }
        }

        itemsIndexed(results) { index, result ->
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(index) {
                delay(index * 50L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                ProductPriceCard(
                    productName = result.name,
                    prices = result.prices,
                    onAddToCart = { onProductClick(result.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun NoResultsState(
    query: String,
    onSuggestionClick: (String) -> Unit
) {
    val suggestions = remember(query) {
        listOf("חלב", "לחם", "ביצים").filter { it != query }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpacingTokens.XXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(SpacingTokens.L))

        Text(
            text = "לא נמצאו תוצאות עבור \"$query\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(SpacingTokens.S))

        Text(
            text = "נסה לחפש במילים אחרות",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(SpacingTokens.XL))

        Text(
            text = "הצעות:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(SpacingTokens.M))

        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            suggestions.forEach { suggestion ->
                ElectricChipButton(
                    text = suggestion,
                    onClick = { onSuggestionClick(suggestion) },
                    selected = false
                )
            }
        }
    }
}

// Helper functions
private fun getSearchSuggestions(query: String): List<String> {
    if (query.isEmpty()) return emptyList()

    val allSuggestions = listOf(
        "חלב תנובה", "חלב 3%", "חלב 1%",
        "לחם אחיד", "לחם מלא", "לחם קל",
        "ביצים L", "ביצים M", "ביצים אורגניות"
    )

    return allSuggestions
        .filter { it.contains(query, ignoreCase = true) }
        .take(5)
}

private fun generateMockSearchResults(query: String): List<ProductSearchResult> {
    // Mock data generation
    return if (query.isNotEmpty()) {
        listOf(
            ProductSearchResult(
                id = "1",
                name = "$query תנובה",
                prices = listOf(
                    StorePrice("רמי לוי", 5.90f),
                    StorePrice("שופרסל", 6.90f),
                    StorePrice("ויקטורי", 6.50f)
                )
            ),
            ProductSearchResult(
                id = "2",
                name = "$query טרה",
                prices = listOf(
                    StorePrice("ויקטורי", 5.50f),
                    StorePrice("מגה", 6.50f),
                    StorePrice("רמי לוי", 6.00f)
                )
            ),
            ProductSearchResult(
                id = "3",
                name = "$query שטראוס",
                prices = listOf(
                    StorePrice("אושר עד", 7.90f),
                    StorePrice("שופרסל", 8.90f),
                    StorePrice("קואופ", 8.50f)
                )
            )
        )
    } else {
        emptyList()
    }
}

// Data classes
data class SearchCategory(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class ProductSearchResult(
    val id: String,
    val name: String,
    val prices: List<StorePrice>
)