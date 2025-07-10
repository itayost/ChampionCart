package com.example.championcart.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSavedCarts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialogs
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCitySheet by remember { mutableStateOf(false) }

    // Show messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "הפרופיל שלי",
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "הגדרות"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    ChampionSnackbar(snackbarData = data)
                }
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
                    onLogout = { showLogoutDialog = true }
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
                fontWeight = FontWeight.SemiBold
            )

            if (!isGuest && memberSince.isNotEmpty()) {
                Text(
                    text = "חבר מאז $memberSince",
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        StatCard(
            title = "עגלות שמורות",
            value = savedCartsCount.toString(),
            icon = Icons.Rounded.ShoppingCart,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "חיסכון כולל",
            value = totalSavings,
            icon = Icons.Rounded.Savings,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MainActionsCard(
    onNavigateToSavedCarts: () -> Unit,
    savedCartsCount: Int,
    isGuest: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "הפעולות שלי",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.s))

            ChampionListItem(
                title = "עגלות שמורות",
                subtitle = if (savedCartsCount > 0) "$savedCartsCount עגלות" else "אין עגלות שמורות",
                leadingIcon = Icons.Rounded.BookmarkBorder,
                onClick = if (!isGuest) onNavigateToSavedCarts else null,
                trailingContent = if (!isGuest && savedCartsCount > 0) {
                    {
                        ChampionBadge(count = savedCartsCount)
                    }
                } else null
            )

            if (isGuest) {
                Spacer(modifier = Modifier.height(Spacing.s))
                Text(
                    text = "התחבר כדי לשמור עגלות",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Spacing.m)
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
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "העדפות",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.s))

            // City Selection
            ChampionListItem(
                title = "עיר",
                subtitle = selectedCity,
                leadingIcon = Icons.Rounded.LocationOn,
                onClick = onCityClick
            )

            ChampionDivider()

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
    onLogout: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l)
        ) {
            if (!isGuest) {
                ChampionListItem(
                    title = "התנתק",
                    leadingIcon = Icons.Rounded.Logout,
                    onClick = onLogout
                )
            } else {
                Text(
                    text = "משתמש אורח",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}