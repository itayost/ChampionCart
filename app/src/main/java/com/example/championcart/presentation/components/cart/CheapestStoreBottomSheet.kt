package com.example.championcart.presentation.components.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.CheapestStoreResult
import com.example.championcart.presentation.components.common.ChampionBottomSheet
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.InfoCard
import com.example.championcart.presentation.components.common.LoadingScreen
import com.example.championcart.presentation.components.common.PrimaryButton
import com.example.championcart.presentation.components.common.SecondaryButton
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Spacing

@Composable
fun CheapestStoreBottomSheet(
    visible: Boolean,
    result: CheapestStoreResult?,
    isCalculating: Boolean,
    cartTotal: Double,
    onDismiss: () -> Unit,
    onNavigateToStore: (String) -> Unit,
    onRecalculate: () -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "החנות הזולה ביותר"
    ) {
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
                            }

                            // Price
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
                                            color = SemanticColors.Success
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Missing items warning
                    if (result.missingItems.isNotEmpty()) {
                        InfoCard(
                            message = "${result.missingItems.size} מוצרים לא נמצאו בחנות הזולה",
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

                    // Store comparison
                    if (result.storeTotals.size > 1) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            Text(
                                text = "השוואת מחירים",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )

                            result.storeTotals.entries
                                .sortedBy { it.value }
                                .take(4) // Show top 4 stores
                                .forEach { (store, price) ->
                                    StoreComparisonRow(
                                        storeName = store,
                                        price = price,
                                        isCheapest = store == result.cheapestStore,
                                        difference = price - result.totalPrice
                                    )
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

@Composable
private fun StoreComparisonRow(
    storeName: String,
    price: Double,
    isCheapest: Boolean,
    difference: Double
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
            .padding(Spacing.m),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store icon
        Icon(
            imageVector = Icons.Rounded.Store,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isCheapest) BrandColors.ElectricMint else MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Store name
        Text(
            text = storeName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCheapest) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        // Price difference
        if (difference > 0) {
            Text(
                text = "+₪${String.format("%.2f", difference)}",
                style = MaterialTheme.typography.bodySmall,
                color = PriceColors.High
            )
        }

        // Price
        Text(
            text = "₪${String.format("%.2f", price)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = when {
                isCheapest -> PriceColors.Best
                difference < 10 -> PriceColors.Mid
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        // Checkmark for cheapest
        if (isCheapest) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "הזול ביותר",
                modifier = Modifier.size(20.dp),
                tint = SemanticColors.Success
            )
        }
    }
}