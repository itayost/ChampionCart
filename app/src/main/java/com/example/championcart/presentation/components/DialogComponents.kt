package com.example.championcart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.*

/**
 * Champion Cart Dialog Components
 * Consistent dialog implementations for the app
 */

/**
 * Alert dialog with consistent styling
 */
@Composable
fun ChampionCartAlertDialog(
    title: String,
    text: String? = null,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    icon: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                icon?.invoke()
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = text?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = confirmButtonColor
                )
            ) {
                Text(
                    text = confirmButtonText,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = dismissButtonText?.let {
            {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        shape = GlassmorphicShapes.Dialog,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    )
}

/**
 * Selection dialog for choosing from a list
 */
@Composable
fun SelectionDialog(
    title: String,
    items: List<String>,
    selectedItem: String? = null,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    itemContent: @Composable ((String) -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = SpacingTokens.L)
                )

                // Items list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items) { item ->
                        if (itemContent != null) {
                            itemContent(item)
                        } else {
                            SelectionDialogItem(
                                text = item,
                                isSelected = item == selectedItem,
                                onClick = {
                                    onItemSelected(item)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }

                // Cancel button
                Spacer(modifier = Modifier.height(SpacingTokens.L))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "ביטול",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Selection dialog item
 */
@Composable
private fun SelectionDialogItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.extended.electricMint
            )
        )
    }
}

/**
 * Custom selection dialog with any content
 */
@Composable
fun <T> CustomSelectionDialog(
    title: String,
    items: List<T>,
    selectedItem: T? = null,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    itemKey: (T) -> Any,
    itemContent: @Composable (T, Boolean, () -> Unit) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = SpacingTokens.L)
                )

                // Items list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = items,
                        key = itemKey
                    ) { item ->
                        itemContent(
                            item,
                            item == selectedItem,
                            {
                                onItemSelected(item)
                                onDismiss()
                            }
                        )
                    }
                }

                // Cancel button
                Spacer(modifier = Modifier.height(SpacingTokens.L))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "ביטול",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Info dialog with icon and message
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    icon: @Composable (() -> Unit)? = null,
    dismissButtonText: String = "הבנתי"
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.XL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
            ) {
                // Icon
                icon?.invoke()

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Dismiss button
                PrimaryButton(
                    text = dismissButtonText,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Loading dialog overlay
 */
@Composable
fun LoadingDialogOverlay(
    isLoading: Boolean,
    message: String = "טוען...",
    onDismissRequest: (() -> Unit)? = null
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = onDismissRequest ?: {},
            properties = DialogProperties(
                dismissOnBackPress = onDismissRequest != null,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = GlassmorphicShapes.Dialog,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpacingTokens.XL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
                ) {
                    LoadingIndicator()

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}