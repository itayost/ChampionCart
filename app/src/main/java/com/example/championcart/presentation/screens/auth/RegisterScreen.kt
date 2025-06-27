package com.example.championcart.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.championcart.presentation.components.GlassButton
import com.example.championcart.presentation.components.PlaceholderContent
import com.example.championcart.presentation.components.SecondaryGlassButton
import com.example.championcart.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    PlaceholderContent(
        title = "הרשמה",
        subtitle = "צור חשבון חדש והתחל לחסוך",
        icon = Icons.Default.PersonAdd
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            GlassButton(
                onClick = onNavigateToHome,
                text = "הירשם (דמו)",
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryGlassButton(
                onClick = onNavigateBack,
                text = "חזור להתחברות",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}