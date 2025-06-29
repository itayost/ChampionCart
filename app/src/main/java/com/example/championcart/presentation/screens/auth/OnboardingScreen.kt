package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    onSkip: () -> Unit = {}, // Accept skip callback
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { OnboardingPage.values().size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    // Skip button in top bar
                    TextButton(
                        onClick = {
                            viewModel.skipOnboarding()
                            onSkip()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("דלג")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
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
        OnboardingPage.NOTIFICATIONS -> NotificationsPage(
            notificationsEnabled = uiState.notificationsEnabled,
            onToggleNotifications = viewModel::toggleNotifications
        )
    }
}

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
                if (isSelected) BrandColors.ElectricMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
    )
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
            text = "השווה מחירים בין כל רשתות המזון הגדולות וחסוך כסף בכל קנייה",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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
            text = "נמצא לך את המחירים הטובים ביותר באזור שלך",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // City Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
            modifier = Modifier.weight(1f)
        ) {
            items(cities) { city ->
                CityCard(
                    city = city,
                    isSelected = city == selectedCity,
                    onClick = { onCitySelected(city) }
                )
            }
        }
    }
}

@Composable
private fun CityCard(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
        Text(
            text = city,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
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
            text = "נודיע לך על מבצעים חמים ומחירים שירדו על המוצרים שאתה קונה",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = Shapes.card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Padding.l),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "הפעל התראות",
                    style = MaterialTheme.typography.bodyLarge
                )

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
    }
}

// Enums and data classes
enum class OnboardingPage {
    WELCOME,
    CITY_SELECTION,
    NOTIFICATIONS
}