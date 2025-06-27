package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/**
 * Custom Button Components
 * Theme-aware Electric Harmony styled buttons
 */

/**
 * Accessibility-aware float animation
 */
@Composable
fun animateFloatWithAccessibility(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    label: String = "FloatAnimation",
    finishedListener: ((Float) -> Unit)? = null
): State<Float> {
    val reduceMotion = LocalReduceMotion.current
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = if (reduceMotion) snap() else animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

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
    val darkTheme = isSystemInDarkTheme()

    val animatedScale by animateFloatWithAccessibility(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = ChampionCartAnimations.Springs.responsive(),
        label = "buttonScale"
    )

    val gradientColors = if (darkTheme) {
        listOf(
            ChampionCartColors.Brand.ElectricMint,
            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.85f)
        )
    } else {
        listOf(
            ChampionCartColors.Brand.ElectricMint,
            ChampionCartColors.Brand.ElectricMintDark
        )
    }

    Button(
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
            },
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ChampionCartColors.Brand.ElectricMint,
            contentColor = Color.White,
            disabledContainerColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.38f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (darkTheme) 8.dp else 2.dp,
            pressedElevation = if (darkTheme) 12.dp else 4.dp,
            hoveredElevation = if (darkTheme) 10.dp else 3.dp,
            disabledElevation = 0.dp
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
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val darkTheme = isSystemInDarkTheme()

    val animatedScale by animateFloatWithAccessibility(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = ChampionCartAnimations.Springs.responsive(),
        label = "buttonScale"
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
            .buttonGlass(
                intensity = if (isPressed) GlassIntensity.Medium else GlassIntensity.Light,
                shape = shape,
                darkTheme = darkTheme
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = if (darkTheme) 0.dp else 1.dp,
            color = if (darkTheme) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
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
    val infiniteTransition = rememberInfiniteTransition(label = "fabGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
    ) {
        // Glow effect behind FAB (dark theme only)
        if (!config.reduceMotion && darkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
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
                        scaleX = if (isPressed) 0.95f else 1f
                        scaleY = if (isPressed) 0.95f else 1f
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
                        scaleX = if (isPressed) 0.95f else 1f
                        scaleY = if (isPressed) 0.95f else 1f
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
        onClick = onClick,
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