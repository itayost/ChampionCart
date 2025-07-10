package com.example.championcart.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Overlay components for ChampionCart
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = Shapes.bottomSheet,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = Spacing.s),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = Shapes.badge
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.l, vertical = Spacing.m),
                        textAlign = TextAlign.Center
                    )
                    ChampionDivider()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = Spacing.xl),
                    content = content
                )
            }
        }
    }
}

@Composable
fun ChampionDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    text: String? = null,
    icon: ImageVector? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = BrandColors.ElectricMint,
                            modifier = Modifier.size(Size.icon)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = text?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            shape = Shapes.cardLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }
}

@Composable
fun ConfirmationDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    text: String,
    confirmText: String = "אישור",
    dismissText: String = "ביטול",
    isDangerous: Boolean = false
) {
    ChampionDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = title,
        text = text,
        icon = if (isDangerous) Icons.Rounded.Warning else Icons.Rounded.Info,
        confirmButton = {
            PrimaryButton(
                text = confirmText,
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                text = dismissText,
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun PopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<PopupMenuItem>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .glass(
                shape = Shapes.card,
                elevation = 8.dp
            )
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    item.onClick()
                    onDismissRequest()
                },
                leadingIcon = item.icon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                enabled = item.enabled
            )
        }
    }
}

data class PopupMenuItem(
    val label: String,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

@Composable
fun LoadingOverlay(
    visible: Boolean,
    message: String? = null
) {
    if (visible) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                shape = Shapes.cardLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(Padding.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.l)
                ) {
                    CircularProgressIndicator(
                        color = BrandColors.ElectricMint,
                        strokeCap = StrokeCap.Round
                    )

                    message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = Shapes.card
) {
    ShimmerEffect(
        modifier = modifier.clip(shape)
    )
}

@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(Size.productCardWidth)
            .height(Size.productCardHeight),
        shape = Shapes.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.m),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Skeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = Shapes.cardSmall
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Skeleton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Skeleton(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp),
                    shape = Shapes.button
                )
                Skeleton(
                    modifier = Modifier.size(36.dp),
                    shape = Shapes.button
                )
            }
        }
    }
}

@Composable
fun ChampionInputDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = BrandColors.ElectricMint,
                            modifier = Modifier.size(Size.icon)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    content()
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            shape = Shapes.cardLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }
}

/**
 * Convenience function for text input dialogs
 */
@Composable
fun TextInputDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    label: String,
    placeholder: String? = null,
    initialValue: String = "",
    validator: ((String) -> String?)? = null, // Returns error message or null if valid
    confirmText: String = "אישור",
    dismissText: String = "ביטול",
    isLoading: Boolean = false
) {
    var value by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(initialValue) }
    var error by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    ChampionInputDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = title,
        description = description,
        icon = icon,
        confirmButton = {
            LoadingButton(
                text = confirmText,
                isLoading = isLoading,
                onClick = {
                    val validationError = validator?.invoke(value)
                    if (validationError != null) {
                        error = validationError
                    } else {
                        onConfirm(value)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                text = dismissText,
                onClick = onDismiss,
                enabled = !isLoading
            )
        }
    ) {
        ChampionTextField(
            value = value,
            onValueChange = {
                value = it
                error = null
            },
            label = label,
            placeholder = placeholder,
            isError = error != null,
            errorMessage = error,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
    }
}