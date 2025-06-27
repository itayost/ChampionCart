package com.example.championcart.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * Input Field Components
 * Glassmorphic text fields with Electric Harmony styling
 */

/**
 * Glass Text Field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = ComponentShapes.Input.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    intensity = when {
                        isFocused -> GlassIntensity.Medium
                        isError -> GlassIntensity.Light
                        else -> GlassIntensity.Light
                    },
                    shape = shape
                ),
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon ?: if (isError) {
                {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = ChampionCartColors.Semantic.Error
                    )
                }
            } else null,
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampionCartColors.Brand.ElectricMint,
                unfocusedBorderColor = ChampionCartTheme.colors.outline.copy(alpha = 0.5f),
                errorBorderColor = ChampionCartColors.Semantic.Error,
                focusedLabelColor = ChampionCartColors.Brand.ElectricMint,
                unfocusedLabelColor = ChampionCartTheme.colors.onSurfaceVariant,
                errorLabelColor = ChampionCartColors.Semantic.Error,
                cursorColor = ChampionCartColors.Brand.ElectricMint,
                errorCursorColor = ChampionCartColors.Semantic.Error
            )
        )

        // Error message
        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = ChampionCartColors.Semantic.Error,
                modifier = Modifier.padding(
                    start = Spacing.m,
                    top = Spacing.xs
                )
            )
        }
    }
}

/**
 * Search Field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "חיפוש...",
    onSearch: () -> Unit = {},
    enabled: Boolean = true
) {
    GlassTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = ChampionCartTheme.colors.onSurfaceVariant
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(
                    onClick = { onValueChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = ChampionCartTheme.colors.onSurfaceVariant
                    )
                }
            }
        } else null,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        shape = ComponentShapes.Input.Search
    )
}

/**
 * Password Field
 */
@Composable
fun GlassPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "סיסמה",
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    GlassTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = ChampionCartTheme.colors.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = if (passwordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    },
                    tint = ChampionCartTheme.colors.onSurfaceVariant
                )
            }
        },
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )
}

/**
 * Quantity Input Field
 */
@Composable
fun QuantityInput(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier
            .glass(
                intensity = GlassIntensity.Light,
                shape = ComponentShapes.Button.Pill
            )
            .padding(Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        IconButton(
            onClick = {
                if (quantity > minQuantity) {
                    onQuantityChange(quantity - 1)
                }
            },
            enabled = quantity > minQuantity,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.size(Sizing.Icon.s)
            )
        }

        // Quantity text
        Text(
            text = quantity.toString(),
            style = CustomTextStyles.price,
            modifier = Modifier.widthIn(min = 32.dp)
        )

        // Increase button
        IconButton(
            onClick = {
                if (quantity < maxQuantity) {
                    onQuantityChange(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                modifier = Modifier.size(Sizing.Icon.s)
            )
        }
    }
}

/**
 * Filter Chip Input
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
            .glass(
                intensity = if (selected) GlassIntensity.Medium else GlassIntensity.Light,
                shape = ComponentShapes.Special.Chip
            ),
        enabled = enabled,
        leadingIcon = if (selected || leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon ?: Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null,
        shape = ComponentShapes.Special.Chip,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.2f),
            selectedLabelColor = ChampionCartColors.Brand.ElectricMint,
            selectedLeadingIconColor = ChampionCartColors.Brand.ElectricMint
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = if (selected) {
                ChampionCartColors.Brand.ElectricMint
            } else {
                ChampionCartTheme.colors.outline.copy(alpha = 0.5f)
            },
            selectedBorderColor = ChampionCartColors.Brand.ElectricMint
        )
    )
}