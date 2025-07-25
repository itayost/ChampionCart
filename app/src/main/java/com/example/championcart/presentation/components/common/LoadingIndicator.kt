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