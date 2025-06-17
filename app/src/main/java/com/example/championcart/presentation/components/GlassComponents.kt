package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

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
    val colors = MaterialTheme.extendedColors

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

    // Hover animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 0.4f,
        animationSpec = tween(300),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .premiumGlass(
                glowColor = glowColor?.copy(alpha = glowAlpha),
                shape = shape
            )
            .let { mod ->
                if (onClick != null) {
                    mod.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                } else mod
            }
            .padding(20.dp),
        content = content
    )

    // Handle press state
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release,
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * Glass surface for headers and hero sections
 */
@Composable
fun GlassHeroSection(
    modifier: Modifier = Modifier,
    backgroundOrbs: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        // Background orbs
        if (backgroundOrbs) {
            CardBackgroundOrbs(
                modifier = Modifier.fillMaxSize(),
                primaryColor = MaterialTheme.extendedColors.electricMint
            )
        }

        // Glass surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .premiumGlass(
                    alpha = 0.12f,
                    borderAlpha = 0.25f,
                    glowColor = MaterialTheme.extendedColors.electricMintGlow,
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
    gradient: List<Color> = listOf(
        MaterialTheme.extendedColors.electricMint,
        MaterialTheme.extendedColors.cosmicPurple
    ),
    glowColor: Color = MaterialTheme.extendedColors.electricMintGlow,
    shape: Shape = RoundedCornerShape(28.dp),
    content: @Composable RowScope.() -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

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
                glowColor = glowColor.copy(alpha = glowAlpha),
                blurRadius = 16.dp
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = shape,
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradient),
                    shape = shape
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }

    // Handle press state
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release,
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * Enhanced glow effect
 */
fun Modifier.glowEffect(
    glowColor: Color,
    blurRadius: Dp = 12.dp
) = this.drawBehind {
    val glowRadiusPx = blurRadius.toPx()

    // Create multiple glow layers for better effect
    repeat(3) { i ->
        drawRect(
            color = glowColor.copy(alpha = 0.1f / (i + 1)),
            size = androidx.compose.ui.geometry.Size(
                size.width + glowRadiusPx * (i + 1),
                size.height + glowRadiusPx * (i + 1)
            ),
            topLeft = Offset(
                -glowRadiusPx * (i + 1) / 2,
                -glowRadiusPx * (i + 1) / 2
            )
        )
    }
}

/**
 * Glass search bar
 */
@Composable
fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .premiumGlass(
                alpha = 0.1f,
                borderAlpha = 0.2f,
                shape = RoundedCornerShape(28.dp)
            ),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.extendedColors.electricMint,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.extendedColors.electricMint,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = true
    )
}