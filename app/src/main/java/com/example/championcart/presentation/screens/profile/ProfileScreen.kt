package com.example.championcart.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.components.rememberCitySelectionDialog
import com.example.championcart.presentation.screens.savedcarts.SavedCartsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(mainNavController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userEmail = tokenManager.getUserEmail()
    var showSavedCarts by remember { mutableStateOf(false) }

    // City selection dialog
    val (currentCity, showCityDialog) = rememberCitySelectionDialog(
        tokenManager = tokenManager,
        onCitySelected = { city ->
            // City selected, could trigger refresh if needed
        }
    )

    // Show saved carts screen if selected
    if (showSavedCarts) {
        SavedCartsScreen(
            onNavigateToCart = {
                showSavedCarts = false
                // Navigate to cart tab - you might need to adjust this based on your navigation setup
            },
            onBack = { showSavedCarts = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Info Card
            UserInfoCard(
                userEmail = userEmail,
                onLogout = {
                    // Clear all user data
                    tokenManager.clearToken()
                    // Navigate to login screen
                    mainNavController.navigate("login") {
                        popUpTo(mainNavController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                onSignIn = {
                    mainNavController.navigate("login")
                }
            )

            // Menu Options
            ProfileMenuCard(
                userEmail = userEmail,
                currentCity = currentCity,
                onSavedCartsClick = { showSavedCarts = true },
                onCityClick = showCityDialog,
                onLanguageClick = { /* TODO */ },
                onAboutClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun UserInfoCard(
    userEmail: String?,
    onLogout: () -> Unit,
    onSignIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (userEmail != null) {
            // Logged in user
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Logged in as",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userEmail,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                FilledTonalIconButton(
                    onClick = onLogout,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout"
                    )
                }
            }
        } else {
            // Guest user
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Guest User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to save your preferences and shopping lists",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In")
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuCard(
    userEmail: String?,
    currentCity: String,
    onSavedCartsClick: () -> Unit,
    onCityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Saved Carts (only for logged-in users)
            if (userEmail != null) {
                ProfileMenuItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Saved Carts",
                    subtitle = null,
                    onClick = onSavedCartsClick
                )
                HorizontalDivider()
            }

            // City Selection
            ProfileMenuItem(
                icon = Icons.Default.LocationOn,
                title = "City",
                subtitle = currentCity,
                onClick = onCityClick
            )
            HorizontalDivider()

            // Language
            ProfileMenuItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "English",
                onClick = onLanguageClick
            )
            HorizontalDivider()

            // About
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "Version 1.0",
                onClick = onAboutClick
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}