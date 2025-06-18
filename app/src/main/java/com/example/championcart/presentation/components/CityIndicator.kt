package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

/**
 * Modern City Indicator with Electric Harmony design
 * Glassmorphic chip with animated interactions
 */
@Composable
fun CityIndicator(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showStoreCount: Int? = null // Optional store count
) {
    val haptics = LocalHapticFeedback.current
    val colors = LocalExtendedColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "city_indicator_scale"
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.2f else 0.1f,
        animationSpec = tween(200),
        label = "city_indicator_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp)) // Pill shape for modern look
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.electricMint.copy(alpha = backgroundAlpha),
                        colors.cosmicPurple.copy(alpha = backgroundAlpha * 0.7f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Animated location icon
            AnimatedLocationIcon(
                tint = colors.electricMint,
                isPressed = isPressed
            )

            Column {
                Text(
                    text = city,
                    style = if (isHebrewText(city)) {
                        MaterialTheme.typography.labelLarge.copy(
                            fontFamily = HeeboFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        MaterialTheme.typography.labelLarge
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Optional store count
                showStoreCount?.let { count ->
                    Text(
                        text = "$count חנויות",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Dropdown arrow with rotation animation
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change city",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationZ = if (isPressed) 180f else 0f
                    },
                tint = colors.electricMint
            )
        }
    }
}

@Composable
private fun AnimatedLocationIcon(
    tint: Color,
    isPressed: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "location_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )

    Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = null,
        modifier = Modifier
            .size(18.dp)
            .scale(if (isPressed) 0.9f else pulseScale),
        tint = tint
    )
}

// Alternative modern design with glassmorphic effect
@Composable
fun GlassCityIndicator(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    storeCount: Int? = null
) {
    val colors = LocalExtendedColors.current
    val haptics = LocalHapticFeedback.current

    Card(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier
            .glassmorphic(
                intensity = GlassIntensity.Light,
                shape = GlassmorphicShapes.Chip
            ),
        shape = GlassmorphicShapes.Chip,
        colors = CardDefaults.cardColors(
            containerColor = colors.glassLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Glowing location icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        colors.electricMint.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = colors.electricMint
                )
            }

            Column {
                Text(
                    text = city,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                storeCount?.let { count ->
                    Text(
                        text = "$count stores nearby",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change location",
                modifier = Modifier.size(20.dp),
                tint = colors.textSecondary
            )
        }
    }
}

// Compact version for app bars
@Composable
fun CompactCityIndicator(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current

    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = colors.electricMint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = city,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Helper function to detect Hebrew text
private fun isHebrewText(text: String): Boolean {
    return text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }
}

// Preview
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun CityIndicatorPreview() {
    ChampionCartTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CityIndicator(
                city = "תל אביב",
                onClick = {},
                showStoreCount = 24
            )

            GlassCityIndicator(
                city = "Jerusalem",
                onClick = {},
                storeCount = 18
            )

            CompactCityIndicator(
                city = "חיפה",
                onClick = {}
            )
        }
    }
}