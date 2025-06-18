package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons

/**
 * Glass Card component for lists
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = GlassmorphicShapes.GlassCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    } else {
        Card(
            modifier = modifier,
            shape = GlassmorphicShapes.GlassCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    }
}

/**
 * Glass Outlined Card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassOutlinedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = GlassmorphicShapes.GlassCard,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        content()
    }
}

/**
 * Glass Selectable Card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            }
        ),
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.extended.electricMint)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 4.dp else 2.dp
        )
    ) {
        content()
    }
}

/**
 * Glass Info Card
 */
@Composable
fun GlassInfoCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(SpacingTokens.M),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Glass Badge component
 */
enum class BadgeSize { SMALL, MEDIUM, LARGE }

@Composable
fun GlassBadge(
    count: Int? = null,
    text: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    size: BadgeSize = BadgeSize.MEDIUM,
    modifier: Modifier = Modifier
) {
    val displayText = count?.toString() ?: text ?: ""
    val (badgeSize, textSize) = when (size) {
        BadgeSize.SMALL -> 20.dp to 10.sp
        BadgeSize.MEDIUM -> 24.dp to 12.sp
        BadgeSize.LARGE -> 40.dp to 16.sp
    }

    Box(
        modifier = modifier
            .size(width = if (displayText.length > 2) badgeSize * 1.5f else badgeSize, height = badgeSize)
            .clip(RoundedCornerShape(badgeSize / 2))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = textSize),
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Animated Price Counter
 */
@Composable
fun AnimatedPriceCounter(
    targetValue: Double,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    modifier: Modifier = Modifier
) {
    var oldValue by remember { mutableStateOf(targetValue) }

    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "price_animation"
    )

    LaunchedEffect(targetValue) {
        oldValue = targetValue
    }

    Text(
        text = "₪${String.format("%.2f", animatedValue)}",
        style = style,
        modifier = modifier
    )
}

/**
 * Glass Chip component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(content = content)  // Wrap the RowScope content in a Row
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = GlassmorphicShapes.Chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            selectedContainerColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.2f),
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.extended.electricMint
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            selectedBorderColor = MaterialTheme.colorScheme.extended.electricMint
        )
    )
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color>,
    glowColor: Color,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = GlassmorphicShapes.Button,
                spotColor = glowColor.copy(alpha = 0.3f),
                ambientColor = glowColor.copy(alpha = 0.3f)
            ),
        shape = GlassmorphicShapes.Button,
        color = Color.Transparent,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors = gradient),
                    shape = GlassmorphicShapes.Button
                )
                .padding(horizontal = SpacingTokens.XL, vertical = SpacingTokens.M),
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
 * Glassmorphic Icon Button
 */
@Composable
fun GlassmorphicIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color? = null,
    glassIntensity: GlassIntensity = GlassIntensity.Light
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = backgroundColor ?: Color.Transparent
    ) {
        Box(
            modifier = if (backgroundColor == null) {
                Modifier.glassmorphic(
                    intensity = glassIntensity,
                    shape = CircleShape
                )
            } else {
                Modifier
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

/**
 * Glassmorphic Bottom Sheet component
 * A modal bottom sheet with glass effect for Champion Cart
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    skipPartiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            // Custom glass-style drag handle
            Box(
                modifier = Modifier
                    .padding(vertical = SpacingTokens.M)
                    .size(width = 48.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
            )
        },
        shape = GlassmorphicShapes.BottomSheet
    ) {
        // Content directly without extra Box wrapper to avoid Composable context issues
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpacingTokens.XL), // Extra padding for gesture area
            content = content
        )
    }
}

/**
 * Premium Button component using GlassButton
 */
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val gradient = listOf(
        MaterialTheme.colorScheme.extended.electricMint,
        MaterialTheme.colorScheme.extended.bestPrice
    )

    GlassButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        gradient = gradient,
        glowColor = MaterialTheme.colorScheme.extended.electricMint
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Box(modifier = Modifier) {
                            icon()
                        }
                        Spacer(modifier = Modifier.width(SpacingTokens.S))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}