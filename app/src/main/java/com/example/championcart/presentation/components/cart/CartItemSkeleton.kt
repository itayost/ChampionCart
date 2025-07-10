package com.example.championcart.presentation.components.cart

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.presentation.components.common.ShimmerEffect
import com.example.championcart.ui.theme.Spacing

@Composable
fun CartItemSkeleton(
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product info skeleton
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Product name
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Store name
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                )
            }

            // Price and quantity skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.l),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price section
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Total price
                    ShimmerEffect(
                        modifier = Modifier
                            .width(80.dp)
                            .height(24.dp)
                    )

                    // Unit price
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                    )
                }

                // Quantity selector skeleton
                ShimmerEffect(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                )
            }
        }
    }
}

@Composable
fun CartSummarySkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(28.dp)
            )
        }

        // Divider
        Box(modifier = Modifier.padding(vertical = Spacing.xs)) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }

        // Total price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(100.dp)
                    .height(18.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(120.dp)
                    .height(28.dp)
            )
        }

        // Potential savings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp)
            )
        }
    }
}