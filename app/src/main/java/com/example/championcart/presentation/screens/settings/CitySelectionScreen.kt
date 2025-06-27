package com.example.championcart.presentation.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun CitySelectionScreen(
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "בחירת עיר",
        subtitle = "בחר את העיר שלך למחירים מדויקים",
        icon = Icons.Default.LocationCity
    )
}