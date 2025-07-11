package com.example.championcart.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToSavedCarts: () -> Unit,
    onNavigateToTermsOfService: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCitySheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "הפרופיל שלי",
                scrollBehavior = null
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.l)
            ) {
                Spacer(modifier = Modifier.height(Spacing.m))

                // Profile Header Card
                ProfileHeaderCard(
                    email = uiState.userEmail,
                    isGuest = uiState.isGuest,
                    memberSince = uiState.memberSince
                )

                // Quick Stats from local data
                QuickStatsCard(
                    savedCartsCount = uiState.savedCartsCount,
                    currentCartItems = uiState.currentCartItems,
                    totalSavings = uiState.totalSavingsFormatted,
                    selectedCity = uiState.selectedCity
                )

                // Main Actions
                MainActionsCard(
                    onNavigateToSavedCarts = onNavigateToSavedCarts,
                    savedCartsCount = uiState.savedCartsCount,
                    isGuest = uiState.isGuest
                )

                // Preferences Section
                PreferencesCard(
                    selectedCity = uiState.selectedCity,
                    onCityClick = { showCitySheet = true },
                    notificationsEnabled = uiState.notificationsEnabled,
                    onNotificationsToggle = viewModel::toggleNotifications
                )

                // Account Actions
                AccountActionsCard(
                    isGuest = uiState.isGuest,
                    onLogout = { showLogoutDialog = true },
                    onLogin = onNavigateToLogin
                )

                // Legal Section - ADD THIS
                LegalSection(
                    onNavigateToTermsOfService = onNavigateToTermsOfService,
                    onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy
                )

                // Bottom spacing for navigation bar
                Spacer(modifier = Modifier.height(Size.bottomNavHeight))
            }

            // Loading overlay
            if (uiState.isLoading) {
                LoadingOverlay(
                    visible = true,
                    message = "טוען..."
                )
            }
        }
    }

    // City Selection Bottom Sheet
    if (showCitySheet) {
        CitySelectionBottomSheet(
            visible = true,
            selectedCity = uiState.selectedCity,
            cities = uiState.availableCities,
            onCitySelected = { city ->
                viewModel.updateCity(city)
                showCitySheet = false
            },
            onRequestLocation = {
                // TODO: Implement location detection
                showCitySheet = false
            },
            onDismiss = { showCitySheet = false }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        ConfirmationDialog(
            visible = true,
            title = "התנתקות",
            text = "האם אתה בטוח שברצונך להתנתק?",
            confirmText = "התנתק",
            isDangerous = true,
            onConfirm = {
                viewModel.logout()
                onNavigateToLogin()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    email: String,
    isGuest: Boolean,
    memberSince: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                BrandColors.ElectricMint,
                                BrandColors.CosmicPurple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGuest) Icons.Rounded.PersonOff else Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // User Info
            Text(
                text = if (isGuest) "משתמש אורח" else email,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!isGuest) {
                Text(
                    text = memberSince,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickStatsCard(
    savedCartsCount: Int,
    currentCartItems: Int,
    totalSavings: String,
    selectedCity: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Text(
                text = "סטטיסטיקות מהירות",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Rounded.ShoppingCart,
                    value = savedCartsCount.toString(),
                    label = "עגלות שמורות"
                )

                StatItem(
                    icon = Icons.Rounded.ShoppingBag,
                    value = currentCartItems.toString(),
                    label = "פריטים בעגלה"
                )

                StatItem(
                    icon = Icons.Rounded.Savings,
                    value = totalSavings,
                    label = "חיסכון כולל"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
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
private fun MainActionsCard(
    onNavigateToSavedCarts: () -> Unit,
    savedCartsCount: Int,
    isGuest: Boolean
) {
    if (!isGuest) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(Padding.l)
            ) {
                ChampionListItem(
                    title = "העגלות השמורות שלי",
                    subtitle = "$savedCartsCount עגלות שמורות",
                    leadingIcon = Icons.Rounded.BookmarkBorder,
                    onClick = onNavigateToSavedCarts
                )
            }
        }
    }
}

@Composable
private fun PreferencesCard(
    selectedCity: String,
    onCityClick: () -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l)
        ) {
            Text(
                text = "העדפות",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.m)
            )

            // City Selection
            SecondaryButton(
                text = "עיר: $selectedCity",
                onClick = onCityClick,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.LocationOn
            )

            // Notifications Toggle
            ChampionListItem(
                title = "התראות",
                subtitle = if (notificationsEnabled) "מופעל" else "כבוי",
                leadingIcon = Icons.Rounded.Notifications,
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onNotificationsToggle,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = BrandColors.ElectricMint
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun AccountActionsCard(
    isGuest: Boolean,
    onLogout: () -> Unit,
    onLogin: () -> Unit
) {
        Column(
            modifier = Modifier.padding(Padding.l)
        ) {
            if (!isGuest) {
                TextButton(
                    text = "התנתק",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    color = SemanticColors.Error
                )
            } else {
                PrimaryButton(
                    text = "התחבר",
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Login
                )
            }
    }
}

@Composable
private fun LegalSection(
    onNavigateToTermsOfService: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l)
        ) {
            Text(
                text = "משפטי",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.m)
            )

            ChampionListItem(
                title = "תנאי שירות",
                leadingIcon = Icons.Rounded.Description,
                onClick = onNavigateToTermsOfService,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            ChampionDivider()

            ChampionListItem(
                title = "מדיניות פרטיות",
                leadingIcon = Icons.Rounded.PrivacyTip,
                onClick = onNavigateToPrivacyPolicy,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}