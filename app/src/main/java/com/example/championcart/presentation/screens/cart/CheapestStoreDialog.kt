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
import com.example.championcart.presentation.theme.SavingsGreen

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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Best store info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = result.bestStore.chainName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = result.bestStore.storeName,
                            fontSize = 16.sp
                        )
                        Text(
                            text = result.bestStore.address,
                            fontSize = 14.sp,
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
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₪${String.format("%.2f", result.totalPrice)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (result.savingsAmount > 0) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "You Save:",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₪${String.format("%.2f", result.savingsAmount)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SavingsGreen
                            )
                            Text(
                                text = "${String.format("%.1f", result.savingsPercentage)}%",
                                fontSize = 14.sp,
                                color = SavingsGreen
                            )
                        }
                    }
                }

                // Items breakdown
                if (result.itemsBreakdown.isNotEmpty()) {
                    Divider()
                    Text(
                        text = "Items Breakdown:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(result.itemsBreakdown) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.itemName} (${item.quantity})",
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "₪${String.format("%.2f", item.totalPrice)}",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}