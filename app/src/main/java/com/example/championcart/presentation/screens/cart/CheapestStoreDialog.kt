package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.domain.models.CheapestCartResult
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheapestStoreDialog(
    result: CheapestCartResult,
    onDismiss: () -> Unit,
    onNavigateToStore: (() -> Unit)? = null
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .scale(animatedScale),
            shape = ComponentShapes.Dialog,
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationLarge)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with celebration
                CelebrationHeader()

                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = Dimensions.paddingLarge,
                        vertical = Dimensions.paddingMedium
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                ) {
                    // Best store card
                    item {
                        BestStoreCard(result.bestStore)
                    }

                    // Price and savings summary
                    item {
                        PriceSummaryCard(
                            totalPrice = result.totalPrice,
                            savingsAmount = result.savingsAmount,
                            savingsPercentage = result.savingsPercentage
                        )
                    }

                    // Items breakdown
                    if (result.itemsBreakdown.isNotEmpty()) {
                        item {
                            ItemsBreakdownSection(result.itemsBreakdown)
                        }
                    }

                    // Other store options (if available)
                    item {
                        OtherStoresSection(result)
                    }
                }

                // Action buttons
                DialogActions(
                    onNavigateToStore = onNavigateToStore,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun CelebrationHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.savings,
                        MaterialTheme.extendedColors.savings.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(Dimensions.paddingLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Text(
                text = "🎉",
                fontSize = 48.sp,
                modifier = Modifier.scale(1.2f).graphicsLayer { rotationZ = rotation }
            )
            Text(
                text = "Best Store Found!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun BestStoreCard(store: com.example.championcart.domain.models.Store) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = ComponentShapes.Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            // Store icon with brand color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(getStoreColor(store.chainName).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = getStoreColor(store.chainName)
                )
            }

            // Store details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Text(
                    text = store.chainName.uppercase(),
                    style = AppTextStyles.storeNameLarge,
                    fontWeight = FontWeight.Bold,
                    color = getStoreColor(store.chainName)
                )
                Text(
                    text = store.storeName,
                    style = AppTextStyles.storeName
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = store.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceSummaryCard(
    totalPrice: Double,
    savingsAmount: Double,
    savingsPercentage: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.savings.copy(alpha = 0.08f)
        ),
        shape = ComponentShapes.Card,
        border = BorderStroke(
            width = Dimensions.borderThin,
            color = MaterialTheme.extendedColors.savings.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Total price
            Column {
                Text(
                    text = "Your Cart Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingExtraSmall))
                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = AppTextStyles.priceDisplayLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Savings badge
            if (savingsAmount > 0) {
                SavingsDisplay(
                    savingsAmount = savingsAmount,
                    savingsPercentage = savingsPercentage
                )
            }
        }
    }
}

@Composable
private fun SavingsDisplay(
    savingsAmount: Double,
    savingsPercentage: Double
) {
    Surface(
        shape = ComponentShapes.Badge,
        color = MaterialTheme.extendedColors.bestDeal,
        shadowElevation = Dimensions.elevationMedium
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YOU SAVE",
                style = AppTextStyles.badgeText,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "₪${String.format("%.2f", savingsAmount)}",
                style = AppTextStyles.priceDisplay,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${savingsPercentage.toInt()}%",
                style = AppTextStyles.buttonText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ItemsBreakdownSection(items: List<com.example.championcart.domain.models.CartItemBreakdown>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
    ) {
        Text(
            text = "Items Breakdown",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = Dimensions.borderThin,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.itemName,
                                style = if (isHebrewText(item.itemName)) {
                                    AppTextStyles.hebrewText
                                } else {
                                    AppTextStyles.productName
                                }
                            )
                            Text(
                                text = "${item.quantity} × ₪${String.format("%.2f", item.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "₪${String.format("%.2f", item.totalPrice)}",
                            style = AppTextStyles.priceDisplaySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (item != items.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Dimensions.spacingExtraSmall),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OtherStoresSection(result: CheapestCartResult) {
    // Mock other stores data (since not provided in the result model)
    val otherStores = listOf(
        OtherStoreOption("Victory", "052", result.totalPrice + 10.60),
        OtherStoreOption("Shufersal", "007", result.totalPrice + 23.40)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
    ) {
        Text(
            text = "Other Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        otherStores.forEach { store ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ComponentShapes.Card,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${store.chain} ${store.storeId}",
                        style = AppTextStyles.storeName
                    )
                    Text(
                        text = "₪${String.format("%.2f", store.totalPrice)} (+₪${String.format("%.2f", store.totalPrice - result.totalPrice)})",
                        style = AppTextStyles.priceDisplaySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogActions(
    onNavigateToStore: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = Dimensions.elevationMedium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            if (onNavigateToStore != null) {
                OutlinedButton(
                    onClick = onNavigateToStore,
                    modifier = Modifier.weight(1f),
                    shape = ComponentShapes.Button
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text(
                        "Navigate",
                        style = AppTextStyles.buttonText
                    )
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Done",
                    style = AppTextStyles.buttonText
                )
            }
        }
    }
}

// Helper function to get store brand color
@Composable
private fun getStoreColor(storeName: String): Color {
    return when (storeName.lowercase()) {
        "shufersal" -> ChampionCartColors.shufersal
        "victory" -> ChampionCartColors.victory
        else -> MaterialTheme.colorScheme.primary
    }
}

// Helper function to detect Hebrew text
private fun isHebrewText(text: String): Boolean {
    return text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }
}

// Data class for other store options
private data class OtherStoreOption(
    val chain: String,
    val storeId: String,
    val totalPrice: Double
)