package com.example.championcart.presentation.screens.store

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun StoreComparisonScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStore: (String) -> Unit
) {
    PlaceholderContent(
        title = "השוואת חנויות",
        subtitle = "השווה מחירי עגלה בין החנויות",
        icon = Icons.Default.CompareArrows
    )
}
