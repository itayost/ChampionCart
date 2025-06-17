package com.example.championcart.presentation.screens.splash

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.championcart.R
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

/**
 * Modern Splash Screen with Electric Harmony Design
 * Features animated logo, floating orbs, and smooth transitions
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_fold"
)
@Composable
fun ModernSplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Start animation after composition
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000) // Show splash for 3 seconds
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.deepNavy,
                        MaterialTheme.colorScheme.extended.deepNavyVariant,
                        Color(0xFF0A0E27) // Even darker at bottom
                    )
                )
            )
    ) {
        // Animated background orbs
        AnimatedBackgroundOrbs(infiniteTransition)

        // Center content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo
            AnimatedLogo(startAnimation)

            Spacer(modifier = Modifier.height(48.dp))

            // App name with animated appearance
            AnimatedAppName(startAnimation)

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            AnimatedTagline(startAnimation)

            Spacer(modifier = Modifier.height(80.dp))

            // Loading indicator
            AnimatedLoadingIndicator(startAnimation)
        }

        // Bottom gradient fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0A0E27)
                        )
                    )
                )
        )
    }
}

/**
 * Animated background orbs for visual interest
 */
@Composable
private fun AnimatedBackgroundOrbs(infiniteTransition: InfiniteTransition) {
    // Orb 1 - Electric Mint
    val orb1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1Y"
    )

    val orb1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1Scale"
    )

    Box(
        modifier = Modifier
            .offset(x = (-50).dp, y = orb1Y.dp)
            .size(300.dp)
            .scale(orb1Scale)
            .graphicsLayer { alpha = 0.3f }
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    )

    // Orb 2 - Cosmic Purple
    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2Y"
    )

    Box(
        modifier = Modifier
            .offset(x = 100.dp, y = orb2Y.dp + 200.dp)
            .size(250.dp)
            .graphicsLayer { alpha = 0.25f }
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    )

    // Orb 3 - Neon Coral (smaller accent)
    val orb3X by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3X"
    )

    Box(
        modifier = Modifier
            .offset(x = orb3X.dp, y = 400.dp)
            .size(150.dp)
            .graphicsLayer { alpha = 0.2f }
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.neonCoral.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.extended.neonCoral.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Animated logo with shopping cart filling animation
 */
@Composable
private fun AnimatedLogo(startAnimation: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -180f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logoRotation"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .rotate(rotation)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.electricMint,
                        MaterialTheme.colorScheme.extended.cosmicPurple
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Shopping cart icon with fill animation
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Add a subtle pulsing effect
                    val pulseScale = 1f + (scale - 1f) * 0.1f
                    scaleX = pulseScale
                    scaleY = pulseScale
                },
            tint = Color.White
        )
    }
}

/**
 * Animated app name
 */
@Composable
private fun AnimatedAppName(startAnimation: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 500),
        label = "nameAlpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nameOffset"
    )

    Column(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .graphicsLayer { this.alpha = alpha },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Champion",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            ),
            color = Color.White
        )
        Text(
            text = "Cart",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.extended.electricMint
        )
    }
}

/**
 * Animated tagline
 */
@Composable
private fun AnimatedTagline(startAnimation: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 1000),
        label = "taglineAlpha"
    )

    Text(
        text = "חסכון חכם בכל קנייה",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 18.sp,
            letterSpacing = 1.sp
        ),
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.graphicsLayer { this.alpha = alpha }
    )
}

/**
 * Animated loading indicator
 */
@Composable
private fun AnimatedLoadingIndicator(startAnimation: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500, delayMillis = 1500),
        label = "loadingAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                1f at 300
                0.3f at 600
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                0.3f at 300
                1f at 600
                0.3f at 900
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                0.3f at 600
                1f at 900
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3"
    )

    Row(
        modifier = Modifier.graphicsLayer { this.alpha = alpha },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LoadingDot(alpha = dot1Alpha)
        LoadingDot(alpha = dot2Alpha)
        LoadingDot(alpha = dot3Alpha)
    }
}

@Composable
private fun LoadingDot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.extended.electricMint.copy(alpha = alpha)
            )
    )
}