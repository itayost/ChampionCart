package com.example.championcart.presentation.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing

/**
 * List components for ChampionCart
 */

@Composable
fun ChampionListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.l, vertical = Spacing.m),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(Size.icon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            trailingContent?.invoke()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.l, vertical = Spacing.s),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        action?.invoke()
    }
}

@Composable
fun ChampionDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
fun ListItemSkeleton(
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showSubtitle: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.l, vertical = Spacing.m),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            ShimmerEffect(
                modifier = Modifier
                    .size(Size.icon)
                    .clip(Shapes.cardSmall)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(Shapes.cardSmall)
            )
            if (showSubtitle) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(Shapes.cardSmall)
                )
            }
        }
    }
}