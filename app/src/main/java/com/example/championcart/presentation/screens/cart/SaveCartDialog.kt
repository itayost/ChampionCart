package com.example.championcart.presentation.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.domain.models.CheapestCartResult
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheapestStoreDialog(
    result: CheapestCartResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Best Store Found!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                // Best store info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = ComponentShapes.Card
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.cardPadding)
                    ) {
                        Text(
                            text = result.bestStore.chainName,
                            style = AppTextStyles.storeNameLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.bestStore.storeName,
                            style = AppTextStyles.storeName
                        )
                        Text(
                            text = result.bestStore.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Savings info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Price:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₪${String.format("%.2f", result.totalPrice)}",
                            style = AppTextStyles.priceDisplayLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (result.savingsAmount > 0) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "You Save:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₪${String.format("%.2f", result.savingsAmount)}",
                                style = AppTextStyles.priceDisplay,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.extendedColors.savings
                            )
                            Text(
                                text = "${String.format("%.1f", result.savingsPercentage)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.extendedColors.savings
                            )
                        }
                    }
                }

                // Items breakdown
                if (result.itemsBreakdown.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Dimensions.spacingSmall),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                        text = "Items Breakdown:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
                    ) {
                        items(result.itemsBreakdown) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.itemName} (${item.quantity})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "₪${String.format("%.2f", item.totalPrice)}",
                                    style = AppTextStyles.priceDisplaySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                shape = ComponentShapes.Button
            ) {
                Text("Close")
            }
        },
        shape = ComponentShapes.Dialog
    )
}