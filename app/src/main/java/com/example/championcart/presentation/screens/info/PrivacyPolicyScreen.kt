package com.example.championcart.presentation.screens.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.PlaceholderContent

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    PlaceholderContent(
        title = "מדיניות פרטיות",
        subtitle = "איך אנחנו שומרים על הפרטיות שלך",
        icon = Icons.Default.PrivacyTip
    )
}