package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlin.math.roundToInt

/**
 * Champion Cart List Components
 * Reusable list items for consistent UI
 */

/**
 * Basic list item
 */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled = enabled) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                } else Modifier
            )
            .padding(
                horizontal = SpacingTokens.L,
                vertical = SpacingTokens.M
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Leading content
        leadingContent?.invoke()

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Trailing content
        trailingContent?.invoke()
    }
}

/**
 * List item with icon
 */
@Composable
fun IconListItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(SizingTokens.IconM)
            )
        },
        trailingContent = trailingContent,
        onClick = onClick
    )
}

/**
 * List item with avatar
 */
@Composable
fun AvatarListItem(
    avatarContent: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                avatarContent()
            }
        },
        trailingContent = trailingContent,
        onClick = onClick
    )
}

/**
 * Swipeable list item
 */
@Composable
fun SwipeableListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    onSwipeToDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    var offsetX by remember { mutableStateOf(0f) }
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Delete background
        if (onSwipeToDelete != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(end = SpacingTokens.L)
                )
            }
        }

        // Swipeable content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    if (onSwipeToDelete != null) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX < -100) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSwipeToDelete()
                                }
                                offsetX = 0f
                            }
                        ) { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                        }
                    }
                }
                .background(MaterialTheme.colorScheme.surface)
                .clickable(enabled = onClick != null) {
                    onClick?.invoke()
                }
                .padding(
                    horizontal = SpacingTokens.L,
                    vertical = SpacingTokens.M
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            leadingContent?.invoke()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Expandable list item
 */
@Composable
fun ExpandableListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    expandedContent: @Composable () -> Unit,
    initiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "chevron_rotation"
    )

    Column(modifier = modifier) {
        ListItem(
            title = title,
            subtitle = subtitle,
            leadingContent = leadingContent,
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotationAngle
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = { isExpanded = !isExpanded }
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SpacingTokens.XXL,
                        end = SpacingTokens.L,
                        bottom = SpacingTokens.M
                    )
            ) {
                expandedContent()
            }
        }
    }
}

/**
 * Checkbox list item
 */
@Composable
fun CheckboxListItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onClick = if (enabled) {
            { onCheckedChange(!checked) }
        } else null,
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.extended.electricMint
                )
            )
        }
    )
}

/**
 * Radio button list item
 */
@Composable
fun RadioButtonListItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onClick = if (enabled) onClick else null,
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.extended.electricMint
                )
            )
        }
    )
}

/**
 * Switch list item
 */
@Composable
fun SwitchListItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        leadingContent = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(SizingTokens.IconM)
                )
            }
        },
        onClick = if (enabled) {
            { onCheckedChange(!checked) }
        } else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.extended.electricMint,
                    checkedTrackColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.5f)
                )
            )
        }
    )
}

/**
 * Action list item with button
 */
@Composable
fun ActionListItem(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    actionEnabled: Boolean = true
) {
    ListItem(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        leadingContent = leadingContent,
        trailingContent = {
            TextButton(
                onClick = onActionClick,
                enabled = actionEnabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.extended.electricMint
                )
            ) {
                Text(actionLabel)
            }
        }
    )
}