package com.example.championcart.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.championcart.presentation.components.GlassButton
import com.example.championcart.presentation.components.GlassCard
import com.example.championcart.presentation.components.PlaceholderContent
import com.example.championcart.presentation.components.SecondaryGlassButton
import com.example.championcart.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onSkipLogin: () -> Unit
) {
    PlaceholderContent(
        title = "התחברות",
        subtitle = "ברוכים הבאים לChampionCart",
        icon = Icons.Default.Login
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            GlassButton(
                onClick = onNavigateToHome,
                text = "התחבר (דמו)",
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryGlassButton(
                onClick = onNavigateToRegister,
                text = "צור חשבון חדש",
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = onSkipLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "המשך כאורח",
                    style = CustomTextStyles.storeName,
                    color = ChampionCartTheme.colors.primary
                )
            }
        }
    }
}