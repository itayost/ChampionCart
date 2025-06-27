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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.*

/**
 * Loading & Empty State Components
 * Skeleton screens, progress indicators, and empty states
 */

/**
 * Full Screen Loading
 */
@Composable
fun FullScreenLoading(
    message: String = "טוען...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChampionCartTheme.colors.surface.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        GlassLoadingCard(message = message)
    }
}

/**
 * Glass Loading Card
 */
@Composable
fun GlassLoadingCard(
    message: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    GlassCard(
        modifier = modifier.widthIn(max = 200.dp),
        shape = ComponentShapes.Card.Large,
        intensity = GlassIntensity.Heavy
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Component.paddingXL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            Box {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(rotation),
                    color = ChampionCartColors.Brand.ElectricMint,
                    strokeWidth = 3.dp
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(-rotation * 1.5f),
                    color = ChampionCartColors.Brand.CosmicPurple,
                    strokeWidth = 2.dp
                )
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Loading Dialog
 */
@Composable
fun LoadingDialog(
    isLoading: Boolean,
    message: String = "מעבד...",
    onDismissRequest: (() -> Unit)? = null
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = onDismissRequest ?: {},
            properties = DialogProperties(
                dismissOnBackPress = onDismissRequest != null,
                dismissOnClickOutside = onDismissRequest != null
            )
        ) {
            GlassLoadingCard(message = message)
        }
    }
}

/**
 * Skeleton List
 */
@Composable
fun SkeletonList(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        repeat(itemCount) { index ->
            SkeletonListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.m),
                delay = index * 100
            )
        }
    }
}

/**
 * Skeleton List Item
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")

    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                delayMillis = delay,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Card(
        modifier = modifier
            .height(80.dp)
            .shimmerGlass(shape = ComponentShapes.Card.Small),
        shape = ComponentShapes.Card.Small,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Component.paddingM),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(ComponentShapes.Product.Image)
                    .background(
                        ChampionCartTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )

            // Text skeletons
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(ComponentShapes.Special.Chip)
                        .background(
                            ChampionCartTheme.colors.onSurface.copy(alpha = 0.1f)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(ComponentShapes.Special.Chip)
                        .background(
                            ChampionCartTheme.colors.onSurface.copy(alpha = 0.08f)
                        )
                )
            }
        }
    }
}

/**
 * Empty State
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.Component.paddingXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Animated icon
        val infiniteTransition = rememberInfiniteTransition(label = "empty")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "iconScale"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .glass(
                    intensity = GlassIntensity.Light,
                    shape = ComponentShapes.Card.Hero
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.6f)
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = ChampionCartTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp)
        )

        // Action button
        if (action != null) {
            Spacer(modifier = Modifier.height(Spacing.m))
            action()
        }
    }
}

/**
 * Common empty states
 */
@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.ShoppingCart,
        title = "העגלה שלך ריקה",
        description = "הוסף מוצרים כדי להתחיל לחסוך",
        modifier = modifier,
        action = {
            GlassButton(
                onClick = onStartShopping,
                text = "התחל לקנות",
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null
                    )
                }
            )
        }
    )
}

@Composable
fun EmptySearchState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.SearchOff,
        title = "לא נמצאו תוצאות",
        description = "לא מצאנו מוצרים התואמים ל-\"$searchQuery\"",
        modifier = modifier
    )
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.ErrorOutline,
        title = "אופס! משהו השתבש",
        description = message,
        modifier = modifier,
        action = {
            GlassButton(
                onClick = onRetry,
                text = "נסה שוב",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                }
            )
        }
    )
}

/**
 * Pull to Refresh Indicator
 */
@Composable
fun GlassPullRefreshIndicator(
    refreshing: Boolean,
    state: androidx.compose.material.pullrefresh.PullRefreshState,
    modifier: Modifier = Modifier
) {
    androidx.compose.material.pullrefresh.PullRefreshIndicator(
        refreshing = refreshing,
        state = state,
        modifier = modifier
            .glass(
                intensity = GlassIntensity.Heavy,
                shape = ComponentShapes.Special.FAB
            ),
        backgroundColor = ChampionCartTheme.colors.surface,
        contentColor = ChampionCartColors.Brand.ElectricMint
    )
}

/**
 * Linear Progress Indicator with Glass
 */
@Composable
fun GlassLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(ComponentShapes.Special.Indicator)
            .glass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Special.Indicator
            )
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            color = ChampionCartColors.Brand.ElectricMint,
            trackColor = Color.Transparent
        )
    }
}