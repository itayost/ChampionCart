package com.example.championcart.presentation.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.ui.theme.*

/**
 * Display a single store price row in the product comparison
 * This component is used when showing full store price list
 */
@Composable
fun StorePriceRow(
    storePrice: StorePrice,
    isLowest: Boolean,
    priceDifference: Double,
    modifier: Modifier = Modifier
) {
    val priceColor = when {
        isLowest -> PriceColors.Best
        priceDifference < 2.0 -> PriceColors.Mid
        else -> PriceColors.High
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.cardSmall)
            .then(
                if (isLowest) {
                    Modifier.background(PriceColors.Best.copy(alpha = 0.08f))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Spacing.s, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store Name with Icon
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isLowest) Icons.Rounded.CheckCircle else Icons.Rounded.Store,
                contentDescription = null,
                tint = if (isLowest) PriceColors.Best else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = storePrice.storeName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isLowest) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isLowest) priceColor else MaterialTheme.colorScheme.onSurface
            )
        }

        // Price with Difference
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₪${storePrice.price}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = priceColor
            )

            if (!isLowest && priceDifference > 0) {
                Text(
                    text = "(+₪${String.format("%.1f", priceDifference)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}