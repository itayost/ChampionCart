package com.example.championcart.presentation.components.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.championcart.presentation.components.common.ChampionChip
import com.example.championcart.presentation.components.common.ChampionDivider
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Spacing

@Composable
fun CartSummaryContent(
    itemCount: Int,
    totalPrice: Double,
    potentialSavings: Double,
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
            Text(
                text = "סיכום עגלה",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            ChampionChip(
                text = "$itemCount פריטים",
                selected = true
            )
        }

        ChampionDivider()

        // Total price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "סה״כ משוער",
                style = MaterialTheme.typography.bodyLarge
            )

            AnimatedContent(
                targetState = totalPrice,
                transitionSpec = {
                    (fadeIn() + slideInVertically { -it / 2 }) togetherWith
                            (fadeOut() + slideOutVertically { -it / 2 })
                },
                label = "totalPriceAnimation"
            ) { animatedPrice ->
                Text(
                    text = "₪${String.format("%.2f", animatedPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Potential savings
        if (potentialSavings > 0) {
            val savingsColor by animateColorAsState(
                targetValue = when {
                    potentialSavings > 50 -> SemanticColors.Success
                    potentialSavings > 20 -> PriceColors.Mid
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "savingsColor"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "חיסכון פוטנציאלי",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AnimatedContent(
                    targetState = potentialSavings,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { -it / 2 }) togetherWith
                                (fadeOut() + slideOutVertically { -it / 2 })
                    },
                    label = "savingsAnimation"
                ) { animatedSavings ->
                    Text(
                        text = "עד ₪${String.format("%.2f", animatedSavings)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = savingsColor
                    )
                }
            }
        }
    }
}