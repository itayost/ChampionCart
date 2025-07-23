package com.example.championcart.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.branding.ChampionCartLogo
import com.example.championcart.presentation.components.branding.LogoSize
import com.example.championcart.presentation.components.branding.LogoVariant
import com.example.championcart.ui.theme.*

/**
 * Loading indicators for ChampionCart with enhanced branding
 */

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = BrandColors.ElectricMint,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun LoadingScreen(
    message: String? = null,
    modifier: Modifier = Modifier,
    showLogo: Boolean = true
) {
    // Animation for logo breathing effect
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showLogo) {
                // ChampionCart logo with breathing animation
                ChampionCartLogo(
                    variant = LogoVariant.Icon,
                    size = LogoSize.Large,
                    modifier = Modifier.scale(logoScale)
                )

                Spacer(modifier = Modifier.height(Spacing.xl))
            }

            // Loading indicator positioned relative to logo
            LoadingIndicator(
                size = if (showLogo) 32.dp else 48.dp
            )

            message?.let {
                Spacer(modifier = Modifier.height(Spacing.l))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LoadingButton(
    isLoading: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Size.buttonHeight),
        enabled = enabled && !isLoading,
        shape = Shapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandColors.ElectricMint
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Box(
        modifier = modifier.glass(
            shape = Shapes.card,
            elevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = alpha.value
                    )
                )
        )
    }
}

/**
 * Enhanced loading state for search results or product loading
 */
@Composable
fun BrandedLoadingCard(
    message: String = "מחפש את המחירים הטובים ביותר",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "branded_loading")

    // Pulsing animation for the logo
    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mini logo with pulse animation
            ChampionCartLogo(
                variant = LogoVariant.Icon,
                size = LogoSize.Medium,
                modifier = Modifier.scale(logoPulse)
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            LoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
        }
    }
}

/**
 * Minimal loading indicator for small spaces
 */
@Composable
fun MiniLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = BrandColors.ElectricMint,
        strokeWidth = 2.dp,
        strokeCap = StrokeCap.Round
    )
}

/**
 * Loading state for empty screens with branding
 */
@Composable
fun EmptyStateLoading(
    title: String = "טוען",
    subtitle: String = "מכין עבורך את הטוב ביותר",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_loading")

    val logoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logoRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large logo with subtle rotation
        Box(
            modifier = Modifier.size(LogoSize.XLarge.iconSize),
            contentAlignment = Alignment.Center
        ) {
            ChampionCartLogo(
                variant = LogoVariant.Icon,
                size = LogoSize.XLarge
            )

            // Rotating loading ring around logo
            CircularProgressIndicator(
                modifier = Modifier
                    .size(LogoSize.XLarge.iconSize + 16.dp),
                color = BrandColors.ElectricMint.copy(alpha = 0.3f),
                strokeWidth = 2.dp,
                strokeCap = StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.s))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}