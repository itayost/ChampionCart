package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Electric Harmony styled buttons with glassmorphic effects
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
 * Primary Glass Button
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

    val animatedScale by animateFloatWithAccessibility(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = ChampionCartAnimations.Springs.responsive(),
        label = "buttonScale"
    )

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
            }
            .glass(
                intensity = if (isPressed) GlassIntensity.Heavy else GlassIntensity.Medium,
                shape = shape
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ChampionCartColors.Brand.ElectricMint,
            contentColor = Color.White,
            disabledContainerColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.38f),
            disabledContentColor = Color.White.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.Component.button,
            pressedElevation = Elevation.Component.buttonPressed,
            hoveredElevation = Elevation.Component.buttonHover,
            disabledElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
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
 * Secondary Glass Button
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

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = size.height)
            .widthIn(min = size.minWidth)
            .glass(
                intensity = if (isPressed) GlassIntensity.Medium else GlassIntensity.Light,
                shape = shape
            ),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ChampionCartTheme.colors.primary,
            disabledContentColor = ChampionCartTheme.colors.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                ChampionCartTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                ChampionCartTheme.colors.onSurface.copy(alpha = 0.12f)
            }
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
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
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = ChampionCartTheme.colors.primary,
            disabledContentColor = ChampionCartTheme.colors.onSurface.copy(alpha = 0.38f)
        )
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
 * Floating Action Button with Glass
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

    if (extended && text != null) {
        ExtendedFloatingActionButton(
            onClick = {
                if (config.enableHaptics) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
            modifier = modifier
                .animatedGlass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Special.FAB
                ),
            shape = ComponentShapes.Special.FAB,
            containerColor = ChampionCartColors.Brand.ElectricMint,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = Elevation.Component.fab,
                pressedElevation = Elevation.Component.fabPressed,
                hoveredElevation = Elevation.Component.fabHover
            ),
            icon = {
                Box(modifier = Modifier.size(size.iconSize)) {
                    icon()
                }
            },
            text = {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium
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
            modifier = modifier
                .size(size.size)
                .animatedGlass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Special.FAB
                ),
            shape = ComponentShapes.Special.FAB,
            containerColor = ChampionCartColors.Brand.ElectricMint,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = Elevation.Component.fab,
                pressedElevation = Elevation.Component.fabPressed,
                hoveredElevation = Elevation.Component.fabHover
            )
        ) {
            Box(modifier = Modifier.size(size.iconSize)) {
                icon()
            }
        }
    }
}

/**
 * Icon Button with Glass Background
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors()
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .glass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Button.Square
            ),
        enabled = enabled,
        colors = colors
    ) {
        icon()
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
        height = Sizing.Button.heightS,
        minWidth = Sizing.Button.minWidthS,
        iconSize = Sizing.Icon.s
    ),
    Medium(
        height = Sizing.Button.heightL,
        minWidth = Sizing.Button.minWidthM,
        iconSize = Sizing.Icon.m
    ),
    Large(
        height = Sizing.Button.heightXL,
        minWidth = Sizing.Button.minWidthL,
        iconSize = Sizing.Icon.l
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
        size = Sizing.FAB.small,
        iconSize = Sizing.Icon.s
    ),
    Medium(
        size = Sizing.FAB.medium,
        iconSize = Sizing.Icon.m
    ),
    Large(
        size = Sizing.FAB.large,
        iconSize = Sizing.Icon.l
    )
}