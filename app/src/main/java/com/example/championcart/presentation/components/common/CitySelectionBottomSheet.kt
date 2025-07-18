package com.example.championcart.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*

/**
 * City selection with two bottom sheets:
 * 1. Options sheet - shows current city, location option, and choose city
 * 2. Cities list sheet - full screen list of all cities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionBottomSheet(
    visible: Boolean,
    selectedCity: String,
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    onRequestLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    var showCitiesList by remember { mutableStateOf(false) }

    // Options Bottom Sheet
    if (visible && !showCitiesList) {
        val optionsSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = optionsSheetState,
            shape = Shapes.bottomSheet,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            // Apply RTL layout
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                CityOptionsContent(
                    selectedCity = selectedCity,
                    onUseLocation = {
                        onRequestLocation()
                        onDismiss()
                    },
                    onChooseCity = {
                        showCitiesList = true
                    }
                )
            }
        }
    }

    // Cities List Bottom Sheet
    if (showCitiesList) {
        val citiesSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                showCitiesList = false
            },
            sheetState = citiesSheetState,
            shape = Shapes.bottomSheet,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            // Apply RTL layout
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                CitiesListContent(
                    cities = cities,
                    selectedCity = selectedCity,
                    onCitySelected = { city ->
                        onCitySelected(city)
                        showCitiesList = false
                        onDismiss() // Close both sheets
                    },
                    onBack = {
                        showCitiesList = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CityOptionsContent(
    selectedCity: String,
    onUseLocation: () -> Unit,
    onChooseCity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = Spacing.xl)
    ) {
        // Title
        Text(
            text = "בחר מיקום",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.m, bottom = Spacing.l),
            textAlign = TextAlign.Center
        )

        ChampionDivider()

        Column(
            modifier = Modifier.padding(vertical = Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            // Current city display
            Text(
                text = "המיקום הנוכחי",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Spacing.l, end = Spacing.l, bottom = Spacing.s)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l),
                shape = Shapes.card,
                colors = CardDefaults.cardColors(
                    containerColor = BrandColors.ElectricMint.copy(alpha = 0.1f)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = BrandColors.ElectricMint.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = Spacing.l),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = BrandColors.ElectricMint,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = selectedCity,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "נבחר",
                        tint = BrandColors.ElectricMint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.l))

            // Use location option
            ChampionListItem(
                title = "השתמש במיקום הנוכחי",
                subtitle = "זהה את העיר שלך אוטומטית",
                leadingIcon = Icons.Rounded.MyLocation,
                onClick = onUseLocation,
                modifier = Modifier.padding(horizontal = Spacing.l)
            )

            // Choose city option
            ChampionListItem(
                title = "בחר עיר מהרשימה",
                subtitle = "ראה את כל הערים הזמינות",
                leadingIcon = Icons.Rounded.LocationCity,
                onClick = onChooseCity,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft, // Changed from ChevronRight for RTL
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(horizontal = Spacing.l)
            )
        }
    }
}

@Composable
private fun CitiesListContent(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.l, vertical = Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward, // Changed from ArrowBack for RTL
                    contentDescription = "חזור",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "בחר עיר",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Spacer for balance (moved to start for RTL)
            Spacer(modifier = Modifier.size(48.dp))


        }

        ChampionDivider()

        // Cities list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Spacing.m)
        ) {
            items(cities) { city ->
                CityListItem(
                    city = city,
                    isSelected = city == selectedCity,
                    onClick = { onCitySelected(city) }
                )

                if (city != cities.last()) {
                    ChampionDivider(
                        modifier = Modifier.padding(horizontal = Spacing.l)
                    )
                }
            }
        }
    }
}

@Composable
private fun CityListItem(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (isSelected) {
                    BrandColors.ElectricMint.copy(alpha = 0.05f)
                } else {
                    Color.Transparent
                }
            )
            .padding(horizontal = Spacing.l, vertical = Spacing.l),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = if (isSelected) {
                    BrandColors.ElectricMint
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = city,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "נבחר",
                tint = BrandColors.ElectricMint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}