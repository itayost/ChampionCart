package com.example.championcart.presentation.components.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Feedback components for ChampionCart
 */

@Composable
fun ChampionBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandColors.NeonCoral,
    contentColor: Color = Color.White
) {
    AnimatedVisibility(
        visible = count > 0,
        modifier = modifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = TextStyles.badge,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChampionChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val backgroundColor = if (selected) {
        BrandColors.ElectricMint
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (selected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .clip(Shapes.chip)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.m,
                vertical = Spacing.s
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )

            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ChampionTooltip(
    text: String,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Surface(
            shape = Shapes.cardSmall,
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shadowElevation = 4.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(Padding.m),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ChampionProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = BrandColors.ElectricMint,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(Shapes.badge)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color)
        )
    }
}

@Composable
fun ChampionSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        action = snackbarData.visuals.actionLabel?.let {
            {
                TextButton(
                    onClick = { snackbarData.performAction() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = BrandColors.ElectricMint
                    )
                ) {
                    Text(it)
                }
            }
        },
        dismissAction = if (snackbarData.visuals.withDismissAction) {
            {
                IconButton(onClick = { snackbarData.dismiss() }) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "סגור"
                    )
                }
            }
        } else null,
        shape = Shapes.card,
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Text(snackbarData.visuals.message)
    }
}

@Composable
fun SuccessIndicator(
    show: Boolean,
    message: String = "נשמר בהצלחה!",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(show) {
        if (show) {
            delay(2000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = show,
        modifier = modifier,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Surface(
            shape = Shapes.card,
            color = SemanticColors.Success,
            contentColor = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(Padding.l),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(Size.icon)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PriceLevelIndicator(
    priceLevel: PriceLevel,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (color, label) = when (priceLevel) {
        PriceLevel.Best -> PriceColors.Best to "הזול ביותר"
        PriceLevel.Mid -> PriceColors.Mid to "מחיר סביר"
        PriceLevel.High -> PriceColors.High to "יקר"
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )

        if (showLabel) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    enabled: Boolean = false,
    onRatingChange: ((Float) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..maxRating) {
            val filled = i <= rating
            Icon(
                imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = "דירוג $i מתוך $maxRating",
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (enabled && onRatingChange != null) {
                            Modifier.clickable { onRatingChange(i.toFloat()) }
                        } else Modifier
                    ),
                tint = if (filled) {
                    SemanticColors.Warning
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            )
        }
    }
}

@Composable
fun InfoCard(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Info,
    action: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = SemanticColors.Info.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(Padding.m),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SemanticColors.Info,
                modifier = Modifier.size(Size.iconSmall)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                action?.invoke()
            }
        }
    }
}

@Composable
fun NotificationDot(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = BrandColors.NeonCoral
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
    }
}