package com.example.championcart.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Empty state components for lists and screens
 */

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        message?.let {
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            Button(
                onClick = onAction,
                shape = Shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandColors.ElectricMint
                )
            ) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Rounded.ShoppingCart,
        title = "העגלה שלך ריקה",
        message = "הוסף מוצרים כדי להתחיל לחסוך!",
        actionText = "התחל לקנות",
        onAction = onStartShopping,
        modifier = modifier
    )
}

@Composable
fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Rounded.SearchOff,
        title = "לא נמצאו תוצאות",
        message = "לא מצאנו מוצרים התואמים ל-\"$query\"",
        modifier = modifier
    )
}

@Composable
fun EmptySavedCartsState(
    onCreateCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Rounded.BookmarkBorder,
        title = "אין עגלות שמורות",
        message = "שמור עגלות כדי לקנות אותן שוב בקלות",
        actionText = "צור עגלה חדשה",
        onAction = onCreateCart,
        modifier = modifier
    )
}

@Composable
fun NoConnectionState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Rounded.WifiOff,
        title = "אין חיבור לאינטרנט",
        message = "בדוק את החיבור שלך ונסה שוב",
        actionText = "נסה שוב",
        onAction = onRetry,
        modifier = modifier
    )
}