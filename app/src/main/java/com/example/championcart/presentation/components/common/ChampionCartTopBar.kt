@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.championcart.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.TextStyles
import java.time.LocalTime

/**
 * Data class for top bar action buttons
 */
data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val badge: String? = null,
    val tint: Color? = null
)

/**
 * Dynamic ChampionCart Top Bar Component
 *
 * @param title Main title text (e.g., "בית", "חיפוש", etc.)
 * @param subtitle Optional subtitle or greeting text (e.g., "ערב טוב")
 * @param navigationIcon Optional navigation icon (back button, menu, etc.)
 * @param actions List of action buttons to display on the right
 * @param showTimeBasedGradient Whether to show time-based gradient background
 * @param isTransparent Whether the top bar should be transparent
 * @param elevation Shadow elevation for the top bar
 * @param scrollBehavior Optional scroll behavior for collapsing/expanding
 * @param modifier Additional modifiers
 */
@Composable
fun ChampionCartTopBar(
    title: String? = null,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: List<TopBarAction> = emptyList(),
    showTimeBasedGradient: Boolean = false,
    isTransparent: Boolean = false,
    elevation: Dp = if (isTransparent) 0.dp else 2.dp,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { LocalTime.now().hour }
    val greeting = remember(currentHour) {
        when (currentHour) {
            in 6..11 -> "בוקר טוב"
            in 12..16 -> "צהריים טובים"
            in 17..20 -> "ערב טוב"
            else -> "לילה טוב"
        }
    }

    // Time-based gradient colors
    val gradientColors = remember(currentHour) {
        when (currentHour) {
            in 6..11 -> listOf(
                Color(0xFFFFE5B4).copy(alpha = 0.3f),
                Color(0xFFFFD700).copy(alpha = 0.1f)
            )
            in 12..16 -> listOf(
                BrandColors.ElectricMint.copy(alpha = 0.2f),
                BrandColors.ElectricMint.copy(alpha = 0.05f)
            )
            in 17..20 -> listOf(
                BrandColors.CosmicPurple.copy(alpha = 0.3f),
                BrandColors.NeonCoral.copy(alpha = 0.1f)
            )
            else -> listOf(
                BrandColors.DeepNavy.copy(alpha = 0.4f),
                BrandColors.DeepNavy.copy(alpha = 0.2f)
            )
        }
    }

    val backgroundColor = when {
        isTransparent -> Color.Transparent
        showTimeBasedGradient -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isTransparent -> MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (showTimeBasedGradient && !isTransparent) {
                    Modifier.background(
                        brush = Brush.verticalGradient(colors = gradientColors)
                    )
                } else Modifier
            )
    ) {
        TopAppBar(
            title = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Subtitle or greeting
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } ?: run {
                        if (title != null && showTimeBasedGradient) {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Main title
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                navigationIcon?.invoke()
            },
            actions = {
                actions.forEach { action ->
                    Box {
                        IconButton(
                            onClick = action.onClick
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.contentDescription,
                                tint = action.tint ?: contentColor,
                                modifier = Modifier.size(Size.icon)
                            )
                        }

                        // Badge
                        action.badge?.let { badgeText ->
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = 8.dp),
                                containerColor = BrandColors.NeonCoral,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = badgeText,
                                    style = TextStyles.badge
                                )
                            }
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                navigationIconContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .then(
                    if (!isTransparent) {
                        Modifier.shadow(
                            elevation = elevation,
                            spotColor = BrandColors.ElectricMint.copy(alpha = 0.1f)
                        )
                    } else Modifier
                )
        )
    }
}