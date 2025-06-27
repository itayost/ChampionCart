package com.example.championcart.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToSavedCarts: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    PlaceholderContent(
        title = "הפרופיל שלי",
        subtitle = "ניהול החשבון והעדפות אישיות",
        icon = Icons.Default.Person
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ProfileMenuItem(
                title = "עגלות שמורות",
                icon = Icons.Default.BookmarkBorder,
                onClick = onNavigateToSavedCarts
            )

            ProfileMenuItem(
                title = "הגדרות",
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            SecondaryGlassButton(
                onClick = onNavigateToLogin,
                text = "התנתק",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        intensity = GlassIntensity.Light
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampionCartColors.Brand.ElectricMint
                )
                Text(
                    text = title,
                    style = ChampionCartTypography.bodyLarge
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ChampionCartTheme.colors.onSurfaceVariant
            )
        }
    }
}