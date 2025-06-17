package com.example.championcart.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.ui.theme.extendedColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Animation states
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Animated gradient offset
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    LaunchedEffect(Unit) {
        // Logo animation
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Text fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, delayMillis = 300)
        )

        // Check authentication status
        delay(2000)

        if (tokenManager.getToken() != null) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.electricMint.copy(alpha = 0.1f),
                        MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.15f),
                        MaterialTheme.extendedColors.neonCoral.copy(alpha = 0.1f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(gradientOffset, 0f),
                    end = androidx.compose.ui.geometry.Offset(gradientOffset + 500f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale.value)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.success
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛒",
                    fontSize = 60.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = "Champion Cart",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = textAlpha.value
                )
            )

            // Tagline
            Text(
                text = "Smart Savings, Every Day",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = textAlpha.value
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            LoadingDots(
                modifier = Modifier.scale(textAlpha.value)
            )
        }
    }
}

@Composable
private fun LoadingDots(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.8f at 0
                        1.2f at 300 + (index * 100)
                        0.8f at 600 + (index * 100)
                        0.8f at 1200
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        when (index) {
                            0 -> MaterialTheme.extendedColors.electricMint
                            1 -> MaterialTheme.extendedColors.cosmicPurple
                            else -> MaterialTheme.extendedColors.neonCoral
                        }
                    )
            )
        }
    }
}