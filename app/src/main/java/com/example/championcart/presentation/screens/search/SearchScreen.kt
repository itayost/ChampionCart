package com.example.championcart.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun SearchScreen(
    initialQuery: String?,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery ?: "") }
    val darkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Using TopSearchBar component
        TopSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onBackClick = onNavigateBack,
            onSearchClick = { /* TODO: Implement search */ }
        )

        // Main content using our components
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            // Using HeroGlassCard for the main search prompt
            HeroGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "חפש והשווה מחירים בין כל הסופרים",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Quick stats using StatsGlassCard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                StatsGlassCard(
                    title = "מוצרים",
                    value = "2,456",
                    icon = {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = ChampionCartColors.Brand.ElectricMint
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                StatsGlassCard(
                    title = "חנויות",
                    value = "6",
                    icon = {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = ChampionCartColors.Brand.CosmicPurple
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Popular searches section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                Text(
                    text = "חיפושים פופולריים",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Using GlassChipButton for search suggestions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    GlassChipButton(
                        text = "חלב",
                        onClick = { searchQuery = "חלב" }
                    )
                    GlassChipButton(
                        text = "לחם",
                        onClick = { searchQuery = "לחם" }
                    )
                    GlassChipButton(
                        text = "ביצים",
                        onClick = { searchQuery = "ביצים" }
                    )
                }
            }

            // Recent searches using ProductListItem style
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                Text(
                    text = "חיפושים אחרונים",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Sample products using our ProductListItem
                val sampleProducts = listOf(
                    ProductItemData(
                        id = "1",
                        name = "חלב תנובה 3%",
                        category = "מוצרי חלב",
                        bestPrice = "₪5.90",
                        bestStore = "שופרסל",
                        isFavorite = true
                    ),
                    ProductItemData(
                        id = "2",
                        name = "לחם אחיד פרוס",
                        category = "מאפים",
                        bestPrice = "₪8.90",
                        bestStore = "רמי לוי",
                        isFavorite = false
                    ),
                    ProductItemData(
                        id = "3",
                        name = "ביצים L",
                        category = "ביצים",
                        bestPrice = "₪19.90",
                        bestStore = "ויקטורי",
                        isFavorite = false
                    )
                )

                sampleProducts.forEachIndexed { index, product ->
                    ProductListItem(
                        product = product,
                        onClick = { onNavigateToProduct(product.id) },
                        index = index
                    )
                }
            }

            // Action buttons using our button components
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                GlassButton(
                    text = "סרוק ברקוד",
                    onClick = { /* TODO: Barcode scanner */ },
                    icon = {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                SecondaryGlassButton(
                    text = "חיפוש קולי",
                    onClick = { /* TODO: Voice search */ },
                    icon = {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}