package com.example.championcart.presentation.screens.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun TermsOfServiceScreen(
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "תנאי שימוש",
        subtitle = "התנאים וההגבלות שלנו",
        icon = Icons.Default.Gavel
    )
}