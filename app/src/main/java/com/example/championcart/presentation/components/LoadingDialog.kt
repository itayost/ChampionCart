package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.championcart.ui.theme.ComponentShapes
import com.example.championcart.ui.theme.Dimensions
import com.example.championcart.ui.theme.extendedColors

@Composable
fun LoadingDialog(
    message: String = "Loading..."
) {
    Dialog(onDismissRequest = {}) {
        LoadingContent(message = message)
    }
}

@Composable
fun LoadingContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = ComponentShapes.Dialog,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationLarge
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Dimensions.dialogPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Modern loading animation with 3 dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    LoadingDot(
                        index = index,
                        color = when (index) {
                            0 -> MaterialTheme.extendedColors.electricMint
                            1 -> MaterialTheme.extendedColors.cosmicPurple
                            else -> MaterialTheme.extendedColors.neonCoral
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
            .size(16.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                color = color.copy(alpha = alpha)
            )
    )
}

// Alternative loading indicator with gradient shimmer
@Composable
fun ShimmerLoadingCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Card(
        modifier = modifier,
        shape = ComponentShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.extendedColors.glass,
                            Color.Transparent
                        ),
                        start = androidx.compose.ui.geometry.Offset(
                            shimmerTranslateAnim - 500f,
                            0f
                        ),
                        end = androidx.compose.ui.geometry.Offset(
                            shimmerTranslateAnim,
                            0f
                        )
                    )
                )
        )
    }
}