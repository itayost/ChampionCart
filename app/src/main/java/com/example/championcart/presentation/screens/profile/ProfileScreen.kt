package com.example.championcart.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToSavedCarts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isGuest: Boolean = false
) {
    val config = ChampionCartTheme.config

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeader(
            userName = if (isGuest) "אורח" else "ישראל ישראלי",
            userEmail = if (isGuest) "התחבר לחשבון שלך" else "israel@example.com",
            memberSince = if (isGuest) null else "חבר מאז 2023",
            isGuest = isGuest
        )

        // Stats Section
        if (!isGuest) {
            ProfileStatsSection()
        }

        // Menu Items
        ProfileMenuSection(
            onNavigateToSavedCarts = onNavigateToSavedCarts,
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToSettings = onNavigateToSettings,
            isGuest = isGuest
        )

        // Action Button
        Spacer(modifier = Modifier.height(SpacingTokens.XL))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingTokens.L)
        ) {
            if (isGuest) {
                ElectricButton(
                    onClick = onNavigateToLogin,
                    text = "התחבר לחשבון",
                    icon = {
                        Icon(Icons.Default.Login, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                GlassButton(
                    onClick = onNavigateToLogin,
                    text = "התנתק",
                    icon = {
                        Icon(Icons.Default.Logout, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp + SpacingTokens.XL))
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    memberSince: String?,
    isGuest: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.2f),
                            ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpacingTokens.XXL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ChampionCartColors.Brand.ElectricMint,
                                ChampionCartColors.Brand.CosmicPurple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isGuest) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                } else {
                    Text(
                        text = userName.take(1),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // User Info
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            memberSince?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        StatsCard(
            title = "חיסכון כולל",
            value = "₪2,456",
            icon = Icons.Default.Savings,
            accentColor = ChampionCartColors.Semantic.Success,
            modifier = Modifier.weight(1f)
        )

        StatsCard(
            title = "עגלות שמורות",
            value = "12",
            icon = Icons.Default.ShoppingCart,
            accentColor = ChampionCartColors.Brand.CosmicPurple,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileMenuSection(
    onNavigateToSavedCarts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSettings: () -> Unit,
    isGuest: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        Text(
            text = "ניהול חשבון",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = SpacingTokens.S)
        )

        ProfileMenuItem(
            title = "עגלות שמורות",
            subtitle = if (isGuest) "התחבר כדי לשמור עגלות" else "12 עגלות שמורות",
            icon = Icons.Default.BookmarkBorder,
            onClick = onNavigateToSavedCarts,
            enabled = !isGuest,
            badge = if (!isGuest) "12" else null
        )

        ProfileMenuItem(
            title = "ההזמנות שלי",
            subtitle = if (isGuest) "התחבר כדי לראות הזמנות" else "צפה בהיסטוריית הקניות",
            icon = Icons.Default.Receipt,
            onClick = onNavigateToOrders,
            enabled = !isGuest
        )

        ProfileMenuItem(
            title = "הגדרות",
            subtitle = "שפה, התראות ועוד",
            icon = Icons.Default.Settings,
            onClick = onNavigateToSettings,
            enabled = true
        )

        ProfileMenuItem(
            title = "עזרה ותמיכה",
            subtitle = "שאלות נפוצות ויצירת קשר",
            icon = Icons.Default.HelpOutline,
            onClick = { /* TODO */ },
            enabled = true
        )
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    badge: String? = null
) {
    val config = ChampionCartTheme.config
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = if (!config.reduceMotion) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else snap(),
        label = "menuItemScale"
    )

    ModernGlassCard(
        onClick = if (enabled) onClick else null,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
                .alpha(if (enabled) 1f else 0.5f),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconM),
                    tint = if (enabled) {
                        ChampionCartColors.Brand.ElectricMint
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    badge?.let {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ChampionCartColors.Brand.NeonCoral)
                                .padding(horizontal = SpacingTokens.S, vertical = 2.dp)
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chevron
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconS),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// Extension function for alpha
private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer { this.alpha = alpha }
)