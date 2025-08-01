package com.example.championcart.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.R
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientShift"
    )

    // Navigate ONLY when the ViewModel has finished loading
    LaunchedEffect(uiState) {
        if (!uiState.isLoading) {
            delay(500)
            when {
                uiState.isLoggedIn -> onNavigateToHome()
                else -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BrandColors.ElectricMint.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface,
                        BrandColors.CosmicPurple.copy(alpha = 0.03f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glass container
        GlassCard(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .scale(scale)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // New Logo using the custom drawable
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = Shapes.cardLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = BrandColors.ElectricMint
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        BrandColors.ElectricMint,
                                        BrandColors.ElectricMint.copy(alpha = 0.8f)
                                    ),
                                    start = androidx.compose.ui.geometry.Offset(gradientShift, 0f),
                                    end = androidx.compose.ui.geometry.Offset(gradientShift + 100f, 100f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use the new logo drawable instead of Material Icon
                        Image(
                            painter = painterResource(id = R.drawable.logo_championcart),
                            contentDescription = "ChampionCart Logo",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // App Name
                Text(
                    text = "סל ניצחון",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = BrandColors.ElectricMint
                )

                Spacer(modifier = Modifier.height(Spacing.s))

                // Tagline
                Text(
                    text = "חסוך חכם, חיה טוב",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                // Loading indicator using the custom LoadingIndicator
                LoadingIndicator(
                    size = 32.dp,
                    strokeWidth = 2.dp
                )
            }
        }

        // Floating elements for visual interest
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl)
        ) {
            // Top left floating element
            ChampionChip(
                text = "🎯",
                selected = false,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .scale(0.8f)
            )

            // Bottom right floating element
            ChampionChip(
                text = "💰",
                selected = false,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .scale(0.8f)
            )
        }
    }
}