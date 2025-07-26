package com.example.championcart.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.branding.ChampionCartLogo
import com.example.championcart.presentation.components.branding.LogoSize
import com.example.championcart.presentation.components.branding.LogoVariant
import com.example.championcart.ui.theme.*

/**
 * Enhanced empty state components with ChampionCart branding
 */

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showLogo: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLogo) {
            // ChampionCart logo for brand reinforcement
            ChampionCartLogo(
                variant = LogoVariant.Icon,
                size = LogoSize.Medium,
                showBackground = false
            )

            Spacer(modifier = Modifier.height(Spacing.l))
        }

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
    // Subtle floating animation for the cart icon
    val infiniteTransition = rememberInfiniteTransition(label = "empty_cart")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ChampionCart logo to reinforce brand when cart is empty
        ChampionCartLogo(
            variant = LogoVariant.Icon,
            size = LogoSize.Large,
            modifier = Modifier.scale(iconScale)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "העגלה שלך ריקה",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = "הוסף מוצרים כדי להתחיל לחסוך!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Button(
            onClick = onStartShopping,
            shape = Shapes.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandColors.ElectricMint
            ),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Rounded.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.s))
            Text("התחל לקנות")
        }
    }
}

@Composable
fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier,
    onSearchAgain: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Search icon with subtle animation
        val infiniteTransition = rememberInfiniteTransition(label = "search_empty")
        val iconRotation by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "iconRotation"
        )

        Icon(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .scale(1f + iconRotation * 0.01f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "לא נמצאו תוצאות",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = "לא מצאנו מוצרים התואמים ל-\"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onSearchAgain != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            SecondaryButton(
                text = "חפש שוב",
                onClick = onSearchAgain,
                icon = Icons.Rounded.Search
            )
        }
    }
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
        modifier = modifier,
        showLogo = true // Show logo for brand reinforcement
    )
}

@Composable
fun NoConnectionState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isRetrying: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ChampionCart logo to maintain brand presence during network issues
        ChampionCartLogo(
            variant = LogoVariant.Icon,
            size = LogoSize.Medium,
            showBackground = false
        )

        Spacer(modifier = Modifier.height(Spacing.l))

        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = SemanticColors.Warning
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "אין חיבור לאינטרנט",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = "בדוק את החיבור שלך ונסה שוב",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        LoadingButton(
            isLoading = isRetrying,
            text = "נסה שוב",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

/**
 * New: Error state with branding
 */
@Composable
fun ErrorState(
    title: String = "משהו השתבש",
    message: String = "נסה שוב או פנה לתמיכה",
    actionText: String = "נסה שוב",
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo maintains brand presence even during errors
        ChampionCartLogo(
            variant = LogoVariant.Icon,
            size = LogoSize.Medium,
            showBackground = false
        )

        Spacer(modifier = Modifier.height(Spacing.l))

        Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = SemanticColors.Error
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

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