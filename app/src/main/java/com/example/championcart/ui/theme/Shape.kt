package com.example.championcart.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Champion Cart Shape System
 * Glassmorphic shapes for Electric Harmony design
 */

// Material3 Shapes
val ChampionCartShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Component-specific shapes
object ComponentShapes {
    // Cards
    object Card {
        val Small = RoundedCornerShape(12.dp)
        val Medium = RoundedCornerShape(16.dp)
        val Large = RoundedCornerShape(20.dp)
        val ExtraLarge = RoundedCornerShape(24.dp)
        val Hero = RoundedCornerShape(28.dp)
    }

    // Buttons
    object Button {
        val Small = RoundedCornerShape(18.dp)      // 36dp height
        val Medium = RoundedCornerShape(24.dp)     // 48dp height
        val Large = RoundedCornerShape(28.dp)      // 56dp height
        val Pill = RoundedCornerShape(50)          // Full pill
        val Square = RoundedCornerShape(12.dp)     // Squircle
    }

    // Input Fields
    object Input {
        val Default = RoundedCornerShape(12.dp)
        val Small = RoundedCornerShape(8.dp)
        val Large = RoundedCornerShape(16.dp)
        val Search = RoundedCornerShape(28.dp)     // Pill search
    }

    // Navigation
    object Navigation {
        val BottomBar = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        val BottomNavigation = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        val TopBar = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 20.dp,
            bottomEnd = 20.dp
        )
    }

    // Bottom Sheets & Modals
    object Sheet {
        val Bottom = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        val Modal = RoundedCornerShape(24.dp)
        val Dialog = RoundedCornerShape(20.dp)
    }

    // Product & Store specific
    object Product {
        val Card = RoundedCornerShape(16.dp)
        val Image = RoundedCornerShape(12.dp)
        val Badge = RoundedCornerShape(8.dp)
    }

    object Store {
        val Card = RoundedCornerShape(12.dp)
        val Logo = RoundedCornerShape(8.dp)
    }

    // Special
    object Special {
        val Chip = RoundedCornerShape(16.dp)
        val Tag = RoundedCornerShape(6.dp)
        val Indicator = RoundedCornerShape(2.dp)
        val FAB = RoundedCornerShape(16.dp)
        val Badge = RoundedCornerShape(8.dp)
    }
}

// Price-specific shapes
object PriceShapes {
    val Container = RoundedCornerShape(8.dp)
    val Badge = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 0.dp,
        bottomStart = 8.dp,
        bottomEnd = 0.dp
    )
    val Comparison = RoundedCornerShape(12.dp)
}

// Glass effect shapes
object GlassShapes {
    val Light = RoundedCornerShape(12.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Heavy = RoundedCornerShape(20.dp)
    val Ultra = RoundedCornerShape(24.dp)
}

// Special corner treatments
object SpecialShapes {
    // One-sided rounded
    val RoundedTop = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val RoundedBottom = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    val RoundedStart = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )

    val RoundedEnd = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 16.dp
    )

    // Diagonal cuts
    val CutTopEnd = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val CutBottomStart = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )
}