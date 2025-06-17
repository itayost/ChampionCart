package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.championcart.R
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Empty state types for different scenarios
 */
enum class EmptyStateType {
    NO_RESULTS,
    EMPTY_CART,
    NO_SAVED_CARTS,
    NO_PRICE_DATA,
    NETWORK_ERROR,
    LOCATION_ERROR,
    NO_STORES_NEARBY,
    NO_DEALS,
    FIRST_TIME,
    NO_HISTORY
}

/**
 * Main empty state composable with animations
 */
@Composable
fun EmptyState(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val defaultConfig = getEmptyStateConfig(type)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            // Animated illustration
            EmptyStateAnimation(type)

            // Text content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
            ) {
                Text(
                    text = title ?: defaultConfig.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } ?: defaultConfig.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Action button
            if (actionLabel != null || defaultConfig.actionLabel != null) {
                Spacer(modifier = Modifier.height(SpacingTokens.M))

                Button(
                    onClick = onAction ?: defaultConfig.onAction ?: {},
                    shape = GlassmorphicShapes.Button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.extended.electricMint
                    )
                ) {
                    Text(
                        text = actionLabel ?: defaultConfig.actionLabel ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Animated illustrations for each empty state type
 */
@Composable
private fun EmptyStateAnimation(type: EmptyStateType) {
    when (type) {
        EmptyStateType.NO_RESULTS -> NoResultsAnimation()
        EmptyStateType.EMPTY_CART -> EmptyCartAnimation()
        EmptyStateType.NO_SAVED_CARTS -> NoSavedCartsAnimation()
        EmptyStateType.NO_PRICE_DATA -> NoPriceDataAnimation()
        EmptyStateType.NETWORK_ERROR -> NetworkErrorAnimation()
        EmptyStateType.LOCATION_ERROR -> LocationErrorAnimation()
        EmptyStateType.NO_STORES_NEARBY -> NoStoresAnimation()
        EmptyStateType.NO_DEALS -> NoDealsAnimation()
        EmptyStateType.FIRST_TIME -> FirstTimeAnimation()
        EmptyStateType.NO_HISTORY -> NoHistoryAnimation()
    }
}

/**
 * No search results animation
 */
@Composable
private fun NoResultsAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "search")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle with gradient
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Magnifying glass
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation),
            tint = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.8f)
        )

        // Floating question marks
        FloatingElements(
            icons = listOf(Icons.Default.QuestionMark),
            count = 3,
            tint = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.6f)
        )
    }
}

/**
 * Empty cart animation
 */
@Composable
private fun EmptyCartAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "cart")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0 with FastOutSlowInEasing
                -20f at 500 with FastOutSlowInEasing
                0f at 1000 with FastOutSlowInEasing
                0f at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounce"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp)
                .size(120.dp, 20.dp)
                .scale(scaleX = 1f - (bounce / -40f), scaleY = 1f)
                .clip(CircleShape)
                .background(
                    Color.Black.copy(alpha = 0.1f * (1f - (bounce / -40f)))
                )
        )

        // Cart
        Icon(
            Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .offset(y = bounce.dp)
                .graphicsLayer {
                    rotationZ = bounce / 4f
                },
            tint = MaterialTheme.colorScheme.extended.surfaceGlass
        )

        // Tumbleweeds
        TumbleweedAnimation()
    }
}

/**
 * No saved carts animation
 */
@Composable
private fun NoSavedCartsAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "bookmark")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing background
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(pulse)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f)
                )
        )

        // Bookmark icon
        Icon(
            Icons.Outlined.BookmarkBorder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.extended.cosmicPurple
        )

        // Floating plus signs
        FloatingElements(
            icons = listOf(Icons.Default.Add),
            count = 4,
            tint = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.6f),
            rotationSpeed = 5000
        )
    }
}

/**
 * Network error animation
 */
@Composable
private fun NetworkErrorAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "network")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Disconnected cloud
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .offset(y = offset.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
        )

        // Signal waves (disconnected)
        DisconnectedSignalWaves()
    }
}

/**
 * Location error animation
 */
@Composable
private fun LocationErrorAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "location")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating compass background
        Icon(
            Icons.Default.Explore,
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .rotate(rotation)
                .graphicsLayer { alpha = 0.2f },
            tint = MaterialTheme.colorScheme.extended.electricMint
        )

        // Lost location pin
        Icon(
            Icons.Default.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )

        // Question marks around
        FloatingElements(
            icons = listOf(Icons.Default.QuestionMark),
            count = 4,
            tint = MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.6f),
            radius = 80.dp
        )
    }
}

/**
 * No stores nearby animation
 */
@Composable
private fun NoStoresAnimation() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Map circle
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.extended.surfaceGlass
                )
        )

        // Store icon in center
        Icon(
            Icons.Default.Store,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        // Expanding radar circles
        RadarAnimation()
    }
}

/**
 * No deals animation
 */
@Composable
private fun NoDealsAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "deals")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sale tag
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .rotate(-15f)
                .clip(GlassmorphicShapes.GlassCard)
                .background(
                    MaterialTheme.colorScheme.extended.surfaceGlass
                )
        ) {
            Icon(
                Icons.Default.LocalOffer,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        // Crossed out percentage
        Text(
            text = "%",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            modifier = Modifier
                .graphicsLayer {
                    // Strike through effect
                    scaleX = 1.2f
                }
        )
    }
}

/**
 * First time user animation
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FirstTimeAnimation() {
    var currentStep by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentStep = (currentStep + 1) % 3
        }
    }

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Animated tutorial steps
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn() + scaleIn() with fadeOut() + scaleOut()
            },
            label = "tutorial"
        ) { step ->
            when (step) {
                0 -> Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.extended.electricMint
                )
                1 -> Icon(
                    Icons.Default.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.extended.cosmicPurple
                )
                2 -> Icon(
                    Icons.Default.Savings,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = BrandColors.Success
                )
            }
        }

        // Progress dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentStep) {
                                MaterialTheme.colorScheme.extended.electricMint
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            }
                        )
                )
            }
        }
    }
}

/**
 * No history animation
 */
@Composable
private fun NoHistoryAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "history")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Clock face
        Icon(
            Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation),
            tint = MaterialTheme.colorScheme.extended.surfaceGlass
        )

        // Empty hourglass
        Icon(
            Icons.Default.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/**
 * No price data animation
 */
@Composable
private fun NoPriceDataAnimation() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Price tag with question mark
        Box(
            modifier = Modifier
                .size(140.dp, 100.dp)
                .clip(GlassmorphicShapes.GlassCard)
                .background(MaterialTheme.colorScheme.extended.surfaceGlass)
        ) {
            Text(
                text = "₪?",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Floating question marks
        FloatingElements(
            icons = listOf(Icons.Default.QuestionMark),
            count = 3,
            tint = MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.4f)
        )
    }
}

/**
 * Helper composables for animations
 */

@Composable
private fun FloatingElements(
    icons: List<ImageVector>,
    count: Int,
    tint: Color,
    radius: Dp = 60.dp,
    rotationSpeed: Int = 10000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationSpeed, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    icons.forEachIndexed { index, icon ->
        val angle = (360f / count) * index
        val offsetX = kotlin.math.cos(Math.toRadians(angle.toDouble() + rotation)) * radius.value
        val offsetY = kotlin.math.sin(Math.toRadians(angle.toDouble() + rotation)) * radius.value

        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .offset(x = offsetX.dp, y = offsetY.dp),
            tint = tint
        )
    }
}

@Composable
private fun TumbleweedAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "tumbleweed")

    val offset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(30.dp)
            .offset(x = offset.dp, y = 50.dp)
            .rotate(rotation)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
    )
}

@Composable
private fun RadarAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    repeat(3) { index ->
        val delay = index * 1000
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "radar_$index"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha_$index"
        )

        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.extended.electricMint.copy(alpha = alpha * 0.3f)
                )
        )
    }
}

@Composable
private fun DisconnectedSignalWaves() {
    Row(
        modifier = Modifier.offset(y = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val height = (index + 1) * 10
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .clip(GlassmorphicShapes.Chip)
                    .background(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

/**
 * Configuration for empty states
 */
private data class EmptyStateConfig(
    val title: String,
    val subtitle: String? = null,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

@Composable
private fun getEmptyStateConfig(type: EmptyStateType): EmptyStateConfig {
    return when (type) {
        EmptyStateType.NO_RESULTS -> EmptyStateConfig(
            title = "אין תוצאות",
            subtitle = "נסה לחפש משהו אחר או לשנות את הפילטרים",
            actionLabel = "נקה חיפוש"
        )
        EmptyStateType.EMPTY_CART -> EmptyStateConfig(
            title = "העגלה ריקה",
            subtitle = "הוסף מוצרים כדי להתחיל לחסוך",
            actionLabel = "התחל קניות"
        )
        EmptyStateType.NO_SAVED_CARTS -> EmptyStateConfig(
            title = "אין עגלות שמורות",
            subtitle = "שמור את העגלה שלך כדי לקנות שוב בקלות",
            actionLabel = "צור עגלה חדשה"
        )
        EmptyStateType.NO_PRICE_DATA -> EmptyStateConfig(
            title = "אין נתוני מחירים",
            subtitle = "לא הצלחנו למצוא מחירים עבור מוצר זה",
            actionLabel = "רענן"
        )
        EmptyStateType.NETWORK_ERROR -> EmptyStateConfig(
            title = "אין חיבור לרשת",
            subtitle = "בדוק את החיבור לאינטרנט ונסה שוב",
            actionLabel = "נסה שוב"
        )
        EmptyStateType.LOCATION_ERROR -> EmptyStateConfig(
            title = "לא ניתן לזהות מיקום",
            subtitle = "אפשר גישה למיקום כדי למצוא חנויות קרובות",
            actionLabel = "הגדרות מיקום"
        )
        EmptyStateType.NO_STORES_NEARBY -> EmptyStateConfig(
            title = "אין חנויות באזור",
            subtitle = "נסה להרחיב את טווח החיפוש",
            actionLabel = "שנה מיקום"
        )
        EmptyStateType.NO_DEALS -> EmptyStateConfig(
            title = "אין מבצעים כרגע",
            subtitle = "בדוק שוב מאוחר יותר למבצעים חדשים",
            actionLabel = "הגדר התראות"
        )
        EmptyStateType.FIRST_TIME -> EmptyStateConfig(
            title = "ברוך הבא!",
            subtitle = "בוא נתחיל לחסוך כסף ביחד",
            actionLabel = "התחל סיור"
        )
        EmptyStateType.NO_HISTORY -> EmptyStateConfig(
            title = "אין היסטוריה",
            subtitle = "ההיסטוריה שלך תופיע כאן",
            actionLabel = null
        )
    }
}