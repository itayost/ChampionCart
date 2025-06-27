package com.example.championcart.presentation.screens.product

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "פרטי מוצר",
        subtitle = "מידע מלא על המוצר והשוואת מחירים",
        icon = Icons.Default.ShoppingBag
    )
}