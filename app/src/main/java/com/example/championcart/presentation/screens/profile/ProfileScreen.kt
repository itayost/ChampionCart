package com.example.championcart.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProfileTopBar(
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 80.dp
            )
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    memberSince = state.memberSince,
                    isGuest = state.isGuest
                )
            }

            // Savings Summary Card
            if (!state.isGuest) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SavingsSummaryCard(
                        totalSavings = state.totalSavings,
                        savingsThisMonth = state.savingsThisMonth,
                        savingsThisYear = state.savingsThisYear,
                        comparisonsCount = state.comparisonsCount
                    )
                }

                // Achievements Section
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    AchievementsSection(
                        achievements = state.achievements
                    )
                }
            } else {
                // Guest prompt
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    GuestPromptCard(
                        onSignUp = onNavigateToAuth
                    )
                }
            }

            // Settings Section
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Settings items
            item {
                SettingsCard {
                    // Default city
                    SettingsItem(
                        icon = Icons.Default.LocationOn,
                        title = "Default City",
                        subtitle = state.defaultCity,
                        onClick = { /* Handle city change */ }
                    )

                    Divider()

                    // Language
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = state.language,
                        onClick = { /* Handle language change */ }
                    )

                    Divider()

                    // Theme
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = state.theme,
                        onClick = { /* Handle theme change */ }
                    )

                    Divider()

                    // Notifications
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = if (state.notificationsEnabled) "Enabled" else "Disabled",
                        onClick = { /* Handle notifications toggle */ },
                        trailing = {
                            Switch(
                                checked = state.notificationsEnabled,
                                onCheckedChange = { viewModel.toggleNotifications() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.extendedColors.electricMint
                                )
                            )
                        }
                    )
                }
            }

            // Account Section
            if (!state.isGuest) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    SettingsCard {
                        // Saved carts
                        SettingsItem(
                            icon = Icons.Default.BookmarkBorder,
                            title = "Saved Carts",
                            subtitle = "${state.savedCartsCount} carts",
                            onClick = { /* Navigate to saved carts */ }
                        )

                        Divider()

                        // Price alerts
                        SettingsItem(
                            icon = Icons.Default.NotificationsActive,
                            title = "Price Alerts",
                            subtitle = "${state.priceAlertsCount} active alerts",
                            onClick = { /* Navigate to price alerts */ }
                        )
                    }
                }
            }

            // About Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                SettingsCard {
                    // Privacy policy
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { /* Open privacy policy */ }
                    )

                    Divider()

                    // Terms of service
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { /* Open terms */ }
                    )

                    Divider()

                    // Version
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0",
                        onClick = { }
                    )
                }
            }

            // Logout button
            if (!state.isGuest) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showLogoutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ComponentShapes.Button,
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.extendedColors.errorRed
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.errorRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign Out",
                            color = MaterialTheme.extendedColors.errorRed
                        )
                    }
                }
            }

            // App branding
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Champion Cart",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Smart Savings, Every Day",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out?") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onNavigateToAuth()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.extendedColors.errorRed
                    )
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.extendedColors.electricMint
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    memberSince: String,
    isGuest: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassFrostedBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.cosmicPurple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isGuest) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                } else {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Email
            if (!isGuest) {
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Member since
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Member since $memberSince",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsSummaryCard(
    totalSavings: Double,
    savingsThisMonth: Double,
    savingsThisYear: Double,
    comparisonsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.extendedColors.successGreen,
                    MaterialTheme.extendedColors.electricMint
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f),
                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.successGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Your Savings Journey",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Savings metrics grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SavingsMetric(
                        label = "Total Saved",
                        value = "₪${String.format("%.2f", totalSavings)}",
                        icon = Icons.Default.AccountBalance,
                        color = MaterialTheme.extendedColors.successGreen
                    )
                    SavingsMetric(
                        label = "This Month",
                        value = "₪${String.format("%.2f", savingsThisMonth)}",
                        icon = Icons.Default.CalendarMonth,
                        color = MaterialTheme.extendedColors.electricMint
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SavingsMetric(
                        label = "This Year",
                        value = "₪${String.format("%.2f", savingsThisYear)}",
                        icon = Icons.Default.TrendingUp,
                        color = MaterialTheme.extendedColors.cosmicPurple
                    )
                    SavingsMetric(
                        label = "Comparisons",
                        value = comparisonsCount.toString(),
                        icon = Icons.Default.CompareArrows,
                        color = MaterialTheme.extendedColors.neonCoral
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivational message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ComponentShapes.Card)
                        .background(MaterialTheme.extendedColors.successGreen.copy(alpha = 0.1f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 You're a savings champion!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.extendedColors.successGreen
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementsSection(
    achievements: List<Achievement>
) {
    Column {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            achievements.take(4).forEach { achievement ->
                AchievementBadge(
                    achievement = achievement,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                achievement.color.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (achievement.isUnlocked) {
            BorderStroke(1.dp, achievement.color)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                fontSize = 28.sp,
                modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else 0.3f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (achievement.isUnlocked) {
                    achievement.color
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun GuestPromptCard(
    onSignUp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.extendedColors.electricMint
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join Champion Cart",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign up to track your savings, create shopping lists, and unlock achievements!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignUp,
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Text("Sign Up Now")
            }
        }
    }
}

@Composable
fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.extendedColors.electricMint
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Achievement data class
data class Achievement(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val isUnlocked: Boolean
)