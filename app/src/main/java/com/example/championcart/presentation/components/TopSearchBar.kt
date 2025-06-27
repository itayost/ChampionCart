package com.example.championcart.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ChampionCartTheme.colors.surface.copy(alpha = 0.95f),
        shadowElevation = Elevation.Component.topAppBar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "חזור"
                )
            }

            GlassTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "חפש מוצרים...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = ChampionCartTheme.colors.onSurfaceVariant
                    )
                },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "נקה"
                            )
                        }
                    }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.xs)
            )

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "סנן"
                )
            }
        }
    }
}