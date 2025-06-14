package com.example.championcart.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

/**
 * Primary action button with press animation and haptic feedback
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed && enabled) Dimensions.elevationSmall else Dimensions.elevationMedium,
        animationSpec = tween(durationMillis = 150),
        label = "elevation"
    )

    Button(
        onClick = {
            if (!loading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = getButtonShape(size)
            )
            .height(getButtonHeight(size)),
        enabled = enabled && !loading,
        shape = getButtonShape(size),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn() with fadeOut()
            },
            label = "loading"
        ) { isLoading ->
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(getIconSize(size))
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    }
                    Text(
                        text = text,
                        style = when (size) {
                            ButtonSize.SMALL -> AppTextStyles.buttonText.copy(fontSize = 12.sp)
                            ButtonSize.MEDIUM -> AppTextStyles.buttonText
                            ButtonSize.LARGE -> AppTextStyles.buttonTextLarge
                        }
                    )
                }
            }
        }
    }
}

/**
 * Secondary button with outline
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .height(getButtonHeight(size)),
        enabled = enabled,
        shape = getButtonShape(size),
        border = BorderStroke(
            width = Dimensions.borderMedium,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(getIconSize(size))
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
            }
            Text(
                text = text,
                style = when (size) {
                    ButtonSize.SMALL -> AppTextStyles.buttonText.copy(fontSize = 12.sp)
                    ButtonSize.MEDIUM -> AppTextStyles.buttonText
                    ButtonSize.LARGE -> AppTextStyles.buttonTextLarge
                }
            )
        }
    }
}

/**
 * Icon button with animated press effect
 */
@Composable
fun AnimatedIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = Dimensions.iconSizeMedium
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isPressed && enabled) {
            tint.copy(alpha = 0.7f)
        } else {
            tint
        },
        animationSpec = tween(durationMillis = 300),
        label = "color"
    )

    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.scale(scale),
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = iconColor
        )
    }
}

/**
 * Floating action button with animated press and shadow
 */
@Composable
fun AnimatedFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String? = null,
    extended: Boolean = text != null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) Dimensions.elevationMedium else Dimensions.elevationLarge,
        animationSpec = tween(durationMillis = 150),
        label = "elevation"
    )

    if (extended && text != null) {
        ExtendedFloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier
                .scale(scale)
                .shadow(
                    elevation = elevation,
                    shape = ComponentShapes.Chip  // Rounded shape for FAB
                ),
            shape = ComponentShapes.Chip,
            containerColor = MaterialTheme.extendedColors.tertiary,
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
            Text(
                text = text,
                style = AppTextStyles.buttonText
            )
        }
    } else {
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier
                .scale(scale)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.extendedColors.tertiary,
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeMedium)
            )
        }
    }
}

/**
 * Chip/Tag button
 */
@Composable
fun ChipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "background"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "content"
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ComponentShapes.Chip,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingExtraSmall))
            }
            Text(
                text = text,
                style = AppTextStyles.chipText,
                color = contentColor
            )
        }
    }
}

/**
 * Button sizes enum
 */
enum class ButtonSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Add to cart button with quantity badge
 */
@Composable
fun AddToCartButton(
    onClick: () -> Unit,
    quantity: Int = 0,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    Box(modifier = modifier) {
        ActionButton(
            text = if (quantity > 0) "Add More" else "Add to Cart",
            onClick = onClick,
            size = size,
            icon = Icons.Default.AddShoppingCart
        )

        // Quantity badge
        if (quantity > 0) {
            Badge(
                modifier = Modifier.align(Alignment.TopEnd),
                containerColor = MaterialTheme.extendedColors.bestDeal,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = quantity.toString(),
                    style = AppTextStyles.badgeText
                )
            }
        }
    }
}

/**
 * Helper functions
 */
private fun getButtonHeight(size: ButtonSize): Dp = when (size) {
    ButtonSize.SMALL -> 36.dp
    ButtonSize.MEDIUM -> Dimensions.buttonHeight
    ButtonSize.LARGE -> 56.dp
}

private fun getButtonShape(size: ButtonSize) = when (size) {
    ButtonSize.SMALL -> RoundedCornerShape(6.dp)
    ButtonSize.MEDIUM -> ComponentShapes.Button
    ButtonSize.LARGE -> RoundedCornerShape(12.dp)
}

private fun getIconSize(size: ButtonSize): Dp = when (size) {
    ButtonSize.SMALL -> 16.dp
    ButtonSize.MEDIUM -> 20.dp
    ButtonSize.LARGE -> 24.dp
}