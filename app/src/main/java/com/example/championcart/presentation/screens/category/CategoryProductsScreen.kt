package com.example.championcart.presentation.screens.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun CategoryProductsScreen(
    categoryId: String,
    categoryName: String,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    PlaceholderContent(
        title = categoryName,
        subtitle = "כל המוצרים בקטגוריה",
        icon = Icons.Default.Category
    )
}