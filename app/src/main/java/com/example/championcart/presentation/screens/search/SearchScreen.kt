package com.example.championcart.presentation.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun SearchScreen(
    initialQuery: String?,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search header
        TopSearchBar(
            query = initialQuery ?: "",
            onQueryChange = { /* TODO */ },
            onBackClick = onNavigateBack,
            onSearchClick = { /* TODO */ }
        )

        // Placeholder content
        PlaceholderContent(
            title = "חיפוש מוצרים",
            subtitle = "חפש והשווה מחירים בין כל הסופרים",
            icon = Icons.Default.Search
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                // Sample search suggestions
                SearchSuggestionChip(
                    text = "חלב 3%",
                    onClick = { onNavigateToProduct("milk-3") }
                )
                SearchSuggestionChip(
                    text = "לחם אחיד",
                    onClick = { onNavigateToProduct("bread-uniform") }
                )
                SearchSuggestionChip(
                    text = "ביצים L",
                    onClick = { onNavigateToProduct("eggs-large") }
                )
            }
        }
    }
}

@Composable
fun SearchSuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    SecondaryGlassButton(
        onClick = onClick,
        text = text,
        size = ButtonSize.Small,
        modifier = Modifier.fillMaxWidth()
    )
}