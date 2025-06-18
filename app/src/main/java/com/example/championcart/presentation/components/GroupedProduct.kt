package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.domain.models.GroupedProduct
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.ui.theme.*

/**
 * Modern product card with cross-chain price comparison
 * Electric Harmony design with glassmorphic effects and spring animations
 * Optimized for Hebrew RTL layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedProductCard(
    product: GroupedProduct,
    onAddToCart: (StorePrice) -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInCart: Boolean = false,
    cartQuantity: Int = 0
) {
    val haptics = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }
    var selectedStore by remember { mutableStateOf<StorePrice?>(null) }

    // Animation states
    val cardScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = SpringSpecs.Bouncy,
        label = "cardScale"
    )

    val glassIntensity by animateFloatAsState(
        targetValue = if (isExpanded) 0.25f else 0.15f,
        animationSpec = SpringSpecs.Smooth,
        label = "glassIntensity"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isExpanded = !isExpanded
                onProductClick()
            },
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = glassIntensity)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(SpacingTokens.L)
        ) {
            // Header Section
            ProductHeader(
                product = product,
                isInCart = isInCart,
                cartQuantity = cartQuantity
            )

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Price Comparison Section
            PriceComparisonSection(
                product = product,
                isExpanded = isExpanded,
                selectedStore = selectedStore,
                onStoreSelect = { store ->
                    selectedStore = store
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            // Animated content expansion
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(SpacingTokens.M))

                    // Savings indicator
                    if (product.savings > 0) {
                        SavingsIndicator(savings = product.savings)
                    }

                    Spacer(modifier = Modifier.height(SpacingTokens.M))

                    // Add to cart button
                    AddToCartButton(
                        selectedStore = selectedStore,
                        onAddToCart = {
                            selectedStore?.let { store ->
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAddToCart(store)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductHeader(
    product: GroupedProduct,
    isInCart: Boolean,
    cartQuantity: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.itemName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${product.weight}${product.unit ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Cart indicator
        if (isInCart) {
            CartIndicator(quantity = cartQuantity)
        }
    }
}

@Composable
private fun CartIndicator(quantity: Int) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioHighBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "cartIndicatorScale"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.extended.electricMint),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.extended.deepNavy,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PriceComparisonSection(
    product: GroupedProduct,
    isExpanded: Boolean,
    selectedStore: StorePrice?,
    onStoreSelect: (StorePrice) -> Unit
) {
    val sortedPrices = remember(product.prices) {
        product.prices.sortedBy { it.price }
    }

    Column {
        // Best and worst price summary
        if (!isExpanded && sortedPrices.isNotEmpty()) {
            PriceSummary(
                lowestPrice = sortedPrices.first(),
                highestPrice = sortedPrices.last()
            )
        }

        // Detailed price list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                sortedPrices.forEach { storePrice ->
                    StorePriceItem(
                        storePrice = storePrice,
                        isSelected = selectedStore == storePrice,
                        isBestPrice = storePrice == sortedPrices.first(),
                        onClick = { onStoreSelect(storePrice) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceSummary(
    lowestPrice: StorePrice,
    highestPrice: StorePrice
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Best price
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = BrandColors.ElectricMint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "₪${lowestPrice.price}",
                style = MaterialTheme.typography.titleSmall,
                color = BrandColors.ElectricMint,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " - ${lowestPrice.chain}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Price range indicator
        if (lowestPrice != highestPrice) {
            Text(
                text = "עד ₪${highestPrice.price}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textDecoration = TextDecoration.LineThrough
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StorePriceItem(
    storePrice: StorePrice,
    isSelected: Boolean,
    isBestPrice: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.2f)
            isBestPrice -> BrandColors.ElectricMint.copy(alpha = 0.1f)
            else -> Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "storePriceBackground"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCardSmall,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                // Selection indicator
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.extended.electricMint
                    )
                )

                // Store name
                Text(
                    text = storePrice.chain,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isBestPrice) FontWeight.SemiBold else FontWeight.Normal
                )

                // Best price badge
                if (isBestPrice) {
                    Badge(
                        containerColor = BrandColors.ElectricMint,
                        contentColor = MaterialTheme.colorScheme.extended.deepNavy
                    ) {
                        Text(
                            text = "הזול ביותר",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Price
            Text(
                text = "₪${storePrice.price}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isBestPrice) BrandColors.ElectricMint else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SavingsIndicator(savings: Double) {
    val animatedSavings by animateFloatAsState(
        targetValue = savings.toFloat(),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "savingsAnimation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.GlassCardSmall)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        BrandColors.Success.copy(alpha = 0.1f),
                        BrandColors.Success.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(SpacingTokens.M),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Filled.TrendingDown,
            contentDescription = null,
            tint = BrandColors.Success,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(SpacingTokens.S))
        Text(
            text = "חסכון של ₪${String.format("%.2f", animatedSavings)}",
            style = MaterialTheme.typography.bodyMedium,
            color = BrandColors.Success ,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AddToCartButton(
    selectedStore: StorePrice?,
    onAddToCart: () -> Unit
) {
    val isEnabled = selectedStore != null
    val buttonScale by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.95f,
        animationSpec = SpringSpecs.Bouncy,
        label = "buttonScale"
    )

    Button(
        onClick = onAddToCart,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(buttonScale),
        shape = GlassmorphicShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.extended.electricMint,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AddShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(SpacingTokens.S))
            Text(
                text = if (selectedStore != null) {
                    "הוסף לעגלה - ${selectedStore.chain}"
                } else {
                    "בחר חנות"
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}