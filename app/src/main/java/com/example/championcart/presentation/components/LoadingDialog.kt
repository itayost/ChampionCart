package com.example.championcart.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.GlassIntensity
import com.example.championcart.ui.theme.GlassmorphicShapes
import com.example.championcart.ui.theme.LocalExtendedColors
import com.example.championcart.ui.theme.glassmorphic

/**
 * Champion Cart - Loading Dialog
 * Glassmorphic loading indicator with Electric Harmony colors
 */

@Composable
fun LoadingDialog(
    isLoading: Boolean,
    message: String = "Loading...",
    onDismissRequest: (() -> Unit)? = null
) {
    if (isLoading) {
        val colors = LocalExtendedColors.current

        Dialog(
            onDismissRequest = onDismissRequest ?: {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = GlassmorphicShapes.Dialog,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated loading dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            LoadingDot(
                                index = index,
                                color = when (index) {
                                    0 -> colors.electricMint
                                    1 -> colors.cosmicPurple
                                    else -> colors.neonCoral
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingDot(
    index: Int,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

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

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.5f at 0
                1f at 300 + (index * 100)
                0.5f at 600 + (index * 100)
                0.5f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * Inline loading indicator for use within screens
 */
@Composable
fun InlineLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val colors = LocalExtendedColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = colors.electricMint,
            strokeWidth = 3.dp
        )

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Skeleton loading placeholder
 */
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = GlassmorphicShapes.GlassCard
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")

    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                shimmerBrush(
                    shimmerTranslateAnim,
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surfaceVariant
                )
            )
    )
}

/**
 * Shimmer brush for skeleton loading
 */
@Composable
fun shimmerBrush(
    translateAnim: Float,
    baseColor: Color,
    highlightColor: Color
): androidx.compose.ui.graphics.Brush {
    return androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = androidx.compose.ui.geometry.Offset(translateAnim - 1000f, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
    )
}

/**
 * Full screen loading overlay
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .glassmorphic(
                        intensity = GlassIntensity.Ultra,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = GlassmorphicShapes.Dialog,
                    colors = CardDefaults.cardColors(
                        containerColor = LocalExtendedColors.current.surfaceGlass
                    )
                ) {
                    Box(
                        modifier = Modifier.padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LocalExtendedColors.current.electricMint
                        )
                    }
                }
            }
        }
    }
}