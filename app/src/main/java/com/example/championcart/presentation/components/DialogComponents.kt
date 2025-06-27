@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.championcart.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.ChampionCartColors
import com.example.championcart.ui.theme.ChampionCartTheme
import com.example.championcart.ui.theme.ComponentShapes
import com.example.championcart.ui.theme.Elevation
import com.example.championcart.ui.theme.GlassIntensity
import com.example.championcart.ui.theme.PriceLevel
import com.example.championcart.ui.theme.Spacing
import com.example.championcart.ui.theme.glass

/**
 * Dialog, Sheet & Modal Components
 * Glassmorphic dialogs and bottom sheets with Electric Harmony styling
 */

/**
 * Glass Alert Dialog
 */
@Composable
fun GlassAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    confirmText: String = "אישור",
    dismissText: String = "ביטול",
    icon: ImageVector? = null,
    iconTint: Color = ChampionCartTheme.colors.primary
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .glass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Sheet.Dialog
                ),
            shape = ComponentShapes.Sheet.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = ChampionCartTheme.colors.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Component.dialog
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Component.paddingXL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .glass(
                                intensity = GlassIntensity.Light,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = iconTint
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.l))
                }

                // Title
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.m))

                // Message
                Text(
                    text = dialogText,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = ChampionCartTheme.colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    SecondaryGlassButton(
                        onClick = onDismissRequest,
                        text = dismissText,
                        modifier = Modifier.weight(1f)
                    )

                    GlassButton(
                        onClick = {
                            onConfirmation()
                            onDismissRequest()
                        },
                        text = confirmText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Glass Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = ComponentShapes.Sheet.Bottom,
        containerColor = Color.Transparent,
        contentColor = ChampionCartTheme.colors.onSurface,
        dragHandle = {
            GlassBottomSheetHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Sheet.Bottom
                )
                .background(
                    ChampionCartTheme.colors.surface.copy(alpha = 0.95f),
                    shape = ComponentShapes.Sheet.Bottom
                )
                .padding(bottom = Spacing.Component.paddingXL)
        ) {
            content()
        }
    }
}

/**
 * Bottom Sheet Handle
 */
@Composable
private fun GlassBottomSheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.m),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ChampionCartTheme.colors.onSurfaceVariant.copy(alpha = 0.4f))
        )
    }
}

/**
 * Store Selection Bottom Sheet
 */
@Composable
fun StoreSelectionSheet(
    stores: List<StoreItemData>,
    selectedStoreId: String?,
    onStoreSelected: (StoreItemData) -> Unit,
    onDismiss: () -> Unit
) {
    GlassBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Component.paddingL),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "בחר חנות",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // Store list
            stores.forEach { store ->
                StoreSelectionItem(
                    store = store,
                    isSelected = store.id == selectedStoreId,
                    onClick = {
                        onStoreSelected(store)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.m))
        }
    }
}

/**
 * Store Selection Item
 */
@Composable
private fun StoreSelectionItem(
    store: StoreItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val config = ChampionCartTheme.config

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (config.enableHaptics) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            }
            .padding(
                horizontal = Spacing.Component.paddingL,
                vertical = Spacing.Component.paddingM
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Store icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ComponentShapes.Store.Logo)
                    .background(
                        ChampionCartTheme.colors.surfaceVariant
                    )
            )

            Text(
                text = store.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ChampionCartColors.Brand.ElectricMint
            )
        }
    }
}

/**
 * Product Quick View Dialog
 */
@Composable
fun ProductQuickViewDialog(
    product: ProductItemData,
    priceComparisons: List<PriceComparison>,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit,
    onViewDetails: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .glass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Sheet.Dialog
                ),
            shape = ComponentShapes.Sheet.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = ChampionCartTheme.colors.surface.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(ChampionCartTheme.colors.surfaceVariant)
                ) {
                    // Product image placeholder

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Spacing.m)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .glass(
                                    intensity = GlassIntensity.Medium,
                                    shape = ComponentShapes.Button.Square
                                )
                                .padding(Spacing.s)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.Component.paddingL)
                ) {
                    // Product name
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(Spacing.s))

                    // Category
                    GlassFilterChip(
                        selected = false,
                        onClick = {},
                        label = product.category,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(Spacing.l))

                    // Price comparisons
                    Text(
                        text = "השוואת מחירים",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(Spacing.m))

                    priceComparisons.forEach { comparison ->
                        PriceComparisonRow(
                            storeName = comparison.storeName,
                            price = comparison.price,
                            priceLevel = comparison.priceLevel,
                            modifier = Modifier.padding(vertical = Spacing.xs)
                        )
                    }
                }

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glass(
                            intensity = GlassIntensity.Medium,
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(Spacing.Component.paddingL),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    SecondaryGlassButton(
                        onClick = onViewDetails,
                        text = "פרטים נוספים",
                        modifier = Modifier.weight(1f)
                    )

                    GlassButton(
                        onClick = onAddToCart,
                        text = "הוסף לעגלה",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Filter Bottom Sheet
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    categories: List<String>,
    stores: List<String>,
    selectedCategories: Set<String>,
    selectedStores: Set<String>,
    priceRange: ClosedFloatingPointRange<Float>,
    onCategoryToggle: (String) -> Unit,
    onStoreToggle: (String) -> Unit,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    GlassBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Component.paddingL)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "סינון",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onReset) {
                    Text("איפוס")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            // Categories
            Text(
                text = "קטגוריות",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                categories.forEach { category ->
                    GlassFilterChip(
                        selected = category in selectedCategories,
                        onClick = { onCategoryToggle(category) },
                        label = category
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Stores
            Text(
                text = "חנויות",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                stores.forEach { store ->
                    GlassFilterChip(
                        selected = store in selectedStores,
                        onClick = { onStoreToggle(store) },
                        label = store
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Price range
            Text(
                text = "טווח מחירים",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            RangeSlider(
                value = priceRange,
                onValueChange = onPriceRangeChange,
                valueRange = 0f..1000f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = ChampionCartColors.Brand.ElectricMint,
                    activeTrackColor = ChampionCartColors.Brand.ElectricMint,
                    inactiveTrackColor = ChampionCartTheme.colors.surfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₪${priceRange.start.toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "₪${priceRange.endInclusive.toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Apply button
            GlassButton(
                onClick = {
                    onApply()
                    onDismiss()
                },
                text = "החל סינון",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.m))
        }
    }
}

/**
 * Success Animation Dialog
 */
@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "success")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "successScale"
        )

        Card(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .glass(
                    intensity = GlassIntensity.Heavy,
                    shape = ComponentShapes.Card.Hero
                ),
            shape = ComponentShapes.Card.Hero,
            colors = CardDefaults.cardColors(
                containerColor = ChampionCartColors.Semantic.Success.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.Component.paddingXL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = ChampionCartColors.Semantic.Success
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = ChampionCartColors.Semantic.Success
                )
            }
        }
    }
}

// Data class for price comparison
data class PriceComparison(
    val storeName: String,
    val price: String,
    val priceLevel: PriceLevel
)