package com.example.championcart.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Shape System - Simplified
 */

// Material3 Shapes
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Component shapes
object Shapes {
    // Cards
    val cardSmall = RoundedCornerShape(8.dp)
    val card = RoundedCornerShape(12.dp)
    val cardLarge = RoundedCornerShape(16.dp)

    // Buttons
    val button = RoundedCornerShape(24.dp)
    val buttonSmall = RoundedCornerShape(20.dp)
    val buttonSquare = RoundedCornerShape(12.dp)

    // Inputs
    val input = RoundedCornerShape(12.dp)
    val searchBar = RoundedCornerShape(28.dp)

    // Bottom sheet
    val bottomSheet = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Special
    val chip = RoundedCornerShape(16.dp)
    val badge = RoundedCornerShape(8.dp)
}