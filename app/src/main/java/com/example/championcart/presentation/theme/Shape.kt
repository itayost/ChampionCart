package com.example.championcart.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Component-specific shapes
object ComponentShapes {
    val Card = RoundedCornerShape(12.dp)
    val Button = RoundedCornerShape(8.dp)
    val TextField = RoundedCornerShape(8.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val Dialog = RoundedCornerShape(16.dp)
    val SearchBar = RoundedCornerShape(28.dp)
    val Chip = RoundedCornerShape(16.dp)
    val Badge = RoundedCornerShape(4.dp)  // Added Badge shape
    val PriceTag = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 12.dp,
        bottomEnd = 12.dp,
        bottomStart = 4.dp
    )
}