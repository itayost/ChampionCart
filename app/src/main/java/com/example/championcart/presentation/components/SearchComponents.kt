package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Search Components
 * Hebrew-first search interface with glassmorphic Electric Harmony design
 * Optimized for Israeli shopping patterns and RTL layout
 */

/**
 * Search Filter Data Class
 */
data class SearchFilter(
    val id: String,
    val nameEnglish: String,
    val nameHebrew: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean = false,
    val count: Int? = null
)

/**
 * Search Suggestion Data Class
 */
data class SearchSuggestion(
    val id: String,
    val text: String,
    val type: SuggestionType = SuggestionType.SEARCH_HISTORY,
    val icon: ImageVector? = null
)

enum class SuggestionType {
    SEARCH_HISTORY,
    PRODUCT_SUGGESTION,
    CATEGORY_SUGGESTION,
    STORE_SUGGESTION
}

/**
 * Glassmorphic Search Bar with Electric Harmony styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {},
    placeholder: String = "חפש מוצרים...",
    showVoiceSearch: Boolean = true,
    showBarcode: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceGlass
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "searchBarBackground"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.SearchField),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 2.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTextStyles.inputHint,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            },
            trailingIcon = {
                SearchTrailingIcons(
                    query = query,
                    onClear = { onQueryChange("") },
                    onVoiceSearch = { /* TODO: Implement voice search */ },
                    onBarcodeSearch = { /* TODO: Implement barcode search */ },
                    showVoiceSearch = showVoiceSearch,
                    showBarcode = showBarcode
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(query)
                    keyboardController?.hide()
                    onActiveChange(false)
                }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = MaterialTheme.colorScheme.extended.electricMint,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.extended.electricMint
            ),
            shape = GlassmorphicShapes.SearchField,
            textStyle = AppTextStyles.inputText.withSmartHebrewSupport(query)
        )
    }

    // Handle focus changes
    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
        }
    }
}

/**
 * Search Bar Trailing Icons
 */
@Composable
private fun SearchTrailingIcons(
    query: String,
    onClear: () -> Unit,
    onVoiceSearch: () -> Unit,
    onBarcodeSearch: () -> Unit,
    showVoiceSearch: Boolean,
    showBarcode: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
    ) {
        // Clear button (only when there's text)
        AnimatedVisibility(
            visible = query.isNotEmpty(),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(SizingTokens.IconM)
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }
        }

        // Voice search button
        if (showVoiceSearch) {
            IconButton(
                onClick = onVoiceSearch,
                modifier = Modifier.size(SizingTokens.IconM)
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = MaterialTheme.colorScheme.extended.electricMint,
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }
        }

        // Barcode scanner button
        if (showBarcode) {
            IconButton(
                onClick = onBarcodeSearch,
                modifier = Modifier.size(SizingTokens.IconM)
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "Scan barcode",
                    tint = MaterialTheme.colorScheme.extended.cosmicPurple,
                    modifier = Modifier.size(SizingTokens.IconS)
                )
            }
        }
    }
}

/**
 * Search Filters Row
 */
@Composable
fun SearchFilters(
    filters: List<SearchFilter>,
    onFilterToggle: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier,
    maxVisible: Int = 5
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        contentPadding = PaddingValues(horizontal = SpacingTokens.L)
    ) {
        items(filters.take(maxVisible)) { filter ->
            SearchFilterChip(
                filter = filter,
                onClick = { onFilterToggle(filter) }
            )
        }

        if (filters.size > maxVisible) {
            item {
                MoreFiltersChip(
                    additionalCount = filters.size - maxVisible,
                    onClick = { /* TODO: Show all filters dialog */ }
                )
            }
        }
    }
}

/**
 * Individual Search Filter Chip
 */
@Composable
private fun SearchFilterChip(
    filter: SearchFilter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (filter.isSelected) {
            MaterialTheme.colorScheme.extended.electricMint
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (filter.isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "chipContent"
    )

    FilterChip(
        selected = filter.isSelected,
        onClick = onClick,
        enabled = true,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                filter.icon?.let { icon ->
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(SizingTokens.IconXS),
                        tint = contentColor
                    )
                }

                Text(
                    text = filter.nameHebrew,
                    style = AppTextStyles.chipText.withSmartHebrewSupport(filter.nameHebrew),
                    color = contentColor
                )

                filter.count?.let { count ->
                    Surface(
                        color = contentColor.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier.glassChip(),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = backgroundColor,
            selectedContainerColor = backgroundColor,
            labelColor = contentColor,
            selectedLabelColor = contentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (filter.isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                alpha = 0.3f
            ),
            selectedBorderColor = Color.Transparent,
            enabled = TODO(),
            selected = TODO()
        )
    )
}

/**
 * More Filters Chip
 */
@Composable
private fun MoreFiltersChip(
    additionalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        enabled = true,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconXS)
                )
                Text(
                    text = "+$additionalCount",
                    style = AppTextStyles.chipText
                )
            }
        },
        modifier = modifier.glassChip(),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.3f),
            enabled = TODO(),
            selected = TODO()
        )
    )
}

/**
 * Search Suggestions List
 */
@Composable
fun SearchSuggestions(
    suggestions: List<SearchSuggestion>,
    onSuggestionClick: (SearchSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible && suggestions.isNotEmpty(),
        enter = fadeIn() + slideInVertically { -it / 2 },
        exit = fadeOut() + slideOutVertically { -it / 2 },
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = GlassmorphicShapes.GlassCard,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.extended.surfaceGlass)
                    .padding(SpacingTokens.S)
            ) {
                suggestions.forEach { suggestion ->
                    SearchSuggestionItem(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
    }
}

/**
 * Individual Search Suggestion Item
 */
@Composable
private fun SearchSuggestionItem(
    suggestion: SearchSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Icon based on suggestion type
        Icon(
            imageVector = suggestion.icon ?: when (suggestion.type) {
                SuggestionType.SEARCH_HISTORY -> Icons.Default.History
                SuggestionType.PRODUCT_SUGGESTION -> Icons.Default.ShoppingBag
                SuggestionType.CATEGORY_SUGGESTION -> Icons.Default.Category
                SuggestionType.STORE_SUGGESTION -> Icons.Default.Store
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(SizingTokens.IconS)
        )

        // Suggestion text
        Text(
            text = suggestion.text,
            style = AppTextStyles.hebrewBodyMedium.withSmartHebrewSupport(suggestion.text),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        // Action icon
        Icon(
            Icons.Default.NorthWest,
            contentDescription = "Apply suggestion",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(SizingTokens.IconXS)
        )
    }
}

/**
 * Quick Filters Section
 */
@Composable
fun QuickFilters(
    onCategoryClick: (String) -> Unit,
    onPriceRangeClick: () -> Unit,
    onStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        Text(
            text = "חיפוש מהיר",
            style = MaterialTheme.typography.titleMedium.withSmartHebrewSupport("חיפוש מהיר"),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            item {
                QuickFilterChip(
                    text = "טחינה",
                    icon = Icons.Default.LocalGroceryStore,
                    onClick = { onCategoryClick("tahini") }
                )
            }
            item {
                QuickFilterChip(
                    text = "מוצרי חלב",
                    icon = Icons.Default.LocalDrink,
                    onClick = { onCategoryClick("dairy") }
                )
            }
            item {
                QuickFilterChip(
                    text = "פירות וירקות",
                    icon = Icons.Default.Eco,
                    onClick = { onCategoryClick("produce") }
                )
            }
            item {
                QuickFilterChip(
                    text = "טווח מחירים",
                    icon = Icons.Default.MonetizationOn,
                    onClick = onPriceRangeClick
                )
            }
            item {
                QuickFilterChip(
                    text = "חנויות",
                    icon = Icons.Default.Store,
                    onClick = onStoreClick
                )
            }
        }
    }
}

/**
 * Quick Filter Chip
 */
@Composable
private fun QuickFilterChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = GlassmorphicShapes.ButtonSmall,
        color = MaterialTheme.colorScheme.extended.surfaceGlass,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = SpacingTokens.M,
                vertical = SpacingTokens.S
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(SizingTokens.IconXS)
            )
            Text(
                text = text,
                style = AppTextStyles.chipText.withSmartHebrewSupport(text)
            )
        }
    }
}

/**
 * Search Result Stats
 */
@Composable
fun SearchResultStats(
    resultCount: Int,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (resultCount > 0) {
            "נמצאו $resultCount תוצאות עבור \"$searchQuery\""
        } else {
            "לא נמצאו תוצאות עבור \"$searchQuery\""
        },
        style = AppTextStyles.hebrewBodyMedium.withSmartHebrewSupport("נמצאו $resultCount תוצאות"),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = modifier.padding(horizontal = SpacingTokens.L)
    )
}