package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Pull to Refresh wrapper component
 */
@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        content()

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.extended.electricMint,
            scale = true
        )
    }
}

/**
 * Skeleton loaders for different content types
 */
object SkeletonLoaders {

    @Composable
    fun ProductCardSkeleton(
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = ComponentShapes.Product.ProductCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpacingTokens.M)
            ) {
                // Image skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(SpacingTokens.S))

                // Title skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )

                Spacer(modifier = Modifier.height(SpacingTokens.XS))

                // Subtitle skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Price skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                )

                Spacer(modifier = Modifier.height(SpacingTokens.S))

                // Button skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = GlassmorphicShapes.Button
                )
            }
        }
    }

    @Composable
    fun ListItemSkeleton(
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Thumbnail skeleton
            SkeletonLoader(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                // Title skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )

                // Subtitle skeleton
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                )
            }

            // Action skeleton
            SkeletonLoader(
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

    @Composable
    fun TextSkeleton(
        lines: Int = 3,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            repeat(lines) { index ->
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth(if (index == lines - 1) 0.7f else 1f)
                        .height(16.dp)
                )
            }
        }
    }
}

/**
 * Custom Snackbar with Electric Harmony styling
 */
@Composable
fun ElectricSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val actionColor = when (snackbarData.visuals.duration) {
        SnackbarDuration.Short -> MaterialTheme.colorScheme.extended.electricMint
        SnackbarDuration.Long -> MaterialTheme.colorScheme.extended.cosmicPurple
        SnackbarDuration.Indefinite -> MaterialTheme.colorScheme.extended.neonCoral
    }

    Snackbar(
        modifier = modifier
            .padding(SpacingTokens.M)
            .glassmorphic(
                intensity = GlassIntensity.Medium,
                shape = RoundedCornerShape(12.dp)
            ),
        action = snackbarData.visuals.actionLabel?.let {
            {
                TextButton(
                    onClick = { snackbarData.performAction() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = actionColor
                    )
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        dismissAction = if (snackbarData.visuals.withDismissAction) {
            {
                IconButton(
                    onClick = { snackbarData.dismiss() }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss"
                    )
                }
            }
        } else null,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Text(snackbarData.visuals.message)
    }
}

/**
 * Animated visibility with scale and fade
 */
@Composable
fun AnimatedVisibilityFade(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(300)
        ) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(
            animationSpec = tween(200)
        ) + scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(200)
        ),
        content = content
    )
}

/**
 * Chip component with Electric Harmony styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200)
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            Color.White
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(200)
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = ComponentShapes.Search.FilterChip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = backgroundColor,
            labelColor = contentColor,
            iconColor = contentColor,
            selectedContainerColor = backgroundColor,
            selectedLabelColor = contentColor,
            selectedLeadingIconColor = contentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            selectedBorderColor = Color.Transparent,
            borderWidth = 1.dp,
            selectedBorderWidth = 0.dp
        )
    )
}

/**
 * Divider with gradient option
 */
@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    )
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(
                brush = Brush.horizontalGradient(colors)
            )
    )
}

/**
 * Section header component
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        action?.invoke()
    }
}

/**
 * Badge component
 */
@Composable
fun ElectricBadge(
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.extended.neonCoral
) {
    if (count > 0) {
        Surface(
            modifier = modifier,
            shape = GlassmorphicShapes.Badge,
            color = containerColor
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                modifier = Modifier.padding(
                    horizontal = SpacingTokens.S,
                    vertical = SpacingTokens.XXS
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}
