package com.example.championcart.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.R
import com.example.championcart.presentation.components.FloatingOrb
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    val logoRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoRotation"
    )

    LaunchedEffect(state) {
        when (state) {
            is SplashState.NavigateToLogin -> {
                delay(300) // Small delay for animation
                onNavigateToLogin()
            }
            is SplashState.NavigateToHome -> {
                delay(300)
                onNavigateToHome()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ChampionCartTheme.colors.background,
                        ChampionCartTheme.colors.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        // Floating orbs in background
        FloatingOrb(
            color = ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.3f),
            size = 120.dp,
            offsetX = -60.dp,
            offsetY = 100.dp,
            duration = 8000
        )

        FloatingOrb(
            color = ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.3f),
            size = 80.dp,
            offsetX = 200.dp,
            offsetY = -50.dp,
            duration = 6000,
            delay = 2000
        )

        FloatingOrb(
            color = ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.3f),
            size = 100.dp,
            offsetX = 50.dp,
            offsetY = 300.dp,
            duration = 7000,
            delay = 1000
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        rotationZ = logoRotation
                    }
                    .glass(
                        intensity = GlassIntensity.Heavy,
                        shape = ComponentShapes.Product.Badge
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "ChampionCart Logo",
                    modifier = Modifier.size(80.dp),
                    tint = ChampionCartColors.Brand.ElectricMint
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // App name
            Text(
                text = "ChampionCart",
                style = CustomTextStyles.displayLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ChampionCartTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            // Tagline
            Text(
                text = "חסכון חכם בכל קנייה",
                style = CustomTextStyles.bodyLarge,
                color = ChampionCartTheme.colors.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Loading indicator
            if (state is SplashState.Loading) {
                ShimmerBox(
                    width = 200.dp,
                    height = 4.dp,
                    shape = ComponentShapes.roundedFull
                )
            }
        }

        // Version info at bottom
        Text(
            text = "v1.0.0",
            style = CustomTextStyles.labelSmall,
            color = ChampionCartTheme.colors.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl)
        )
    }
}