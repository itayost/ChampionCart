package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Empty state component with icon, title, description and optional action
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (() -> Unit)? = null,
    actionLabel: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 2 },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Animated icon
            AnimatedIcon(
                icon = icon,
                tint = iconTint,
                modifier = Modifier.size(Dimensions.iconSizeExtraLarge * 2) // 96.dp
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Action button
            if (action != null && actionLabel != null) {
                Spacer(modifier = Modifier.height(Dimensions.spacingLarge))
                Button(
                    onClick = action,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    shape = ComponentShapes.Button
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

/**
 * Animated icon with subtle bounce effect
 */
@Composable
private fun AnimatedIcon(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.scale(scale),
        tint = tint
    )
}

/**
 * Common empty states for the app
 */
object EmptyStates {
    @Composable
    fun NoSearchResults(
        searchQuery: String,
        onClearSearch: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            icon = Icons.Default.SearchOff,
            title = "No products found",
            description = "We couldn't find any products matching \"$searchQuery\"",
            action = onClearSearch,
            actionLabel = "Clear search",
            modifier = modifier
        )
    }

    @Composable
    fun EmptyCart(
        onStartShopping: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            icon = Icons.Default.ShoppingCart,
            title = "Your cart is empty",
            description = "Add products to your cart to find the best prices",
            action = onStartShopping,
            actionLabel = "Start shopping",
            iconTint = MaterialTheme.extendedColors.savings.copy(alpha = 0.3f),
            modifier = modifier
        )
    }

    @Composable
    fun NoSavedCarts(
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            icon = Icons.Default.BookmarkBorder,
            title = "No saved carts",
            description = "Save your shopping lists to access them anytime",
            modifier = modifier
        )
    }

    @Composable
    fun NetworkError(
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            icon = Icons.Default.WifiOff,
            title = "No connection",
            description = "Check your internet connection and try again",
            action = onRetry,
            actionLabel = "Retry",
            iconTint = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
            modifier = modifier
        )
    }
}

/**
 * Shimmer effect for loading states
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    return this.then(
        background(
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ),
                start = Offset(translateAnim - 200f, 0f),
                end = Offset(translateAnim, 0f)
            )
        )
    )
}

/**
 * Product card skeleton for loading state
 */
@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.cardPadding)
        ) {
            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(ComponentShapes.DialogSmall)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Price skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(28.dp)
                        .clip(ComponentShapes.Badge)
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(Dimensions.buttonHeight)
                        .clip(ComponentShapes.Button)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Store rows skeleton
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(ComponentShapes.Card)
                        .shimmerEffect()
                )
                if (it < 1) {
                    Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                }
            }
        }
    }
}

/**
 * Loading state for lists
 */
@Composable
fun LoadingList(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        repeat(itemCount) {
            ProductCardSkeleton()
        }
    }
}

/**
 * Circular loading indicator with text
 */
@Composable
fun LoadingIndicator(
    text: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Full screen loading state
 */
@Composable
fun FullScreenLoading(
    text: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(text = text)
    }
}

/**
 * Pull to refresh indicator
 */
@Composable
fun RefreshingIndicator(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isRefreshing,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = Dimensions.elevationSmall
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingMedium),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Text(
                    text = "Updating prices...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}