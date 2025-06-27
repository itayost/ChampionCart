package com.example.championcart.presentation.screens.cart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun SavedCartsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    PlaceholderContent(
        title = "עגלות שמורות",
        subtitle = "העגלות השמורות שלך",
        icon = Icons.Default.Bookmark
    )
}