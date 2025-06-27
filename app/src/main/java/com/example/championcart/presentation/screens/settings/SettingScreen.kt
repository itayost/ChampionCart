package com.example.championcart.presentation.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCitySelection: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsOfService: () -> Unit
) {
    PlaceholderContent(
        title = "הגדרות",
        subtitle = "התאמה אישית של האפליקציה",
        icon = Icons.Default.Settings
    )
}