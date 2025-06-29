package com.example.championcart.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.BuildConfig
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleHomeScreen(
    onNavigateToShowcase: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "ChampionCart",
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Rounded.Logout,
                            contentDescription = "התנתק",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Padding.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = BrandColors.ElectricMint
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = "ברוכים הבאים ל-ChampionCart",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            Text(
                text = "האפליקציה שלך לחיסכון חכם בסופר",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Main Actions
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Padding.l),
                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    Text(
                        text = "פעולות ראשיות",
                        style = MaterialTheme.typography.titleMedium
                    )

                    ChampionDivider()

                    ChampionListItem(
                        title = "חפש מוצרים",
                        subtitle = "השווה מחירים בין חנויות",
                        leadingIcon = Icons.Rounded.Search,
                        onClick = { /* TODO: Navigate to search */ },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    ChampionListItem(
                        title = "העגלה שלי",
                        subtitle = "צפה ונהל את העגלה שלך",
                        leadingIcon = Icons.Rounded.ShoppingCart,
                        onClick = { /* TODO: Navigate to cart */ },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    ChampionListItem(
                        title = "התנתק",
                        subtitle = "צא מהחשבון שלך",
                        leadingIcon = Icons.Rounded.Logout,
                        onClick = onLogout,
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

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Developer Options
            if (BuildConfig.DEBUG) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Padding.l),
                        verticalArrangement = Arrangement.spacedBy(Spacing.m)
                    ) {
                        Text(
                            text = "אפשרויות מפתח",
                            style = MaterialTheme.typography.titleMedium
                        )

                        ChampionDivider()

                        ChampionListItem(
                            title = "מרכז רכיבים",
                            subtitle = "צפה בכל הרכיבים של האפליקציה",
                            leadingIcon = Icons.Rounded.Dashboard,
                            onClick = onNavigateToShowcase,
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
        }
    }
}