package com.example.championcart.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.LocalExtendedColors

/**
 * Champion Cart - Premium Glass Components
 * Implementing your glassmorphism design system perfectly
 */

/**
 * Enhanced glass effect with proper blur simulation and glow
 */
fun Modifier.premiumGlass(
    alpha: Float = 0.08f,
    borderAlpha: Float = 0.18f,
    blurRadius: Dp = 20.dp,
    glowColor: Color? = null,
    shape: Shape = RoundedCornerShape(24.dp)
) = composed {
    val colors = LocalExtendedColors.current // FIXED: Using LocalExtendedColors.current

    this
        .clip(shape)
        .background(
            color = Color.White.copy(alpha = alpha),
            shape = shape
        )
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = borderAlpha),
            shape = shape
        )
        .let { modifier ->
            if (glowColor != null) {
                modifier.glowEffect(glowColor = glowColor, blurRadius = blurRadius)
            } else {
                modifier
            }
        }
        .shadow(
            elevation = 8.dp,
            shape = shape,
            spotColor = Color.Black.copy(alpha = 0.08f)
        )
}

/**
 * Interactive glass card with hover effects
 */
@Composable
fun GlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .premiumGlass(
                shape = shape,
                glowColor = glowColor
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                } else Modifier
            ),
        content = content
    )
}

/**
 * Hero glass card with animated background orbs
 */
@Composable
fun HeroGlassCard(
    modifier: Modifier = Modifier,
    backgroundOrbs: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalExtendedColors.current // FIXED

    Box(
        modifier = modifier
    ) {
        // Background orbs
        if (backgroundOrbs) {
            CardBackgroundOrbs(
                modifier = Modifier.fillMaxSize(),
                primaryColor = colors.electricMint
            )
        }

        // Glass surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .premiumGlass(
                    alpha = 0.12f,
                    borderAlpha = 0.25f,
                    glowColor = colors.electricMintGlow,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp),
            content = content
        )
    }
}

/**
 * Glass button with gradient and glow effects
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color>? = null,
    glowColor: Color? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    content: @Composable RowScope.() -> Unit
) {
    val colors = LocalExtendedColors.current // FIXED
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Default gradient if not provided
    val defaultGradient = listOf(
        colors.electricMint,
        colors.cosmicPurple
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0.6f,
        animationSpec = tween(200),
        label = "button_glow"
    )

    Button(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .glowEffect(
                glowColor = glowColor ?: colors.electricMintGlow, // FIXED
                blurRadius = 16.dp,
                alpha = glowAlpha
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = shape,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradient ?: defaultGradient // FIXED
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                }
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * Floating glass chip for selections
 */
@Composable
fun GlassChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit
) {
    val colors = LocalExtendedColors.current // FIXED

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            colors.electricMint.copy(alpha = 0.2f)
        } else {
            colors.glassLight
        },
        animationSpec = tween(300),
        label = "chip_background"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            colors.electricMint
        } else {
            colors.borderGlass
        },
        animationSpec = tween(300),
        label = "chip_border"
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            contentAlignment = Alignment.Center
        ) {
            label()
        }
    }
}

/**
 * Glass loading indicator
 */
@Composable
fun GlassLoadingCard(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current // FIXED

    GlassCard(
        modifier = modifier,
        glowColor = colors.electricMintGlow // FIXED
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = colors.electricMint, // FIXED
                strokeWidth = 3.dp
            )

            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Glow effect modifier helper
 */
fun Modifier.glowEffect(
    glowColor: Color,
    blurRadius: Dp = 16.dp,
    alpha: Float = 1f
) = composed {
    this.drawWithCache {
        onDrawBehind {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alpha * 0.3f),
                        glowColor.copy(alpha = 0f)
                    ),
                    radius = size.minDimension / 2 + blurRadius.toPx()
                ),
                radius = size.minDimension / 2 + blurRadius.toPx()
            )
        }
    }
}