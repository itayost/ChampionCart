package com.example.championcart.presentation.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun CartScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToStoreComparison: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    PlaceholderContent(
        title = "העגלה שלי",
        subtitle = "ניהול העגלה וחישוב המחיר הטוב ביותר",
        icon = Icons.Default.ShoppingCart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            GlassButton(
                onClick = onNavigateToStoreComparison,
                text = "השווה מחירים בין חנויות",
                icon = {
                    Icon(
                        Icons.Default.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            GlassButton(
                onClick = { onNavigateToProduct("sample-product") },
                text = "הוסף מוצרים",
                style = GlassButtonStyle.Secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}