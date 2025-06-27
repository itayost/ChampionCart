package com.example.championcart.presentation.screens.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "עזרה",
        subtitle = "מדריכים ושאלות נפוצות",
        icon = Icons.Default.Help
    )
}