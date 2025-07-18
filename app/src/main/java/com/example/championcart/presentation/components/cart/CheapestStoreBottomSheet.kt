package com.example.championcart.presentation.components.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.presentation.components.common.ChampionBottomSheet
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.InfoCard
import com.example.championcart.presentation.components.common.LoadingScreen
import com.example.championcart.presentation.components.common.PrimaryButton
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Spacing

// Enhanced data class to hold store details with missing items info
data class StoreComparisonData(
    val storeName: String,
    val price: Double,
    val missingItemsCount: Int,
    val availableItemsCount: Int
)

@Composable
fun CheapestStoreBottomSheet(
    visible: Boolean,
    result: CheapestStoreResult?,
    isCalculating: Boolean,
    cartTotal: Double,
    onDismiss: () -> Unit,
    onNavigateToStore: (String) -> Unit,
    onRecalculate: () -> Unit,
    // New parameter to pass detailed store data from the API
    storeDetails: List<StoreComparisonData>? = null
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "החנות הזולה ביותר"
    ) {
        // Apply RTL layout for the entire bottom sheet content
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l)
                    .padding(bottom = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l)
            ) {
                when {
                    isCalculating -> {
                        // Loading state
                        LoadingScreen(
                            message = "מחפש את המחירים הטובים ביותר...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    result != null -> {
                        // Winner store card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.l),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Store info
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    Text(
                                        text = result.cheapestStore,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    result.address?.let { address ->
                                        Text(
                                            text = address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Show available/missing items for winner store
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Available items
                                        if (result.availableItems != null) {
                                            Text(
                                                text = "${result.availableItems} מוצרים זמינים",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SemanticColors.Success,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Missing items warning
                                        val missingCount = result.totalMissingItems ?: result.missingItems.size
                                        if (missingCount > 0) {
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Warning,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = SemanticColors.Warning
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "$missingCount חסרים",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = SemanticColors.Warning,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                // Price (stays on the right side in RTL)
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "₪${String.format("%.2f", result.totalPrice)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PriceColors.Best
                                    )
                                    if (result.storeTotals.size > 1) {
                                        val savings = cartTotal - result.totalPrice
                                        if (savings > 0) {
                                            Text(
                                                text = "חיסכון של ₪${String.format("%.2f", savings)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SemanticColors.Success,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Missing items detail (if any)
                        if (result.missingItems.isNotEmpty()) {
                            InfoCard(
                                message = buildString {
                                    append("המוצרים הבאים לא נמצאו: ")
                                    append(result.missingItems.take(3).joinToString(", "))
                                    if (result.missingItems.size > 3) {
                                        append(" ועוד ${result.missingItems.size - 3}")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                        ) {
                            PrimaryButton(
                                text = "נווט לחנות",
                                onClick = {
                                    onNavigateToStore(result.cheapestStore)
                                },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Rounded.Navigation
                            )
                        }

                        // Store comparison with missing items info
                        if (result.storeTotals.size > 1) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.s)
                            ) {
                                Text(
                                    text = "השוואת חנויות",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )

                                // If we have detailed store data, use it. Otherwise, fall back to simple comparison
                                if (!storeDetails.isNullOrEmpty()) {
                                    storeDetails
                                        .sortedBy { it.price }
                                        .take(5)
                                        .forEach { store ->
                                            StoreComparisonRow(
                                                storeName = store.storeName,
                                                price = store.price,
                                                isCheapest = store.storeName == result.cheapestStore,
                                                difference = store.price - result.totalPrice,
                                                missingItemsCount = store.missingItemsCount,
                                                availableItemsCount = store.availableItemsCount
                                            )
                                        }
                                } else {
                                    // Fallback to simple comparison without missing items info
                                    result.storeTotals.entries
                                        .sortedBy { it.value }
                                        .take(5)
                                        .forEach { (store, price) ->
                                            StoreComparisonRow(
                                                storeName = store,
                                                price = price,
                                                isCheapest = store == result.cheapestStore,
                                                difference = price - result.totalPrice,
                                                missingItemsCount = 0,
                                                availableItemsCount = 0
                                            )
                                        }
                                }
                            }
                        }
                    }

                    else -> {
                        // No results state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.m)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Store,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "לא נמצאו תוצאות",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "נסה להוסיף עוד מוצרים לעגלה",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PrimaryButton(
                                text = "חשב מחירים",
                                onClick = onRecalculate,
                                icon = Icons.Rounded.Refresh
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreComparisonRow(
    storeName: String,
    price: Double,
    isCheapest: Boolean,
    difference: Double,
    missingItemsCount: Int = 0,
    availableItemsCount: Int = 0
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCheapest) {
            BrandColors.ElectricMint.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        label = "storeRowBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .padding(Spacing.l), // Increased padding for more height
        horizontalArrangement = Arrangement.SpaceBetween, // Changed to SpaceBetween for better RTL layout
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store info section (icon + name + availability)
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.Top
        ) {
            // Store icon
            Icon(
                imageVector = Icons.Rounded.Store,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isCheapest) BrandColors.ElectricMint else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Store name and availability info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCheapest) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                // Show availability info if we have the data
                if (availableItemsCount > 0 || missingItemsCount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // Available items
                        if (availableItemsCount > 0) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SemanticColors.Success
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$availableItemsCount זמינים",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SemanticColors.Success,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Missing items warning
                        if (missingItemsCount > 0) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SemanticColors.Warning
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$missingItemsCount חסרים",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SemanticColors.Warning,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Price info column (stays on the right in RTL)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Price with larger font for better readability
            Text(
                text = "₪${String.format("%.2f", price)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isCheapest -> PriceColors.Best
                    difference < 10 -> PriceColors.Mid
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Price difference
            if (difference > 0) {
                Text(
                    text = "+₪${String.format("%.2f", difference)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PriceColors.High
                )
            }

            // Checkmark for cheapest (only if all items available)
            if (isCheapest && missingItemsCount == 0) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "הזול ביותר",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp),
                    tint = SemanticColors.Success
                )
            }
        }
    }
}