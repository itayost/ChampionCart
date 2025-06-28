package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

/**
 * Modern Button Components
 * Electric Harmony Design System
 * Matching the HTML showcase with rounded, modern styling
 */

/**
 * Primary Electric Button with shimmer effect
 * Rounded pill shape with gradient background
 */
@Composable
fun ElectricButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config

    // Scale animation matching HTML (0.92 when pressed)
    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.92f
            else -> 1f
        },
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else snap(),
        label = "buttonScale"
    )

    // Shimmer animation
    val shimmerTransition = if (!config.reduceMotion && enabled && !loading) {
        rememberInfiniteTransition(label = "shimmer")
    } else null

    val shimmerOffset by shimmerTransition?.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    ) ?: mutableStateOf(0f)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ComponentShapes.Button.Large) // Using Large for pill shape (28dp)
            .background(
                brush = Brush.linearGradient(
                    colors = if (enabled) {
                        listOf(
                            ChampionCartColors.Brand.ElectricMint,
                            ChampionCartColors.Brand.ElectricMintLight.copy(alpha = 0.9f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                )
            )
            .then(
                if (shimmerTransition != null && enabled && !loading) {
                    Modifier.drawWithContent {
                        drawContent()
                        // Shimmer overlay
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0f)
                                ),
                                start = Offset(size.width * shimmerOffset, 0f),
                                end = Offset(size.width * (shimmerOffset + 0.5f), size.height)
                            )
                        )
                    }
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = {
                    if (config.enableHaptics) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                }
            )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = size.horizontalPadding,
                vertical = size.verticalPadding
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size.iconSize),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                icon?.let {
                    Box(modifier = Modifier.size(size.iconSize)) {
                        it()
                    }
                    Spacer(modifier = Modifier.width(SpacingTokens.S))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = size.fontSize,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Glass Button with glassmorphic effect
 * Secondary button style with transparency
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config
    val hazeState = LocalHazeState.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else snap(),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ComponentShapes.Button.Large)
            .then(
                if (hazeState != null) {
                    Modifier.modernGlass(
                        intensity = GlassIntensity.Medium,
                        shape = ComponentShapes.Button.Large,
                        hazeState = hazeState
                    )
                } else {
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            ),
                            shape = ComponentShapes.Button.Large
                        )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (config.enableHaptics) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 300f
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = size.horizontalPadding,
                        vertical = size.verticalPadding
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Box(modifier = Modifier.size(size.iconSize)) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.primary
                        ) {
                            it()
                        }
                    }
                    Spacer(modifier = Modifier.width(SpacingTokens.S))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = size.fontSize,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Glowing Icon Button with animated glow effect
 * Circular FAB-style button with pulsing glow
 */
@Composable
fun GlowingIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config

    // Glow animation
    val glowAnimation = if (!config.reduceMotion) {
        rememberInfiniteTransition(label = "glow")
    } else null

    val glowAlpha by glowAnimation?.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    ) ?: mutableStateOf(0.5f)

    Box(
        modifier = modifier
            .size(SizingTokens.ButtonHeightL) // 56.dp
            .drawBehind {
                // Glow effect
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha),
                            glowColor.copy(alpha = 0f)
                        ),
                        radius = size.minDimension / 1.5f
                    )
                )
            }
            .clip(CircleShape)
            .background(glowColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (config.enableHaptics) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(SizingTokens.IconM) // 24.dp
                .scale(if (isPressed) 0.85f else 1f)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides Color.White
            ) {
                icon()
            }
        }
    }
}

/**
 * Text Button with minimal glass effect
 * Used for less prominent actions
 */
@Composable
fun ElectricTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val config = ChampionCartTheme.config
    val hapticFeedback = LocalHapticFeedback.current

    TextButton(
        onClick = {
            if (config.enableHaptics) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier.then(
            if (isPressed) {
                Modifier.background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = ComponentShapes.Button.Medium
                )
            } else Modifier
        ),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(modifier = Modifier.size(SizingTokens.IconS)) {
                    it()
                }
            }
            Text(
                text = text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Loading Button variant
 * Shows loading state with disabled appearance
 */
@Composable
fun LoadingButton(
    text: String = "טוען...",
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium
) {
    ElectricButton(
        onClick = { },
        text = text,
        loading = true,
        enabled = false,
        modifier = modifier,
        size = size
    )
}

/**
 * Button Size configurations
 * Matches the HTML showcase sizing
 */
enum class ButtonSize(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val fontSize: androidx.compose.ui.unit.TextUnit,
    val iconSize: Dp
) {
    Small(
        horizontalPadding = SpacingTokens.L,      // 16.dp
        verticalPadding = SpacingTokens.S,       // 8.dp
        fontSize = 14.sp,
        iconSize = SizingTokens.IconXS            // 18.dp
    ),
    Medium(
        horizontalPadding = SpacingTokens.XXL,    // 24.dp
        verticalPadding = SpacingTokens.M,       // 12.dp
        fontSize = 16.sp,
        iconSize = SizingTokens.IconS            // 20.dp
    ),
    Large(
        horizontalPadding = 32.dp,                // Using direct value as XXL is 24.dp
        verticalPadding = SpacingTokens.L,       // 16.dp
        fontSize = 18.sp,
        iconSize = SizingTokens.IconM            // 24.dp
    )
}

/**
 * Chip Button for filters and selections
 * Compact rounded button with selection state
 */
@Composable
fun ElectricChipButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val config = ChampionCartTheme.config
    val hapticFeedback = LocalHapticFeedback.current

    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.08f)
        },
        animationSpec = tween(
            durationMillis = ChampionCartAnimations.Durations.Quick,
            easing = ChampionCartAnimations.Easings.Standard
        ),
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(
            durationMillis = ChampionCartAnimations.Durations.Quick,
            easing = ChampionCartAnimations.Easings.Standard
        ),
        label = "chipContent"
    )

    Surface(
        onClick = {
            if (config.enableHaptics) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier
            .height(SizingTokens.ButtonHeightS) // 32.dp
            .clip(ComponentShapes.Button.Small),
        shape = ComponentShapes.Button.Small,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(modifier = Modifier.size(SizingTokens.IconXS)) {
                    it()
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}