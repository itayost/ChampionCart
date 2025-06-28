package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CartScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToStoreComparison: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isGuest: Boolean = false
) {
    // Sample cart data - in real app, this would come from ViewModel
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItemData("1", "חלב תנובה 3%", "₪6.90", 2),
                CartItemData("2", "לחם אחיד פרוס", "₪8.50", 1),
                CartItemData("3", "ביצים L", "₪24.90", 1),
                CartItemData("4", "עגבניות", "₪12.90", 3)
            )
        )
    }

    val totalPrice = cartItems.sumOf { item ->
        (item.price.replace("₪", "").toDoubleOrNull() ?: 0.0) * item.quantity
    }

    val totalItems = cartItems.sumOf { it.quantity }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (cartItems.isEmpty()) {
            EmptyCartState(
                onAddProducts = { onNavigateToProduct("") }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                CartHeader(
                    itemCount = totalItems,
                    totalPrice = totalPrice
                )

                // Cart Items List
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    ModernCartItemList(
                        items = cartItems,
                        onQuantityChange = { item, newQuantity ->
                            cartItems = cartItems.map {
                                if (it.id == item.id) it.copy(quantity = newQuantity) else it
                            }
                        },
                        onRemoveItem = { item ->
                            cartItems = cartItems.filter { it.id != item.id }
                        }
                    )
                }

                // Bottom Actions
                CartBottomActions(
                    onCompareStores = onNavigateToStoreComparison,
                    onCheckout = {
                        if (isGuest) {
                            onNavigateToLogin()
                        } else {
                            // Navigate to checkout
                        }
                    },
                    isGuest = isGuest,
                    totalPrice = totalPrice
                )
            }
        }

        // Floating Add Button
        if (cartItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = SpacingTokens.L,
                        bottom = 180.dp // Above bottom actions
                    )
            ) {
                GlowingIconButton(
                    onClick = { onNavigateToProduct("") },
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "הוסף מוצרים"
                        )
                    },
                    glowColor = ChampionCartColors.Brand.ElectricMint
                )
            }
        }
    }
}

@Composable
private fun CartHeader(
    itemCount: Int,
    totalPrice: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
                .padding(top = SpacingTokens.M)
        ) {
            Text(
                text = "העגלה שלי",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$itemCount פריטים",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "סה״כ משוער",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "₪${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ChampionCartColors.Brand.ElectricMint
                )
            }
        }
    }
}

@Composable
private fun CartBottomActions(
    onCompareStores: () -> Unit,
    onCheckout: () -> Unit,
    isGuest: Boolean,
    totalPrice: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(SpacingTokens.XL)
                .padding(bottom = SpacingTokens.M),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            // Store Comparison Card
            StoreComparisonSummary(
                onClick = onCompareStores
            )

            // Checkout Button
            ElectricButton(
                onClick = onCheckout,
                text = if (isGuest) "התחבר להמשך" else "המשך לתשלום",
                icon = {
                    Icon(
                        if (isGuest) Icons.Default.Login else Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large
            )

            if (isGuest) {
                Text(
                    text = "התחבר כדי לשמור את העגלה ולקבל הצעות מותאמות אישית",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StoreComparisonSummary(
    onClick: () -> Unit
) {
    ModernGlassCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        borderGradient = true
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.CosmicPurple,
                        modifier = Modifier.size(SizingTokens.IconM)
                    )
                }

                Column {
                    Text(
                        text = "השווה מחירים",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "מצא את החנות הזולה ביותר",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyCartState(
    onAddProducts: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpacingTokens.XXL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Empty cart illustration
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                                ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.5f)
                )
            }

            Text(
                text = "העגלה שלך ריקה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "הוסף מוצרים כדי להתחיל לחסוך",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            ElectricButton(
                onClick = onAddProducts,
                text = "התחל קניות",
                icon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                size = ButtonSize.Large
            )
        }
    }
}

/**
 * Quick Add Products Sheet
 * Can be shown as a bottom sheet for quick product addition
 */
@Composable
fun QuickAddProductsSheet(
    onDismiss: () -> Unit,
    onProductAdd: (String) -> Unit
) {
    val popularProducts = listOf(
        "חלב", "לחם", "ביצים", "עגבניות", "מלפפונים",
        "גבינה צהובה", "יוגורט", "חומוס", "טחינה", "פיתות"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XL),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        Text(
            text = "הוספה מהירה",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "מוצרים פופולריים",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Popular products chips
        com.google.accompanist.flowlayout.FlowRow(
            mainAxisSpacing = SpacingTokens.S,
            crossAxisSpacing = SpacingTokens.S
        ) {
            popularProducts.forEach { product ->
                ElectricChipButton(
                    text = product,
                    onClick = { onProductAdd(product) },
                    selected = false
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingTokens.M))

        // Search button
        /*SearchBarButton(
            onClick = onDismiss,
            hint = "חפש מוצר ספציפי"
        )*/
    }
}