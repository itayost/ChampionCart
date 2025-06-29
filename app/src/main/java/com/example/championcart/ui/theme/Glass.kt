package com.example.championcart.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Simplified Glass Effects
 * Clean glassmorphic design without complex dependencies
 */

@Composable
fun Modifier.glass(
    shape: Shape = Shapes.card,
    elevation: Dp = 2.dp,
    borderWidth: Dp = 1.dp,
    darkTheme: Boolean = isSystemInDarkTheme()
): Modifier = composed {
    val backgroundColor = if (darkTheme) {
        GlassColors.Medium
    } else {
        Color.White.copy(alpha = 0.9f)
    }

    val borderColor = if (darkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    this
        .shadow(
            elevation = elevation,
            shape = shape,
            spotColor = if (darkTheme) {
                BrandColors.ElectricMint.copy(alpha = 0.1f)
            } else {
                Color.Black.copy(alpha = 0.05f)
            }
        )
        .clip(shape)
        .background(backgroundColor)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
}

@Composable
fun Modifier.priceGlass(
    priceLevel: PriceLevel,
    shape: Shape = Shapes.badge
): Modifier = composed {
    val color = when (priceLevel) {
        PriceLevel.Best -> PriceColors.Best
        PriceLevel.Mid -> PriceColors.Mid
        PriceLevel.High -> PriceColors.High
    }

    this
        .clip(shape)
        .background(
            color = if (isSystemInDarkTheme()) {
                color.copy(alpha = 0.15f)
            } else {
                Color.White.copy(alpha = 0.95f)
            }
        )
        .border(
            width = 1.5.dp,
            color = color.copy(alpha = 0.4f),
            shape = shape
        )
}

enum class PriceLevel { Best, Mid, High }