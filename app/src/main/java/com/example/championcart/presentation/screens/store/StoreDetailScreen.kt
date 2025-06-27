package com.example.championcart.presentation.screens.store

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun StoreDetailScreen(
    storeId: String,
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "פרטי חנות",
        subtitle = "מידע על החנות ומחירים",
        icon = Icons.Default.Store
    )
}
