package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Primary CTA Button with gradient background
 * Used for main actions like "Add to Cart", "Checkout", etc.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.extended.electricMint,
        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.8f)
    )
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.96f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        animationSpec = tween(200)
    )

    Button(
        onClick = {
            if (!loading) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(SizingTokens.ButtonXL)
            .scale(scale),
        enabled = enabled && !loading,
        shape = GlassmorphicShapes.Button,
        contentPadding = PaddingValues(horizontal = SpacingTokens.XL),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = buttonAlpha) }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = loading,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        icon?.let {
                            it()
                            Spacer(modifier = Modifier.width(SpacingTokens.S))
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Secondary Button with outline style
 * Used for secondary actions
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    borderColor: Color = MaterialTheme.colorScheme.extended.electricMint
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed -> 0.96f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    )

    OutlinedButton(
        onClick = {
            if (!loading) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(SizingTokens.ButtonXL)
            .scale(scale),
        enabled = enabled && !loading,
        shape = GlassmorphicShapes.Button,
        border = BorderStroke(
            width = 2.dp,
            color = borderColor.copy(alpha = if (enabled) 1f else 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = SpacingTokens.XL),
        interactionSource = interactionSource,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = borderColor
        )
    ) {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn() with fadeOut()
            }
        ) { isLoading ->
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = borderColor,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        it()
                        Spacer(modifier = Modifier.width(SpacingTokens.S))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Small Icon Button with glassmorphic effect
 */
@Composable
fun GlassmorphicIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = SizingTokens.IconButtonM,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioMediumBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    )

    IconButton(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .size(size)
            .scale(scale)
            .glassmorphic(
                intensity = GlassIntensity.Light,
                shape = CircleShape
            ),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Floating Action Button with gradient
 */
@Composable
fun GradientFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String? = null,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.extended.electricMint,
        MaterialTheme.colorScheme.extended.cosmicPurple
    )
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioHighBounce,
            stiffness = SpringSpecs.StiffnessMedium
        )
    )

    if (text != null) {
        ExtendedFloatingActionButton(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = ComponentShapes.Navigation.FAB
                ),
            shape = ComponentShapes.Navigation.FAB,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    icon()
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    } else {
        FloatingActionButton(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = ComponentShapes.Navigation.FAB
                ),
            shape = ComponentShapes.Navigation.FAB,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}

/**
 * Text Button for inline actions
 */
@Composable
fun GlassmorphicTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.extended.electricMint
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}