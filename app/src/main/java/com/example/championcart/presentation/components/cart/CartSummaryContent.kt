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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.common.ChampionChip
import com.example.championcart.presentation.components.common.ChampionDivider
import com.example.championcart.presentation.components.common.PrimaryButton
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.PriceColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Spacing

@Composable
fun CartSummaryContent(
    itemCount: Int,
    totalPrice: Double,
    potentialSavings: Double,
    isGuest: Boolean,
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Header with cart icon and item count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BrandColors.ElectricMint
                )
                Text(
                    text = "סיכום עגלה",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            ChampionChip(
                text = "$itemCount פריטים",
                selected = true,
                onClick = {}
            )
        }

        ChampionDivider()

        if (isGuest) {
            // Guest user prompt
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                Text(
                    text = "התחבר כדי לגלות כמה תוכל לחסוך!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "משתמשים רשומים נהנים מ:\n• השוואת מחירים בין חנויות\n• שמירת עגלות קניות\n• מעקב אחר חיסכון",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.m)
                )
            }
        } else {
            // Logged in user - show prices
            // Total price with animation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "סה״כ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                AnimatedContent(
                    targetState = totalPrice,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { -it / 2 }) togetherWith
                                (fadeOut() + slideOutVertically { it / 2 })
                    },
                    label = "priceAnimation"
                ) { animatedPrice ->
                    Text(
                        text = "₪${String.format("%.2f", animatedPrice)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PriceColors.Best
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

                Spacer(modifier = Modifier.height(Spacing.s))

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
}
