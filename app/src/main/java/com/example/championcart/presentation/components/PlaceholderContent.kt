package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

@Composable
fun PlaceholderContent(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "placeholder")
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconRotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ChampionCartTheme.colors.background,
                        ChampionCartTheme.colors.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        // Background orbs
        FloatingOrb(
            color = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.2f),
            size = 100.dp,
            offsetX = -50.dp,
            offsetY = 50.dp
        )

        FloatingOrb(
            color = ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.2f),
            size = 80.dp,
            offsetX = 150.dp,
            offsetY = -30.dp,
            delay = 1000
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            rotationZ = iconRotation
                        }
                        .glass(
                            intensity = GlassIntensity.Medium,
                            shape = ComponentShapes.Special.Chip
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.l))
            }

            // Title
            Text(
                text = title,
                style = CustomTextStyles.storeName,
                color = ChampionCartTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )

            // Subtitle
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(Spacing.s))
                Text(
                    text = subtitle,
                    style = CustomTextStyles.price,
                    color = ChampionCartTheme.colors.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Content
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                intensity = GlassIntensity.Light
            ) {
                content()
            }
        }
    }
}