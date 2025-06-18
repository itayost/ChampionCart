package com.example.championcart.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.Language
import com.example.championcart.domain.models.UserPreferences
import com.example.championcart.domain.models.UserStats
import com.example.championcart.presentation.components.EmptyState
import com.example.championcart.presentation.components.EmptyStateType
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.AppTextStyles
import com.example.championcart.ui.theme.GlassmorphicShapes
import com.example.championcart.ui.theme.SizingTokens
import com.example.championcart.ui.theme.SpacingTokens
import com.example.championcart.ui.theme.ThemePreference
import com.example.championcart.ui.theme.extended

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    isGuest = state.isGuest
                )
            }

            // Stats Section (only for logged-in users)
            if (!state.isGuest) {
                item {
                    StatsCard(
                        userStats = state.userStats,
                        isLoading = state.isLoading
                    )
                }
            }

            // Preferences Section
            item {
                PreferencesSection(
                    userPreferences = state.userPreferences,
                    selectedCity = state.selectedCity,
                    onCityClick = viewModel::showCitySelector,
                    onLanguageClick = viewModel::showLanguageSelector,
                    onThemeClick = viewModel::showThemeSelector,
                    onNotificationsToggle = viewModel::toggleNotifications
                )
            }

            // Saved Carts Preview (only for logged-in users)
            if (!state.isGuest && state.savedCarts.isNotEmpty()) {
                item {
                    SavedCartsPreview(
                        savedCartsCount = state.savedCarts.size,
                        onViewAllClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.navigate(Screen.SavedCarts.route)
                        }
                    )
                }
            }

            // Actions Section
            item {
                ActionsSection(
                    isGuest = state.isGuest,
                    onLoginClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.Auth.route)
                    },
                    onLogoutClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showLogoutDialog()
                    },
                    onSavedCartsClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.SavedCarts.route)
                    },
                    onAboutClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // Error handling
            state.error?.let { error ->
                item {
                    EmptyState(
                        type = EmptyStateType.NETWORK_ERROR,
                        title = "שגיאה",
                        subtitle = error,
                        actionLabel = "נסה שוב",
                        onAction = viewModel::clearError
                    )
                }
            }
        }

        // Dialogs
        if (state.showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    viewModel.logout()
                    viewModel.hideLogoutDialog()
                },
                onDismiss = viewModel::hideLogoutDialog
            )
        }

        if (state.showCitySelector) {
            CitySelectionDialog(
                cities = state.availableCities,
                currentCity = state.selectedCity,
                onCitySelected = viewModel::updateDefaultCity,
                onDismiss = viewModel::hideCitySelector
            )
        }

        if (state.showLanguageSelector) {
            LanguageSelectionDialog(
                currentLanguage = state.userPreferences.language,
                onLanguageSelected = viewModel::updateLanguage,
                onDismiss = viewModel::hideLanguageSelector
            )
        }

        if (state.showThemeSelector) {
            ThemeSelectionDialog(
                currentTheme = state.userPreferences.theme,
                onThemeSelected = viewModel::updateTheme,
                onDismiss = viewModel::hideThemeSelector
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    isGuest: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.XL),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.L),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.extended.electricMint,
                                MaterialTheme.colorScheme.extended.cosmicPurple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGuest) "G" else userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // User Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS)
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isGuest) {
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "משתמש אורח",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    userStats: UserStats,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            Text(
                text = "הסטטיסטיקה שלך",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = SpacingTokens.M)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.extended.electricMint,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "נחסך השנה",
                        value = "₪${String.format("%.2f", userStats.totalSavings)}",
                        icon = Icons.Default.Savings
                    )
                    StatItem(
                        title = "מוצרים במעקב",
                        value = userStats.itemsTracked.toString(),
                        icon = Icons.Default.ShoppingCart
                    )
                    StatItem(
                        title = "החודש",
                        value = "₪${String.format("%.2f", userStats.thisMonthSavings)}",
                        icon = Icons.Default.TrendingUp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.extended.electricMint,
            modifier = Modifier.size(SizingTokens.IconM)
        )
        Text(
            text = value,
            style = AppTextStyles.priceMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.extended.electricMint
        )
        Text(
            text = title,
            style = AppTextStyles.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PreferencesSection(
    userPreferences: UserPreferences,
    selectedCity: String,
    onCityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            Text(
                text = "העדפות",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = SpacingTokens.M)
            )

            // City Selection
            PreferenceItem(
                icon = Icons.Default.LocationOn,
                title = "עיר ברירת מחדל",
                subtitle = selectedCity,
                onClick = onCityClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = SpacingTokens.S),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Language Selection
            PreferenceItem(
                icon = Icons.Default.Language,
                title = "שפה",
                subtitle = userPreferences.language.displayName,
                onClick = onLanguageClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = SpacingTokens.S),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Theme Selection
            PreferenceItem(
                icon = Icons.Default.Palette,
                title = "ערכת נושא",
                subtitle = userPreferences.theme.displayName,
                onClick = onThemeClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = SpacingTokens.S),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Notifications Toggle
            PreferenceToggle(
                icon = Icons.Default.Notifications,
                title = "התראות",
                subtitle = "קבל התראות על מבצעים ועדכונים",
                isChecked = userPreferences.notificationsEnabled,
                onCheckedChange = onNotificationsToggle
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = SpacingTokens.S),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun PreferenceItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.extended.electricMint,
            modifier = Modifier.size(SizingTokens.IconM)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SizingTokens.IconS)
        )
    }
}

@Composable
private fun PreferenceToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.extended.electricMint,
            modifier = Modifier.size(SizingTokens.IconM)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.extended.electricMint,
                checkedTrackColor = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SavedCartsPreview(
    savedCartsCount: Int,
    onViewAllClick: () -> Unit
) {
    Card(
        onClick = onViewAllClick,
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.extended.electricMint,
                        modifier = Modifier.size(SizingTokens.IconM)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS)
                ) {
                    Text(
                        text = "עגלות שמורות",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$savedCartsCount עגלות שמורות",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionsSection(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSavedCartsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.extended.surfaceGlass
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            if (isGuest) {
                // Login button for guests
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = GlassmorphicShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.extended.electricMint
                    )
                ) {
                    Icon(
                        Icons.Default.Login,
                        contentDescription = null,
                        modifier = Modifier.size(SizingTokens.IconS)
                    )
                    Spacer(modifier = Modifier.width(SpacingTokens.S))
                    Text("התחבר לחשבון")
                }
            } else {
                // Actions for logged-in users
                ActionItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "עגלות שמורות",
                    onClick = onSavedCartsClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = SpacingTokens.S),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                ActionItem(
                    icon = Icons.Default.Info,
                    title = "אודות",
                    onClick = onAboutClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = SpacingTokens.S),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                ActionItem(
                    icon = Icons.Default.Logout,
                    title = "התנתק",
                    onClick = onLogoutClick,
                    tintColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tintColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = SpacingTokens.M),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(SizingTokens.IconM)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = tintColor
        )
    }
}

// Dialogs
@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "התנתקות",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                "האם אתה בטוח שברצונך להתנתק?",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("התנתק")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        },
        shape = GlassmorphicShapes.Dialog
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L)
            ) {
                Text(
                    text = "בחר שפה",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = SpacingTokens.L)
                )

                Language.values().forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                            .padding(vertical = SpacingTokens.M),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        RadioButton(
                            selected = language == currentLanguage,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.extended.electricMint
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L)
            ) {
                Text(
                    text = "בחר ערכת נושא",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = SpacingTokens.L)
                )

                ThemePreference.values().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                            .padding(vertical = SpacingTokens.M),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.extended.electricMint
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CitySelectionDialog(
    cities: List<String>,
    currentCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // This would use the existing CitySelectionDialog component
    // from CitySelectionDialog.kt with the proper city data
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassmorphicShapes.Dialog,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.L)
            ) {
                Text(
                    text = "בחר עיר",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = SpacingTokens.L)
                )

                LazyColumn {
                    items(cities.size) { index ->
                        val city = cities[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(city)
                                    onDismiss()
                                }
                                .padding(vertical = SpacingTokens.M),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            RadioButton(
                                selected = city == currentCity,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.extended.electricMint
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}