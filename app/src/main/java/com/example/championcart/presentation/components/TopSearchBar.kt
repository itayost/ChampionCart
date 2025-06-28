package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Modern Top Search Bar Component
 * Fixed version with proper imports and usage
 */
@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חפש מוצרים...",
    onBackClick: (() -> Unit)? = null,
    suggestions: List<String> = emptyList(),
    showSuggestions: Boolean = true,
    isLoading: Boolean = false
) {
    val hapticFeedback = LocalHapticFeedback.current
    val config = ChampionCartTheme.config
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Animated scale when focused
    val searchBarScale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else snap(),
        label = "searchBarScale"
    )

    // Glow effect when focused
    val glowAnimation = if (!config.reduceMotion && isFocused) {
        rememberInfiniteTransition(label = "glow")
    } else null

    val glowAlpha by glowAnimation?.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    ) ?: mutableStateOf(0f)

    Column(modifier = modifier) {
        // Main Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S)
        ) {
            // Glow effect behind search bar
            if (isFocused && !config.reduceMotion) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 4.dp)
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        ChampionCartColors.Brand.ElectricMint.copy(alpha = glowAlpha),
                                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0f)
                                    ),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.width
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx())
                            )
                        }
                )
            }

            // Search Bar Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(searchBarScale),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                border = BorderStroke(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) {
                        ChampionCartColors.Brand.ElectricMint
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isFocused) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            }
                        )
                        .padding(horizontal = SpacingTokens.S),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    // Leading Icon
                    IconButton(
                        onClick = {
                            if (config.enableHaptics) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            onBackClick?.invoke() ?: focusRequester.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = onBackClick?.let { Icons.Default.ArrowBack }
                                ?: Icons.Default.Search,
                            contentDescription = onBackClick?.let { "חזור" } ?: "חפש",
                            tint = if (isFocused) {
                                ChampionCartColors.Brand.ElectricMint
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // Search Input
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Animated Placeholder
                        AnimatedVisibility(
                            visible = query.isEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = placeholder,
                                style = CustomTextStyles.price,
                                color = ChampionCartColors.Brand
                            )
                        }

                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            textStyle = typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (query.isNotEmpty()) FontWeight.Medium else FontWeight.Normal
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    onSearch(query)
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(ChampionCartColors.Brand.ElectricMint),
                            interactionSource = interactionSource
                        )
                    }

                    // Action Buttons
                    AnimatedContent(
                        targetState = query.isNotEmpty(),
                        transitionSpec = {
                            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                        },
                        label = "actionButtons"
                    ) { hasQuery ->
                        if (hasQuery) {
                            IconButton(
                                onClick = {
                                    if (config.enableHaptics) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    onQueryChange("")
                                    focusRequester.requestFocus()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "נקה",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            if (isLoading) {
                                Box(
                                    modifier = Modifier.size(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = ChampionCartColors.Brand.ElectricMint
                                    )
                                }
                            } else {
                                MicButton()
                            }
                        }
                    }
                }
            }
        }

        // Search Suggestions
        AnimatedVisibility(
            visible = showSuggestions && suggestions.isNotEmpty() && isFocused,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SearchSuggestions(
                suggestions = suggestions,
                onSuggestionClick = { suggestion ->
                    onQueryChange(suggestion)
                    onSearch(suggestion)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        }
    }
}

/**
 * Animated Microphone Button
 */
@Composable
private fun MicButton() {
    val config = ChampionCartTheme.config
    val hapticFeedback = LocalHapticFeedback.current

    val pulseAnimation = if (!config.reduceMotion) {
        rememberInfiniteTransition(label = "micPulse")
    } else null

    val scale by pulseAnimation?.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    ) ?: mutableStateOf(1f)

    IconButton(
        onClick = {
            if (config.enableHaptics) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        },
        modifier = Modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "חיפוש קולי",
            tint = ChampionCartColors.Brand.ElectricMint
        )
    }
}

/**
 * Search Suggestions Dropdown
 */
@Composable
private fun SearchSuggestions(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    ModernGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L)
            .padding(top = SpacingTokens.S),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = SpacingTokens.S)
        ) {
            suggestions.forEachIndexed { index, suggestion ->
                SearchSuggestionItem(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion) },
                    index = index
                )

                if (index < suggestions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = SpacingTokens.L),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

/**
 * Individual Search Suggestion
 */
@Composable
private fun SearchSuggestionItem(
    suggestion: String,
    onClick: () -> Unit,
    index: Int
) {
    val config = ChampionCartTheme.config
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(index) {
        if (!config.reduceMotion) {
            delay(index * 50L)
        }
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInHorizontally { -it / 4 }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = SpacingTokens.L,
                    vertical = SpacingTokens.M
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(SizingTokens.IconS)
            )

            Text(
                text = suggestion,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.NorthWest,
                contentDescription = "השתמש",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(SizingTokens.IconXS)
            )
        }
    }
}