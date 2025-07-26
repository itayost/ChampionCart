package com.example.championcart.presentation.components.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * City selection with two bottom sheets:
 * 1. Options sheet - shows current city, location option, and choose city
 * 2. Cities list sheet - full screen list of all cities
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CitySelectionBottomSheet(
    visible: Boolean,
    selectedCity: String,
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    onRequestLocation: () -> Unit,
    onDismiss: () -> Unit,
    isDetectingLocation: Boolean = false,
    locationError: String? = null,
    locationDetectionSuccess: Boolean = false
) {
    var showCitiesList by remember { mutableStateOf(false) }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Location permission state
    val locationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Handle location permission request
    val handleLocationRequest = {
        when {
            locationPermissionState.status.isGranted -> {
                onRequestLocation()
            }
            locationPermissionState.status.shouldShowRationale -> {
                showLocationPermissionDialog = true
            }
            else -> {
                showLocationPermissionDialog = true
            }
        }
    }

    // Show success animation when city is detected
    LaunchedEffect(locationDetectionSuccess) {
        if (locationDetectionSuccess) {
            showSuccessAnimation = true
        }
    }

    // Options Bottom Sheet
    if (visible && !showCitiesList && !showSuccessAnimation) {
        val optionsSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                if (!isDetectingLocation) {
                    onDismiss()
                }
            },
            sheetState = optionsSheetState,
            shape = Shapes.bottomSheet,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            // Apply RTL layout
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box {
                    CityOptionsContent(
                        selectedCity = selectedCity,
                        onUseLocation = handleLocationRequest,
                        onChooseCity = {
                            showCitiesList = true
                        },
                        locationError = locationError,
                    )

                    // Loading overlay during detection
                    if (isDetectingLocation) {
                        LoadingOverlay(
                            visible = true,
                            message = "מזהה את המיקום שלך"
                        )
                    }
                }
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

    // Location Permission Dialog
    LocationPermissionDialog(
        visible = showLocationPermissionDialog,
        onGrantPermission = {
            showLocationPermissionDialog = false
            locationPermissionState.launchPermissionRequest()
        },
        onDenyPermission = {
            showLocationPermissionDialog = false
        }
    )

    // Success Animation
    LocationDetectedAnimation(
        visible = showSuccessAnimation,
        detectedCity = selectedCity,
        onAnimationComplete = {
            showSuccessAnimation = false
            onDismiss()
        }
    )
}

@Composable
private fun CityOptionsContent(
    selectedCity: String,
    onUseLocation: () -> Unit,
    onChooseCity: () -> Unit,
    locationError: String? = null
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

            // Show error if location detection failed
            locationError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.l),
                    shape = Shapes.card,
                    colors = CardDefaults.cardColors(
                        containerColor = SemanticColors.Error.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = SemanticColors.Error.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = Spacing.m),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = SemanticColors.Error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = SemanticColors.Error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.m))
            }

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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
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