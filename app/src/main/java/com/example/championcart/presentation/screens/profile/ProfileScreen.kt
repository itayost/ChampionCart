package com.example.championcart.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.Language
import com.example.championcart.domain.models.UserPreferences
import com.example.championcart.domain.models.UserStats
import com.example.championcart.presentation.components.*
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    ChampionCartScreen(
        topBar = {
            ChampionCartTopBar(
                title = "הפרופיל שלי",
                showBackButton = false
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Profile Header
            item {
                ProfileHeaderCard(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    isGuest = state.isGuest
                )
            }

            // Stats Section (only for logged-in users)
            if (!state.isGuest) {
                item {
                    UserStatsCard(
                        userStats = state.userStats,
                        isLoading = state.isLoading
                    )
                }
            }

            // Preferences Section
            item {
                PreferencesCard(
                    userPreferences = state.userPreferences,
                    selectedCity = state.selectedCity,
                    onCityClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showCitySelector()
                    },
                    onLanguageClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showLanguageSelector()
                    },
                    onThemeClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showThemeSelector()
                    },
                    onNotificationsToggle = viewModel::toggleNotifications
                )
            }

            // Saved Carts Preview (only for logged-in users)
            if (!state.isGuest && state.savedCarts.isNotEmpty()) {
                item {
                    ActionCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "עגלות שמורות",
                        subtitle = "${state.savedCarts.size} עגלות שמורות",
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.navigate(Screen.SavedCarts.route)
                        }
                    )
                }
            }

            // Actions Section
            item {
                ProfileActionsCard(
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
            ChampionCartAlertDialog(
                title = "התנתקות",
                text = "האם אתה בטוח שברצונך להתנתק?",
                confirmButtonText = "התנתק",
                dismissButtonText = "ביטול",
                onConfirm = {
                    viewModel.logout()
                    viewModel.hideLogoutDialog()
                },
                onDismiss = viewModel::hideLogoutDialog,
                confirmButtonColor = MaterialTheme.colorScheme.error
            )
        }

        if (state.showCitySelector) {
            SelectionDialog(
                title = "בחר עיר",
                items = state.availableCities,
                selectedItem = state.selectedCity,
                onItemSelected = viewModel::updateDefaultCity,
                onDismiss = viewModel::hideCitySelector
            )
        }

        if (state.showLanguageSelector) {
            val languages = Language.values().map { it.displayName }
            SelectionDialog(
                title = "בחר שפה",
                items = languages,
                selectedItem = state.userPreferences.language.displayName,
                onItemSelected = { displayName ->
                    Language.values().find { it.displayName == displayName }?.let {
                        viewModel.updateLanguage(it)
                    }
                },
                onDismiss = viewModel::hideLanguageSelector
            )
        }

        if (state.showThemeSelector) {
            val themes = ThemePreference.values()
                .filter { it != ThemePreference.Auto }
                .map { it.displayName }

            SelectionDialog(
                title = "בחר ערכת נושא",
                items = themes,
                selectedItem = state.userPreferences.theme.displayName,
                onItemSelected = { displayName ->
                    ThemePreference.values().find { it.displayName == displayName }?.let {
                        viewModel.updateTheme(it)
                    }
                },
                onDismiss = viewModel::hideThemeSelector
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    userName: String,
    userEmail: String,
    isGuest: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.XL),
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.L),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            UserAvatar(
                userName = userName,
                isGuest = isGuest,
                size = 80.dp
            )

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
                Text(
                    text = if (!isGuest) userEmail else "משתמש אורח",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun UserStatsCard(
    userStats: UserStats,
    isLoading: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            SectionHeader(title = "הסטטיסטיקה שלך")

            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
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
private fun PreferencesCard(
    userPreferences: UserPreferences,
    selectedCity: String,
    onCityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            SectionHeader(title = "העדפות")

            // City Selection
            SettingsItem(
                icon = Icons.Default.LocationOn,
                title = "עיר ברירת מחדל",
                subtitle = selectedCity,
                onClick = onCityClick
            )

            ItemDivider()

            // Language Selection
            SettingsItem(
                icon = Icons.Default.Language,
                title = "שפה",
                subtitle = userPreferences.language.displayName,
                onClick = onLanguageClick
            )

            ItemDivider()

            // Theme Selection
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "ערכת נושא",
                subtitle = userPreferences.theme.displayName,
                onClick = onThemeClick
            )

            ItemDivider()

            // Notifications Toggle
            SwitchListItem(
                icon = Icons.Default.Notifications,
                title = "התראות",
                subtitle = "קבל התראות על מבצעים ועדכונים",
                checked = userPreferences.notificationsEnabled,
                onCheckedChange = onNotificationsToggle
            )
        }
    }
}

@Composable
private fun ProfileActionsCard(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSavedCartsClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L)
        ) {
            if (isGuest) {
                // Login button for guests
                PrimaryButton(
                    text = "התחבר לחשבון",
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.Login
                )
            } else {
                // Actions for logged-in users
                MenuItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "עגלות שמורות",
                    onClick = onSavedCartsClick
                )

                ItemDivider()

                MenuItem(
                    icon = Icons.Default.Logout,
                    title = "התנתק",
                    onClick = onLogoutClick,
                    tintColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}