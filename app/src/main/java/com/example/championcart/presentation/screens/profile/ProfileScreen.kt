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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.championcart.domain.models.*
import com.example.championcart.presentation.components.ErrorState
import com.example.championcart.presentation.navigation.Screen
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptics = LocalHapticFeedback.current

    // Logout confirmation dialog
    if (state.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideLogoutDialog,
            title = {
                Text(
                    "Logout",
                    style = AppTextStyles.hebrewHeadline
                )
            },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.errorRed
                    )
                ) {
                    Text("Logout", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideLogoutDialog) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    // City Selection Dialog
    if (state.showCitySelector) {
        CitySelectionDialog(
            availableCities = state.availableCities,
            selectedCity = state.selectedCity,
            onCitySelected = { city ->
                viewModel.updateDefaultCity(city)
            },
            onDismiss = viewModel::hideCitySelector
        )
    }

    // Language Selection Dialog
    if (state.showLanguageSelector) {
        LanguageSelectionDialog(
            selectedLanguage = state.userPreferences.language,
            onLanguageSelected = { language ->
                viewModel.updateLanguage(language)
            },
            onDismiss = viewModel::hideLanguageSelector
        )
    }

    // Theme Selection Dialog
    if (state.showThemeSelector) {
        ThemeSelectionDialog(
            selectedTheme = state.userPreferences.theme,
            onThemeSelected = { theme ->
                viewModel.updateTheme(theme)
            },
            onDismiss = viewModel::hideThemeSelector
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.02f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
        ) {
            item {
                ProfileHeader(
                    userName = state.userName,
                    userEmail = state.userEmail,
                    isGuest = state.isGuest,
                    onAvatarClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )
            }

            if (!state.isGuest) {
                item {
                    StatsSection(
                        userStats = state.userStats,
                        isLoading = state.isLoading
                    )
                }
            }

            item {
                SettingsSection(
                    userPreferences = state.userPreferences,
                    selectedCity = state.selectedCity,
                    onCityClick = viewModel::showCitySelector,
                    onLanguageClick = viewModel::showLanguageSelector,
                    onThemeClick = viewModel::showThemeSelector,
                    onNotificationsToggle = viewModel::toggleNotifications,
                    onPriceAlertsToggle = viewModel::togglePriceAlerts
                )
            }

            item {
                ActionsSection(
                    isGuest = state.isGuest,
                    onLoginClick = {
                        navController.navigate(Screen.Auth.route)
                    },
                    onLogoutClick = viewModel::showLogoutDialog,
                    onSavedCartsClick = {
                        navController.navigate(Screen.SavedCarts.route)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // Error handling
            state.error?.let { error ->
                item {
                    ErrorState(
                        message = error,
                        onRetry = viewModel::clearError
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    isGuest: Boolean,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onAvatarClick() },
                shape = CircleShape,
                color = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGuest) Icons.Default.PersonOutline else Icons.Default.Person,
                        contentDescription = "Profile Avatar",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.extendedColors.electricMint
                    )
                }
            }

            // User Info
            Column {
                Text(
                    text = userName,
                    style = AppTextStyles.hebrewHeadline,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!isGuest) {
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Sign in to access all features",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    userStats: UserStats,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge)
        ) {
            Text(
                text = "Your Savings",
                style = AppTextStyles.hebrewHeadline,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.extendedColors.electricMint
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Total Saved",
                        value = "₪${String.format("%.2f", userStats.totalSavings)}",
                        icon = Icons.Default.Savings
                    )
                    StatItem(
                        title = "Items Tracked",
                        value = userStats.itemsTracked.toString(),
                        icon = Icons.Default.ShoppingCart
                    )
                    StatItem(
                        title = "This Month",
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.electricMint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
        Text(
            text = value,
            style = AppTextStyles.priceMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.electricMint
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
private fun SettingsSection(
    userPreferences: UserPreferences,
    selectedCity: String,
    onCityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onPriceAlertsToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge)
        ) {
            Text(
                text = "Settings",
                style = AppTextStyles.hebrewHeadline,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
            )

            SettingItem(
                title = "Default City",
                subtitle = selectedCity,
                icon = Icons.Default.LocationOn,
                onClick = onCityClick
            )

            SettingItem(
                title = "Language",
                subtitle = userPreferences.language.displayName,
                icon = Icons.Default.Language,
                onClick = onLanguageClick
            )

            SettingItem(
                title = "Theme",
                subtitle = userPreferences.theme.displayName,
                icon = Icons.Default.Palette,
                onClick = onThemeClick
            )

            SettingToggleItem(
                title = "Notifications",
                subtitle = "App notifications",
                icon = Icons.Default.Notifications,
                checked = userPreferences.notificationsEnabled,
                onToggle = onNotificationsToggle
            )

            SettingToggleItem(
                title = "Price Alerts",
                subtitle = "Get notified of price drops",
                icon = Icons.Default.NotificationsActive,
                checked = userPreferences.priceAlertsEnabled,
                onToggle = onPriceAlertsToggle
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = ComponentShapes.CardSmall,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.electricMint,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTextStyles.hebrewBody,
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
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.electricMint,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
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
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.extendedColors.electricMint,
                checkedTrackColor = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun ActionsSection(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSavedCartsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            if (isGuest) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.electricMint
                    ),
                    shape = ComponentShapes.Button
                ) {
                    Icon(
                        Icons.Default.Login,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text("Sign In", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                OutlinedButton(
                    onClick = onSavedCartsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ComponentShapes.Button
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text("Saved Carts", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.extendedColors.errorRed
                    ),
                    shape = ComponentShapes.Button
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text("Logout", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// Simple selection dialogs
@Composable
private fun CitySelectionDialog(
    availableCities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select City", style = AppTextStyles.hebrewHeadline) },
        text = {
            LazyColumn {
                items(availableCities.size) { index ->
                    val city = availableCities[index]
                    val isSelected = city == selectedCity

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCitySelected(city)
                                onDismiss()
                            }
                            .padding(Dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onCitySelected(city)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun LanguageSelectionDialog(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language", style = AppTextStyles.hebrewHeadline) },
        text = {
            Column {
                Language.values().forEach { language ->
                    val isSelected = language == selectedLanguage

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                            .padding(Dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun ThemeSelectionDialog(
    selectedTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme", style = AppTextStyles.hebrewHeadline) },
        text = {
            Column {
                ThemePreference.values().forEach { theme ->
                    val isSelected = theme == selectedTheme

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                            .padding(Dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}