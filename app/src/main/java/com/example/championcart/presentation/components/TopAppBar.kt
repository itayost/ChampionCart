package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Glassmorphic TopAppBar with Electric Harmony design
 * Supports navigation, actions, and scroll behaviors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicTopAppBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    isTransparent: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            navigationIcon?.invoke() ?: onNavigationClick?.let {
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        it()
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier
            .then(
                if (!isTransparent) {
                    Modifier
                        .shadow(
                            elevation = 4.dp,
                            shape = ComponentShapes.Navigation.TopAppBar
                        )
                        .glassmorphic(
                            intensity = GlassIntensity.Light,
                            shape = ComponentShapes.Navigation.TopAppBar
                        )
                } else {
                    Modifier
                }
            ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isTransparent) Color.Transparent else Color.Transparent,
            scrolledContainerColor = if (isTransparent) Color.Transparent else Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * Large TopAppBar variant with gradient background
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicLargeTopAppBar(
    title: String,
    subtitle: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f)
    ),
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            onNavigationClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier
            .background(
                Brush.verticalGradient(gradientColors)
            ),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * Search TopAppBar with integrated search field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onNavigationClick: (() -> Unit)? = null,
    placeholder: String = "חפש מוצרים...", // Search products
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            SearchTextField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearchSubmit,
                placeholder = placeholder,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            onNavigationClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier
            .glassmorphic(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Navigation.TopAppBar
            ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Collapsing TopAppBar with parallax effect
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CollapsingTopAppBar(
    title: String,
    expandedContent: @Composable () -> Unit,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val collapsed by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction > 0.5f
        }
    }

    Box(modifier = modifier) {
        // Expanded background content
        AnimatedVisibility(
            visible = !collapsed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                expandedContent()
            }
        }

        // Collapsing app bar
        MediumTopAppBar(
            title = {
                AnimatedContent(
                    targetState = collapsed,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { isCollapsed ->
                    Text(
                        text = if (isCollapsed) title else "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            navigationIcon = {
                onNavigationClick?.let {
                    IconButton(onClick = it) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )
    }
}