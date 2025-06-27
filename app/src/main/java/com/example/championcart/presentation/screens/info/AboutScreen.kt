package com.example.championcart.presentation.screens.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "אודות",
        subtitle = "ChampionCart - חסכון חכם בכל קנייה",
        icon = Icons.Default.Info
    )
}