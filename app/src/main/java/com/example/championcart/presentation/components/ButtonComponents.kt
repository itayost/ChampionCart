package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlin.math.sin

/**
 * Custom Button Components
 * Theme-aware Electric Harmony styled buttons
 * Using ChampionCartAnimations from Animation.kt
 */

/**
 * Primary Glass Button - Theme-aware gradient
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = ComponentShapes.Button.Medium,
    size: ButtonSize = ButtonSize.Medium
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val darkTheme = isSystemInDarkTheme()
    val hazeState = LocalHazeState.current

    // Use ChampionCartAnimations
    val animatedScale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.94f
            isHovered -> 1.02f
            else -> 1f
        },
        animationSpec = if (!config.reduceMotion) {
            ChampionCartAnimations.Springs.ButtonPress
        } else {
            snap()
        },
        label = "buttonScale"
    )

    // Color animation using ChampionCartAnimations
    val animatedContainerColor by animateColorAsState(
        targetValue = when {
            !enabled -> ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.38f)
            isPressed -> ChampionCartColors.Brand.ElectricMintDark
            isHovered -> ChampionCartColors.Brand.ElectricMintLight
            else -> ChampionCartColors.Brand.ElectricMint
        },
        animationSpec = tween(
            durationMillis = ChampionCartAnimations.Durations.Quick,
            easing = ChampionCartAnimations.Easings.Standard
        ),
        label = "containerColor"
    )

    Box(modifier = modifier) {
        // Glow effect for light theme
        if (!config.reduceMotion && config.enableMicroAnimations && enabled && !darkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .blur(16.dp)
                    .clip(shape)
                    .background(
                        color = animatedContainerColor.copy(alpha = if (isPressed) 0.3f else 0.2f)
                    )
            )
        }

        Button(
            onClick = {
                if (config.enableHaptics) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
            modifier = Modifier
                .heightIn(min = size.height)
                .widthIn(min = size.minWidth)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    shadowElevation = when {
                        darkTheme && isPressed -> 12.dp.toPx()
                        darkTheme -> 8.dp.toPx()
                        isPressed -> 4.dp.toPx()
                        else -> 2.dp.toPx()
                    }
                },
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedContainerColor,
                contentColor = Color.White,
                disabledContainerColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.38f),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            interactionSource = interactionSource
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Spacing.s)
            ) {
                if (icon != null) {
                    Box(modifier = Modifier.size(size.iconSize)) {
                        icon()
                    }
                }
                Text(
                    text = text,
                    style = size.getTextStyle(),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Secondary Glass Button - Theme-aware glass effect
 */
@Composable
fun SecondaryGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = ComponentShapes.Button.Medium,
    size: ButtonSize = ButtonSize.Medium
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val darkTheme = isSystemInDarkTheme()
    val hazeState = LocalHazeState.current

    val animatedScale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.97f
            isHovered -> 1.01f
            else -> 1f
        },
        animationSpec = if (!config.reduceMotion) {
            ChampionCartAnimations.Springs.CardInteraction
        } else {
            snap()
        },
        label = "buttonScale"
    )

    // Animated border color
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            isPressed -> ChampionCartColors.Brand.ElectricMint
            isHovered -> ChampionCartColors.Brand.ElectricMintLight
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(
            durationMillis = ChampionCartAnimations.Durations.Quick,
            easing = ChampionCartAnimations.Easings.Standard
        ),
        label = "borderColor"
    )

    OutlinedButton(
        onClick = {
            if (config.enableHaptics) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier
            .heightIn(min = size.height)
            .widthIn(min = size.minWidth)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .modernGlass(
                intensity = when {
                    isPressed -> GlassIntensity.Medium
                    isHovered -> GlassIntensity.Light
                    else -> GlassIntensity.Light
                },
                shape = shape,
                hazeState = hazeState
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = animatedBorderColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.s)
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(size.iconSize)) {
                    icon()
                }
            }
            Text(
                text = text,
                style = size.getTextStyle(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Text Button with Glass Effect
 */
@Composable
fun GlassTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val darkTheme = isSystemInDarkTheme()

    TextButton(
        onClick = onClick,
        modifier = modifier
            .then(
                if (isPressed) {
                    Modifier.glass(
                        intensity = GlassIntensity.Light,
                        shape = ComponentShapes.Button.Square,
                        style = GlassStyle.Subtle,
                        darkTheme = darkTheme
                    )
                } else {
                    Modifier
                }
            ),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(Sizing.Icon.s)) {
                    icon()
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
 * Floating Action Button with Glass - Enhanced with theme-aware glow
 */
@Composable
fun GlassFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    extended: Boolean = false,
    text: String? = null,
    size: FABSize = FABSize.Medium
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val darkTheme = isSystemInDarkTheme()

    // Animated glow effect for dark theme only
    val infiniteTransition = if (!config.reduceMotion && config.enableMicroAnimations) {
        rememberInfiniteTransition(label = "fabGlow")
    } else null

    val glowAlpha by infiniteTransition?.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ChampionCartAnimations.Durations.Elaborate,
                easing = ChampionCartAnimations.Easings.Standard
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    ) ?: mutableStateOf(0.45f)

    Box(modifier = modifier) {
        // Glow effect behind FAB (dark theme only)
        if (!config.reduceMotion && darkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .blur(20.dp)
                    .clip(ComponentShapes.Special.FAB)
                    .background(
                        color = ChampionCartColors.Brand.ElectricMint.copy(alpha = glowAlpha * 0.3f)
                    )
            )
        }

        val fabElevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (darkTheme) 12.dp else 6.dp,
            pressedElevation = if (darkTheme) 16.dp else 8.dp,
            hoveredElevation = if (darkTheme) 14.dp else 7.dp
        )

        val animatedScale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = if (!config.reduceMotion) {
                ChampionCartAnimations.Springs.ButtonPress
            } else {
                snap()
            },
            label = "fabScale"
        )

        if (extended && text != null) {
            ExtendedFloatingActionButton(
                onClick = {
                    if (config.enableHaptics) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                },
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    },
                shape = ComponentShapes.Special.FAB,
                containerColor = ChampionCartColors.Brand.ElectricMint,
                contentColor = Color.White,
                elevation = fabElevation,
                interactionSource = interactionSource,
                icon = {
                    Box(modifier = Modifier.size(size.iconSize)) {
                        icon()
                    }
                },
                text = {
                    Text(
                        text = text,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        } else {
            FloatingActionButton(
                onClick = {
                    if (config.enableHaptics) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                },
                modifier = Modifier
                    .size(size.size)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    },
                shape = ComponentShapes.Special.FAB,
                containerColor = ChampionCartColors.Brand.ElectricMint,
                contentColor = Color.White,
                elevation = fabElevation,
                interactionSource = interactionSource
            ) {
                Box(modifier = Modifier.size(size.iconSize)) {
                    icon()
                }
            }
        }
    }
}

/**
 * Icon Button with Glass Background - Theme-aware
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    isActive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val darkTheme = isSystemInDarkTheme()

    IconButton(
        onClick = onClick,
        modifier = modifier
            .glass(
                intensity = when {
                    isPressed -> GlassIntensity.Medium
                    isActive -> GlassIntensity.Light
                    else -> GlassIntensity.Light
                },
                shape = ComponentShapes.Button.Square,
                style = if (darkTheme) GlassStyle.Subtle else GlassStyle.Default,
                darkTheme = darkTheme
            ),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    ) {
        icon()
    }
}

/**
 * Chip-style button with theme-aware glass effect
 */
@Composable
fun GlassChipButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val darkTheme = isSystemInDarkTheme()
    val config = ChampionCartTheme.config
    val haptics = LocalHapticFeedback.current

    val backgroundColor = when {
        selected && darkTheme -> ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.15f)
        selected && !darkTheme -> ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.08f)
        !selected && darkTheme -> Color.White.copy(alpha = 0.1f)
        else -> Color.Black.copy(alpha = 0.05f)
    }

    val contentColor = when {
        selected -> ChampionCartColors.Brand.ElectricMint
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = {
            if (config.enableHaptics) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier
            .height(32.dp)
            .glass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Button.Small,
                style = when {
                    selected && !darkTheme -> GlassStyle.Bordered
                    selected && darkTheme -> GlassStyle.Default
                    else -> GlassStyle.Subtle
                },
                darkTheme = darkTheme
            ),
        shape = ComponentShapes.Button.Small,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(16.dp)) {
                    icon()
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

/**
 * Button Sizes
 */
enum class ButtonSize(
    val height: Dp,
    val minWidth: Dp,
    val iconSize: Dp
) {
    Small(
        height = 32.dp,
        minWidth = 64.dp,
        iconSize = 16.dp
    ),
    Medium(
        height = 40.dp,
        minWidth = 96.dp,
        iconSize = 18.dp
    ),
    Large(
        height = 48.dp,
        minWidth = 128.dp,
        iconSize = 20.dp
    )
}

// Extension function to get text style within composable context
@Composable
fun ButtonSize.getTextStyle() = when (this) {
    ButtonSize.Small -> MaterialTheme.typography.labelMedium
    ButtonSize.Medium -> MaterialTheme.typography.labelLarge
    ButtonSize.Large -> MaterialTheme.typography.titleMedium
}

/**
 * FAB Sizes
 */
enum class FABSize(
    val size: Dp,
    val iconSize: Dp
) {
    Small(
        size = 40.dp,
        iconSize = 18.dp
    ),
    Medium(
        size = 56.dp,
        iconSize = 24.dp
    ),
    Large(
        size = 72.dp,
        iconSize = 28.dp
    )
}