@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.championcart.presentation.components.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
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

/**
 * Specialized top bar for screens with search functionality
 */
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    placeholder: String = "חפש מוצרים...",
    actions: List<TopBarAction> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                placeholder = placeholder,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "חזור",
                    modifier = Modifier.size(Size.icon)
                )
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        tint = action.tint ?: MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Size.icon)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

/**
 * Glass effect top bar variant
 */
@Composable
fun GlassTopBar(
    title: String? = null,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: List<TopBarAction> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glass(
                shape = Shapes.bottomSheet,
                elevation = 4.dp
            )
    ) {
        ChampionCartTopBar(
            title = title,
            subtitle = subtitle,
            navigationIcon = navigationIcon,
            actions = actions,
            isTransparent = true,
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Animated collapsing top bar
 */
@Composable
fun CollapsingTopBar(
    title: String,
    expandedTitle: String = title,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: List<TopBarAction> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val collapsed by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction > 0.5f
        }
    }

    val titleAlpha by animateFloatAsState(
        targetValue = if (collapsed) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "titleAlpha"
    )

    Box(modifier = modifier) {
        // Expanded header
        AnimatedVisibility(
            visible = !collapsed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(Padding.l),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = expandedTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Collapsing app bar
        TopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier.alpha(titleAlpha),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                navigationIcon?.invoke()
            },
            actions = {
                actions.forEach { action ->
                    IconButton(onClick = action.onClick) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                            tint = action.tint ?: MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(Size.icon)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Simple back button for navigation
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "חזור",
            tint = tint,
            modifier = Modifier.size(Size.icon)
        )
    }
}

// ============= PREVIEW SECTION =============

@Preview(name = "Basic Top Bar", group = "Top Bar")
@Composable
private fun ChampionCartTopBarPreview() {
    ChampionCartTheme {
        ChampionCartTopBar(
            title = "בית",
            subtitle = "ברוך הבא",
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Menu, contentDescription = "תפריט")
                }
            }
        )
    }
}

@Preview(name = "Top Bar with Actions", group = "Top Bar")
@Composable
private fun ChampionCartTopBarWithActionsPreview() {
    ChampionCartTheme {
        ChampionCartTopBar(
            title = "העגלה שלי",
            subtitle = "15 פריטים",
            navigationIcon = {
                BackButton(onClick = {})
            },
            actions = listOf(
                TopBarAction(
                    icon = Icons.Rounded.Notifications,
                    contentDescription = "התראות",
                    onClick = {},
                    badge = "3"
                ),
                TopBarAction(
                    icon = Icons.Rounded.ShoppingCart,
                    contentDescription = "עגלה",
                    onClick = {},
                    badge = "5",
                    tint = BrandColors.ElectricMint
                )
            )
        )
    }
}

@Preview(name = "Time-Based Gradient", group = "Top Bar")
@Composable
private fun ChampionCartTopBarGradientPreview() {
    ChampionCartTheme {
        ChampionCartTopBar(
            title = "בית",
            showTimeBasedGradient = true,
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Menu, contentDescription = "תפריט")
                }
            },
            actions = listOf(
                TopBarAction(
                    icon = Icons.Rounded.Search,
                    contentDescription = "חיפוש",
                    onClick = {}
                )
            )
        )
    }
}

@Preview(name = "Transparent Top Bar", group = "Top Bar")
@Composable
private fun ChampionCartTopBarTransparentPreview() {
    ChampionCartTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BrandColors.ElectricMint,
                            BrandColors.CosmicPurple
                        )
                    )
                )
        ) {
            ChampionCartTopBar(
                title = "מוצרים",
                isTransparent = true,
                navigationIcon = {
                    BackButton(onClick = {}, tint = Color.White)
                }
            )
        }
    }
}

@Preview(name = "Search Top Bar", group = "Top Bar")
@Composable
private fun SearchTopBarPreview() {
    ChampionCartTheme {
        var searchQuery by remember { mutableStateOf("") }
        SearchTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearch = {},
            onBack = {},
            placeholder = "חפש מוצרים, מותגים או ברקודים...",
            actions = listOf(
                TopBarAction(
                    icon = Icons.Rounded.FilterList,
                    contentDescription = "סינון",
                    onClick = {}
                )
            )
        )
    }
}

@Preview(name = "Glass Top Bar", group = "Top Bar")
@Composable
private fun GlassTopBarPreview() {
    ChampionCartTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray)
        ) {
            GlassTopBar(
                title = "הגדרות",
                subtitle = "נהל את החשבון שלך",
                navigationIcon = {
                    BackButton(onClick = {})
                }
            )
        }
    }
}

@Preview(name = "Dark Theme Top Bar", group = "Top Bar", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChampionCartTopBarDarkPreview() {
    ChampionCartTheme(darkTheme = true) {
        ChampionCartTopBar(
            title = "פרופיל",
            subtitle = "עריכת פרטים אישיים",
            navigationIcon = {
                BackButton(onClick = {})
            },
            actions = listOf(
                TopBarAction(
                    icon = Icons.Rounded.Edit,
                    contentDescription = "ערוך",
                    onClick = {}
                )
            )
        )
    }
}

// Multipreview for different device sizes
@Preview(name = "Phone", group = "Top Bar Devices", device = "spec:width=411dp,height=891dp")
@Preview(name = "Tablet", group = "Top Bar Devices", device = "spec:width=800dp,height=1280dp")
@Composable
private fun ChampionCartTopBarDevicePreview() {
    ChampionCartTheme {
        ChampionCartTopBar(
            title = "ChampionCart",
            showTimeBasedGradient = true,
            actions = listOf(
                TopBarAction(
                    icon = Icons.Rounded.Notifications,
                    contentDescription = "התראות",
                    onClick = {},
                    badge = "2"
                )
            )
        )
    }
}

// Preview showing all locales
@Preview(name = "Hebrew", group = "Locales", locale = "iw")
@Preview(name = "English", group = "Locales", locale = "en")
@Composable
private fun ChampionCartTopBarLocalePreview() {
    ChampionCartTheme {
        ChampionCartTopBar(
            title = "Home",
            subtitle = "Welcome back",
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                }
            }
        )
    }
}