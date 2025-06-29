package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { OnboardingPage.values().size })
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPageContent(
                    page = OnboardingPage.values()[page],
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .glass(
                        shape = Shapes.bottomSheet,
                        elevation = 8.dp
                    )
                    .padding(Padding.xl)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    modifier = Modifier.padding(bottom = Spacing.xl)
                ) {
                    repeat(OnboardingPage.values().size) { index ->
                        PageIndicator(
                            isSelected = pagerState.currentPage == index
                        )
                    }
                }

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    if (pagerState.currentPage > 0) {
                        SecondaryButton(
                            text = "הקודם",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    PrimaryButton(
                        text = if (pagerState.currentPage == OnboardingPage.values().size - 1) {
                            "בואו נתחיל!"
                        } else {
                            "הבא"
                        },
                        onClick = {
                            if (pagerState.currentPage == OnboardingPage.values().size - 1) {
                                viewModel.completeOnboarding()
                                onComplete()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.weight(if (pagerState.currentPage > 0) 1f else 2f),
                        enabled = when (OnboardingPage.values()[pagerState.currentPage]) {
                            OnboardingPage.CITY_SELECTION -> uiState.selectedCity != null
                            else -> true
                        }
                    )
                }

                // Skip Button
                if (pagerState.currentPage < OnboardingPage.values().size - 1) {
                    TextButton(
                        text = "דלג",
                        onClick = {
                            viewModel.completeOnboarding()
                            onComplete()
                        },
                        modifier = Modifier.padding(top = Spacing.m)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    uiState: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    when (page) {
        OnboardingPage.WELCOME -> WelcomePage()
        OnboardingPage.CITY_SELECTION -> CitySelectionPage(
            selectedCity = uiState.selectedCity,
            cities = uiState.cities,
            onCitySelected = viewModel::selectCity
        )
        OnboardingPage.STORE_PREFERENCES -> StorePreferencesPage(
            selectedStores = uiState.selectedStores,
            onStoreToggle = viewModel::toggleStore
        )
        OnboardingPage.NOTIFICATIONS -> NotificationsPage(
            notificationsEnabled = uiState.notificationsEnabled,
            onToggleNotifications = viewModel::toggleNotifications
        )
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn()
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                shape = Shapes.cardLarge,
                colors = CardDefaults.cardColors(
                    containerColor = BrandColors.ElectricMint
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Text(
            text = "ברוכים הבאים ל-ChampionCart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.l))

        Text(
            text = "האפליקציה שתעזור לך לחסוך\nבכל קנייה בסופר",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Features
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            FeatureItem(
                icon = Icons.Rounded.TrendingDown,
                title = "השווה מחירים",
                description = "מצא את המחירים הטובים ביותר"
            )
            FeatureItem(
                icon = Icons.Rounded.Savings,
                title = "חסוך כסף",
                description = "חסוך עד 30% בכל קנייה"
            )
            FeatureItem(
                icon = Icons.Rounded.Speed,
                title = "חווית קנייה מהירה",
                description = "תכנן את הקניות שלך בחכמה"
            )
        }
    }
}

@Composable
private fun CitySelectionPage(
    selectedCity: String?,
    cities: List<String>,
    onCitySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Icon(
            imageVector = Icons.Rounded.LocationCity,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = BrandColors.ElectricMint
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "באיזו עיר אתה גר?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.m))

        Text(
            text = "נציג לך חנויות ומבצעים באזור שלך",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // City Selection Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
            modifier = Modifier.fillMaxWidth()
        ) {
            cities.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    row.forEach { city ->
                        CityCard(
                            city = city,
                            isSelected = city == selectedCity,
                            onClick = { onCitySelected(city) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StorePreferencesPage(
    selectedStores: Set<String>,
    onStoreToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Icon(
            imageVector = Icons.Rounded.Store,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = BrandColors.ElectricMint
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "איזה רשתות אתה מעדיף?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.m))

        Text(
            text = "נתמקד בחנויות האהובות עליך",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Store Selection
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            listOf(
                "שופרסל" to StoreColors.Shufersal,
                "רמי לוי" to StoreColors.RamiLevi,
                "ויקטורי" to StoreColors.Victory,
                "מגה" to StoreColors.Mega,
                "אושר עד" to StoreColors.OsherAd,
                "קואופ" to StoreColors.Coop
            ).forEach { (store, color) ->
                StoreSelectionCard(
                    storeName = store,
                    isSelected = store in selectedStores,
                    storeColor = color,
                    onClick = { onStoreToggle(store) }
                )
            }
        }
    }
}

@Composable
private fun NotificationsPage(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = BrandColors.ElectricMint
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "קבל התראות חכמות",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.m))

        Text(
            text = "נעדכן אותך על מבצעים חדשים\nוירידות מחירים במוצרים שאתה אוהב",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Notification Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.cardLarge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Padding.l),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "התראות מבצעים",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "קבל עדכונים על הנחות ומבצעים",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BrandColors.ElectricMint,
                        checkedTrackColor = BrandColors.ElectricMint.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.l))

        InfoCard(
            message = "תמיד תוכל לשנות את ההעדפות שלך בהגדרות"
        )
    }
}

// Helper Components
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
            .clip(Shapes.badge)
            .background(
                if (isSelected) BrandColors.ElectricMint
                else MaterialTheme.colorScheme.surfaceVariant
            )
    )
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Size.iconLarge),
            tint = BrandColors.ElectricMint
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CityCard(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                BrandColors.ElectricMint
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (!isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun StoreSelectionCard(
    storeName: String,
    isSelected: Boolean,
    storeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                storeColor.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) storeColor else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(Shapes.cardSmall)
                        .background(storeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = storeName.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = storeName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = storeColor,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}

enum class OnboardingPage {
    WELCOME,
    CITY_SELECTION,
    STORE_PREFERENCES,
    NOTIFICATIONS
}