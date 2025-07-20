package com.example.championcart.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LocationDetectedAnimation(
    visible: Boolean,
    detectedCity: String,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        LaunchedEffect(Unit) {
            delay(2000) // Show for 2 seconds
            onAnimationComplete()
        }

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(Spacing.l)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.l)
                ) {
                    // Animated checkmark
                    AnimatedCheckmark()

                    Text(
                        text = "העיר זוהתה בהצלחה!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SemanticColors.Success
                    )

                    Text(
                        text = detectedCity,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedCheckmark(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "checkmark")
    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "checkmark_progress"
    )

    Icon(
        imageVector = Icons.Rounded.CheckCircle,
        contentDescription = null,
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                scaleX = animationProgress
                scaleY = animationProgress
                alpha = animationProgress
            },
        tint = SemanticColors.Success
    )
}