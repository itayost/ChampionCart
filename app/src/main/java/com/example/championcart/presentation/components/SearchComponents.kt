package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Search Components (Cleaned)
 * Focused search components that are actually used
 */

/**
 * Main search bar component used across the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionCartSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חפש מוצרים...",
    enabled: Boolean = true,
    showVoiceSearch: Boolean = true,
    onVoiceSearchClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (optional)
        onBackClick?.let {
            IconButton(
                onClick = it,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "חזור",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Search field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = if (isFocused) {
                        MaterialTheme.colorScheme.extended.electricMint
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            trailingIcon = {
                Row {
                    // Clear button
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "נקה",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Voice search button
                    if (showVoiceSearch && onVoiceSearchClick != null) {
                        IconButton(
                            onClick = onVoiceSearchClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "חיפוש קולי",
                                tint = MaterialTheme.colorScheme.extended.electricMint
                            )
                        }
                    }
                }
            },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onSearch()
                }
            ),
            shape = GlassmorphicShapes.SearchField,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.extended.electricMint,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
            )
        )
    }

    LaunchedEffect(Unit) {
        if (enabled) {
            focusRequester.requestFocus()
        }
    }
}

/**
 * Quick search suggestions
 */
@Composable
fun SearchSuggestions(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        suggestions.forEach { suggestion ->
            SearchSuggestionItem(
                text = suggestion,
                onClick = { onSuggestionClick(suggestion) }
            )
        }
    }
}

/**
 * Individual search suggestion item
 */
@Composable
private fun SearchSuggestionItem(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.History
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SizingTokens.IconS)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.NorthWest,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(SizingTokens.IconXS)
        )
    }
}

/**
 * Search results header with count and sort options
 */
@Composable
fun SearchResultsHeader(
    resultCount: Int,
    sortOption: String,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "נמצאו $resultCount מוצרים",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TextButton(
            onClick = onSortClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.extended.electricMint
            )
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null,
                modifier = Modifier.size(SizingTokens.IconS)
            )
            Spacer(modifier = Modifier.width(SpacingTokens.XS))
            Text(
                text = sortOption,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * No search results component
 */
@Composable
fun NoSearchResults(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(40.dp)
            )
        }

        // Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            Text(
                text = "לא נמצאו תוצאות",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "לא מצאנו מוצרים התואמים ל-\"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Action button
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