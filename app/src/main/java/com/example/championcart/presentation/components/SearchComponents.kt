package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Search Components
 * Smart product discovery with Hebrew-first design and glassmorphic effects
 */

// Data classes for search functionality
data class SearchFilter(
    val id: String,
    val name: String,
    val nameHebrew: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean = false,
    val count: Int? = null
)

data class SearchSuggestion(
    val id: String,
    val text: String,
    val textHebrew: String? = null,
    val type: SuggestionType = SuggestionType.Product,
    val category: String? = null,
    val popularity: Int = 0
)

enum class SuggestionType {
    Product,
    Category,
    Brand,
    Store,
    Recent
}

/**
 * Main Search Bar Component
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חפש מוצרים...",
    isActive: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {},
    showVoiceSearch: Boolean = true,
    showBarcode: Boolean = true,
    leadingIcon: ImageVector? = Icons.Default.Search
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFocused by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isActive || isFocused) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        },
        animationSpec = SpringSpecs.Smooth,
        label = "searchBackground"
    )

    GlassSearchBar(
        modifier = modifier.fillMaxWidth()
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
                    style = AppTextStyles.inputHint.withSmartHebrewSupport(placeholder),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        icon,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.extended.electricMint,
                        modifier = Modifier.size(SizingTokens.IconM)
                    )
                }
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
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
        animationSpec = SpringSpecs.Smooth,
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (filter.isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = SpringSpecs.Smooth,
        label = "chipContent"
    )

    FilterChip(
        selected = filter.isSelected,
        onClick = onClick,
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
            selectedContainerColor = backgroundColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (filter.isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            selectedBorderColor = Color.Transparent
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
            borderColor = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.3f)
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
            shape = GlassmorphicShapes.GlassCard
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(GlassIntensity.Heavy)
                    .heightIn(max = 300.dp)
            ) {
                items(suggestions) { suggestion ->
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
    val suggestionIcon = when (suggestion.type) {
        SuggestionType.Product -> Icons.Default.ShoppingCart
        SuggestionType.Category -> Icons.Default.Category
        SuggestionType.Brand -> Icons.Default.BusinessCenter
        SuggestionType.Store -> Icons.Default.Store
        SuggestionType.Recent -> Icons.Default.History
    }

    val suggestionColor = when (suggestion.type) {
        SuggestionType.Product -> MaterialTheme.colorScheme.extended.electricMint
        SuggestionType.Category -> MaterialTheme.colorScheme.extended.cosmicPurple
        SuggestionType.Brand -> MaterialTheme.colorScheme.primary
        SuggestionType.Store -> MaterialTheme.colorScheme.secondary
        SuggestionType.Recent -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        Icon(
            suggestionIcon,
            contentDescription = null,
            tint = suggestionColor,
            modifier = Modifier.size(SizingTokens.IconS)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.textHebrew ?: suggestion.text,
                style = AppTextStyles.inputText.withSmartHebrewSupport(suggestion.textHebrew ?: suggestion.text),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            suggestion.category?.let { category ->
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (suggestion.type == SuggestionType.Recent) {
            IconButton(
                onClick = { /* TODO: Remove from recent */ },
                modifier = Modifier.size(SizingTokens.IconS)
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Remove from recent",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Search Results Header
 */
@Composable
fun SearchResultsHeader(
    query: String,
    resultCount: Int,
    modifier: Modifier = Modifier,
    onClearSearch: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "תוצאות עבור \"$query\"",
                style = AppTextStyles.hebrewHeadline,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$resultCount מוצרים נמצאו",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        TextButton(
            onClick = onClearSearch,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.extended.electricMint
            )
        ) {
            Text("נקה חיפוש")
        }
    }
}

/**
 * Empty Search State
 */
@Composable
fun EmptySearchState(
    message: String = "לא נמצאו תוצאות",
    subtitle: String = "נסה לחפש במילות מפתח אחרות",
    modifier: Modifier = Modifier,
    onSuggestedAction: (() -> Unit)? = null,
    actionText: String = "חפש קטגוריות"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(SizingTokens.IconHuge),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Text(
            text = message,
            style = AppTextStyles.hebrewHeadline,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle,
            style = AppTextStyles.hebrewBodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        onSuggestedAction?.let { action ->
            Button(
                onClick = action,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.extended.electricMint
                ),
                shape = GlassmorphicShapes.Button
            ) {
                Text(actionText)
            }
        }
    }
}

// Preview Data
private val sampleFilters = listOf(
    SearchFilter("all", "All", "הכל", Icons.Default.SelectAll, true, 1250),
    SearchFilter("dairy", "Dairy", "חלב", Icons.Default.LocalDrink, false, 85),
    SearchFilter("meat", "Meat", "בשר", Icons.Default.Restaurant, false, 120),
    SearchFilter("kosher", "Kosher", "כשר", Icons.Default.VerifiedUser, false, 950),
    SearchFilter("organic", "Organic", "אורגני", Icons.Default.Eco, false, 45),
    SearchFilter("sale", "On Sale", "במבצע", Icons.Default.LocalOffer, false, 200)
)

private val sampleSuggestions = listOf(
    SearchSuggestion("1", "Milk", "חלב", SuggestionType.Product, "מוצרי חלב"),
    SearchSuggestion("2", "Dairy", "מוצרי חלב", SuggestionType.Category),
    SearchSuggestion("3", "Tnuva", "תנובה", SuggestionType.Brand),
    SearchSuggestion("4", "Bread", "לחם", SuggestionType.Recent, "אפייה")
)

@Preview(name = "Search Bar")
@Composable
private fun SearchBarPreview() {
    ChampionCartTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                SearchBar(
                    query = "",
                    onQueryChange = {},
                    onSearch = {}
                )
            }
        }
    }
}

@Preview(name = "Search Filters")
@Composable
private fun SearchFiltersPreview() {
    ChampionCartTheme {
        Surface {
            SearchFilters(
                filters = sampleFilters,
                onFilterToggle = {},
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Preview(name = "Search Suggestions")
@Composable
private fun SearchSuggestionsPreview() {
    ChampionCartTheme {
        Surface {
            SearchSuggestions(
                suggestions = sampleSuggestions,
                onSuggestionClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
