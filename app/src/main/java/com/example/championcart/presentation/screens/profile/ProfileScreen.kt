package com.example.championcart.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialogs and bottom sheets
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showCitySheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "הפרופיל שלי",
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "הגדרות",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    ChampionSnackbar(snackbarData = snackbarData)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = Spacing.l,
                end = Spacing.l,
                top = Spacing.l,
                bottom = Size.bottomNavHeight + Spacing.l
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    userName = uiState.userName,
                    userEmail = uiState.userEmail,
                    isGuest = uiState.isGuest,
                    savingsThisMonth = uiState.savingsThisMonth,
                    productsTracked = uiState.productsTracked,
                    savedCarts = uiState.savedCartsCount
                )
            }

            // Quick Stats
            item {
                QuickStatsSection(
                    totalSavings = uiState.totalSavings,
                    averageSavingsPerCart = uiState.averageSavingsPerCart,
                    favoriteStore = uiState.favoriteStore,
                    shoppingFrequency = uiState.shoppingFrequency
                )
            }

            // Main Actions
            item {
                MainActionsSection(
                    onNavigateToSavedCarts = onNavigateToSavedCarts,
                    savedCartsCount = uiState.savedCartsCount,
                    isGuest = uiState.isGuest
                )
            }

            // Preferences Section
            item {
                PreferencesSection(
                    selectedCity = uiState.selectedCity,
                    selectedLanguage = uiState.selectedLanguage,
                    notificationsEnabled = uiState.notificationsEnabled,
                    darkModeEnabled = uiState.darkModeEnabled,
                    onCityClick = { showCitySheet = true },
                    onLanguageClick = { showLanguageSheet = true },
                    onNotificationsClick = { showNotificationSettings = true },
                    onDarkModeToggle = viewModel::toggleDarkMode
                )
            }

            // Account Section
            item {
                AccountSection(
                    isGuest = uiState.isGuest,
                    onLoginClick = onNavigateToLogin,
                    onLogoutClick = { showLogoutDialog = true }
                )
            }

            // App Info
            item {
                AppInfoSection()
            }
        }
    }

    // Language Selection Bottom Sheet
    LanguageSelectionSheet(
        visible = showLanguageSheet,
        selectedLanguage = uiState.selectedLanguage,
        onLanguageSelected = { language ->
            viewModel.updateLanguage(language)
            showLanguageSheet = false
            scope.launch {
                snackbarHostState.showSnackbar("השפה שונתה ל$language")
            }
        },
        onDismiss = { showLanguageSheet = false }
    )

    // City Selection Bottom Sheet
    CitySelectionSheet(
        visible = showCitySheet,
        selectedCity = uiState.selectedCity,
        cities = uiState.availableCities,
        onCitySelected = { city ->
            viewModel.updateCity(city)
            showCitySheet = false
            scope.launch {
                snackbarHostState.showSnackbar("העיר שונתה ל$city")
            }
        },
        onDismiss = { showCitySheet = false }
    )

    // Notification Settings Dialog
    NotificationSettingsDialog(
        visible = showNotificationSettings,
        settings = uiState.notificationSettings,
        onSettingsChanged = viewModel::updateNotificationSettings,
        onDismiss = { showNotificationSettings = false }
    )

    // Logout Confirmation Dialog
    ConfirmationDialog(
        visible = showLogoutDialog,
        title = "התנתקות",
        text = "האם אתה בטוח שברצונך להתנתק?",
        confirmText = "התנתק",
        onConfirm = {
            viewModel.logout()
            onNavigateToLogin()
        },
        onDismiss = { showLogoutDialog = false }
    )
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    isGuest: Boolean,
    savingsThisMonth: String,
    productsTracked: Int,
    savedCarts: Int
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
            // Profile Avatar
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
                    imageVector = if (isGuest) Icons.Rounded.PersonOutline else Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(Spacing.m))

            // User Info
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (!isGuest) {
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(
                    value = savingsThisMonth,
                    label = "חיסכון החודש",
                    icon = Icons.Rounded.Savings
                )
                ProfileStatItem(
                    value = productsTracked.toString(),
                    label = "מוצרים במעקב",
                    icon = Icons.Rounded.Visibility
                )
                ProfileStatItem(
                    value = savedCarts.toString(),
                    label = "עגלות שמורות",
                    icon = Icons.Rounded.BookmarkBorder
                )
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Size.iconSmall),
            tint = BrandColors.ElectricMint
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickStatsSection(
    totalSavings: String,
    averageSavingsPerCart: String,
    favoriteStore: String,
    shoppingFrequency: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        StatCard(
            title = "חיסכון כולל",
            value = totalSavings,
            icon = Icons.Rounded.TrendingUp,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "חיסכון ממוצע",
            value = averageSavingsPerCart,
            icon = Icons.Rounded.Analytics,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MainActionsSection(
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
                fontWeight = FontWeight.Bold
            )

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.s))

            ChampionListItem(
                title = "עגלות שמורות",
                subtitle = if (savedCartsCount > 0) "$savedCartsCount עגלות" else "אין עגלות שמורות",
                leadingIcon = Icons.Rounded.BookmarkBorder,
                onClick = onNavigateToSavedCarts,
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        if (savedCartsCount > 0) {
                            ChampionBadge(count = savedCartsCount)
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            if (!isGuest) {
                ChampionListItem(
                    title = "היסטוריית קניות",
                    subtitle = "צפה בקניות הקודמות שלך",
                    leadingIcon = Icons.Rounded.History,
                    onClick = { /* TODO */ },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                ChampionListItem(
                    title = "מוצרים במעקב",
                    subtitle = "נהל את רשימת המעקב שלך",
                    leadingIcon = Icons.Rounded.Visibility,
                    onClick = { /* TODO */ },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PreferencesSection(
    selectedCity: String,
    selectedLanguage: String,
    notificationsEnabled: Boolean,
    darkModeEnabled: Boolean,
    onCityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDarkModeToggle: (Boolean) -> Unit
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
                fontWeight = FontWeight.Bold
            )

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.s))

            // City Selection
            ChampionListItem(
                title = "עיר",
                subtitle = selectedCity,
                leadingIcon = Icons.Rounded.LocationCity,
                onClick = onCityClick,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(Size.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // Language
            ChampionListItem(
                title = "שפה",
                subtitle = selectedLanguage,
                leadingIcon = Icons.Rounded.Language,
                onClick = onLanguageClick,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(Size.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            // Notifications
            ChampionListItem(
                title = "התראות",
                subtitle = if (notificationsEnabled) "מופעל" else "כבוי",
                leadingIcon = Icons.Rounded.Notifications,
                onClick = onNotificationsClick,
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandColors.ElectricMint,
                            checkedTrackColor = BrandColors.ElectricMint.copy(alpha = 0.3f)
                        )
                    )
                }
            )

            // Dark Mode
            ChampionListItem(
                title = "מצב כהה",
                subtitle = if (darkModeEnabled) "מופעל" else "כבוי",
                leadingIcon = Icons.Rounded.DarkMode,
                onClick = { onDarkModeToggle(!darkModeEnabled) },
                trailingContent = {
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = onDarkModeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandColors.ElectricMint,
                            checkedTrackColor = BrandColors.ElectricMint.copy(alpha = 0.3f)
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun AccountSection(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "חשבון",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ChampionDivider(modifier = Modifier.padding(vertical = Spacing.s))

            if (isGuest) {
                ChampionListItem(
                    title = "התחבר לחשבון",
                    subtitle = "שמור עגלות וצפה בהיסטוריה",
                    leadingIcon = Icons.Rounded.Login,
                    onClick = onLoginClick,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            } else {
                ChampionListItem(
                    title = "פרטיות",
                    subtitle = "נהל את הגדרות הפרטיות שלך",
                    leadingIcon = Icons.Rounded.Security,
                    onClick = { /* TODO */ },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                ChampionListItem(
                    title = "עזרה ותמיכה",
                    subtitle = "מדריכים ושאלות נפוצות",
                    leadingIcon = Icons.Rounded.HelpOutline,
                    onClick = { /* TODO */ },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                ChampionListItem(
                    title = "התנתק",
                    leadingIcon = Icons.Rounded.Logout,
                    onClick = onLogoutClick,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Size.iconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AppInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Text(
            text = "ChampionCart",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "גרסה 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            TextButton(
                text = "תנאי שימוש",
                onClick = { /* TODO */ }
            )
            TextButton(
                text = "מדיניות פרטיות",
                onClick = { /* TODO */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionSheet(
    visible: Boolean,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "בחר שפה"
    ) {
        val languages = listOf("עברית", "English", "العربية", "русский")

        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            languages.forEach { language ->
                ChampionListItem(
                    title = language,
                    leadingIcon = if (language == selectedLanguage) {
                        Icons.Rounded.CheckCircle
                    } else {
                        Icons.Rounded.Circle
                    },
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySelectionSheet(
    visible: Boolean,
    selectedCity: String,
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "בחר עיר"
    ) {
        LazyColumn(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            items(cities) { city ->
                ChampionListItem(
                    title = city,
                    leadingIcon = if (city == selectedCity) {
                        Icons.Rounded.CheckCircle
                    } else {
                        Icons.Rounded.LocationCity
                    },
                    onClick = { onCitySelected(city) }
                )
            }
        }
    }
}

@Composable
private fun NotificationSettingsDialog(
    visible: Boolean,
    settings: NotificationSettings,
    onSettingsChanged: (NotificationSettings) -> Unit,
    onDismiss: () -> Unit
) {
    ChampionDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = "הגדרות התראות",
        icon = Icons.Rounded.Notifications,
        confirmButton = {
            PrimaryButton(
                text = "שמור",
                onClick = onDismiss
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            NotificationToggleItem(
                title = "התראות מחיר",
                subtitle = "קבל התראות כשמחיר מוצר במעקב יורד",
                checked = settings.priceAlerts,
                onCheckedChange = {
                    onSettingsChanged(settings.copy(priceAlerts = it))
                }
            )

            NotificationToggleItem(
                title = "מבצעים חדשים",
                subtitle = "התראות על מבצעים במוצרים שקנית",
                checked = settings.newDeals,
                onCheckedChange = {
                    onSettingsChanged(settings.copy(newDeals = it))
                }
            )

            NotificationToggleItem(
                title = "תזכורות עגלה",
                subtitle = "תזכורת לסיים עגלה שהתחלת",
                checked = settings.cartReminders,
                onCheckedChange = {
                    onSettingsChanged(settings.copy(cartReminders = it))
                }
            )

            NotificationToggleItem(
                title = "סיכום חודשי",
                subtitle = "סיכום החיסכון החודשי שלך",
                checked = settings.monthlySummary,
                onCheckedChange = {
                    onSettingsChanged(settings.copy(monthlySummary = it))
                }
            )
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BrandColors.ElectricMint,
                checkedTrackColor = BrandColors.ElectricMint.copy(alpha = 0.3f)
            )
        )
    }
}

data class NotificationSettings(
    val priceAlerts: Boolean = true,
    val newDeals: Boolean = true,
    val cartReminders: Boolean = false,
    val monthlySummary: Boolean = true
)