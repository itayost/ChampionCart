package com.example.championcart.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.utils.shimmerEffect
import com.example.championcart.ui.theme.*

/**
 * Empty state component for when no data is available
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.Inbox,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    message: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon background
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.2f),
                            MaterialTheme.extendedColors.electricMint.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.extendedColors.electricMint
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Subtitle
        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Action button
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAction,
                shape = ComponentShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.extendedColors.electricMint
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Error state component for when something goes wrong
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition(label = "error").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon with animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    MaterialTheme.extendedColors.errorRed.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.extendedColors.errorRed
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error title
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Retry button
        Button(
            onClick = onRetry,
            shape = ComponentShapes.Button,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.electricMint
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

/**
 * Loading skeleton for list items
 */
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = ComponentShapes.Card
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }
        }
    }
}

/**
 * No network connection state
 */
@Composable
fun NoConnectionState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "No Internet Connection",
        subtitle = "Please check your connection and try again",
        icon = Icons.Default.WifiOff,
        actionLabel = "Retry",
        onAction = onRetry,
        modifier = modifier,
        message = "You haven't saved any shopping carts yet. Start shopping and save your carts for later!"
    )
}