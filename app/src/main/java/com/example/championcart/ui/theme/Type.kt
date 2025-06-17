package com.example.championcart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.championcart.R

/**
 * Champion Cart - Modern Typography System
 * Variable fonts with expressive hierarchy
 */

@OptIn(ExperimentalTextApi::class)
// Space Grotesk - For display and headlines (Latin)
val SpaceGroteskFontFamily = FontFamily(
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(300)
        )
    ),
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    ),
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    )
)

// Heebo - For Hebrew display text
val HeeboFontFamily = FontFamily(
    Font(R.font.heebo_black, FontWeight.Black),
    Font(R.font.heebo_bold, FontWeight.Bold),
    Font(R.font.heebo_medium, FontWeight.Medium),
    Font(R.font.heebo_regular, FontWeight.Normal),
    Font(R.font.heebo_light, FontWeight.Light)
)

@OptIn(ExperimentalTextApi::class)
// Inter Variable - For body text (300-800 weight range)
val InterFontFamily = FontFamily(
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(300)
        )
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(800)
        )
    )
)

// Rubik - For Hebrew body text
val RubikFontFamily = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_light, FontWeight.Light)
)

@OptIn(ExperimentalTextApi::class)
// JetBrains Mono - For prices and numbers with tabular alignment
val JetBrainsMonoFontFamily = FontFamily(
    Font(
        R.font.jetbrains_mono_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        R.font.jetbrains_mono_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        R.font.jetbrains_mono_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    ),
    Font(
        R.font.jetbrains_mono_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    )
)

// Typography - Modern Expressive Hierarchy
val Typography = Typography(
    // Display - Hero text with variable weight
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // Headlines - Using Inter Variable
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // Titles - Using Inter Variable
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body text - Using Inter Variable
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Labels - Using Inter Variable
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Custom text styles for specific use cases
object AppTextStyles {
    // Hero text with dynamic weight
    val heroDisplay = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp,
        fontWeight = FontWeight(700)
    )

    // Hebrew display text
    val hebrewDisplay = TextStyle(
        fontFamily = HeeboFontFamily,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.Black
    )

    val hebrewHeadline = TextStyle(
        fontFamily = HeeboFontFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    )

    // Hebrew body text
    val hebrewBody = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    )

    val hebrewBodyMedium = TextStyle(
        fontFamily = RubikFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal
    )

    // Price displays with animated transitions
    val priceHero = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight(700),
        fontFeatureSettings = "tnum" // Tabular numbers
    )

    val priceLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight(600),
        fontFeatureSettings = "tnum"
    )

    val priceMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(500),
        fontFeatureSettings = "tnum"
    )

    val priceSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(500),
        fontFeatureSettings = "tnum"
    )

    // Product names with emphasis
    val productNameLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(600)
    )

    val productName = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight(500)
    )

    // Store names with branding
    val storeNameLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(600),
        letterSpacing = 0.5.sp
    )

    val storeName = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight(500),
        letterSpacing = 0.25.sp
    )

    // Button text styles
    val buttonLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(600),
        letterSpacing = 0.5.sp
    )

    val buttonMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(600),
        letterSpacing = 0.5.sp
    )

    val buttonSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight(600),
        letterSpacing = 0.5.sp
    )

    // Chip and badge text
    val chip = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight(500),
        letterSpacing = 0.25.sp
    )

    val badge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight(600),
        letterSpacing = 0.5.sp
    )

    // Input field styles
    val inputLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight(500),
        letterSpacing = 0.5.sp
    )

    val inputText = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(400),
        letterSpacing = 0.15.sp
    )

    val inputHint = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(400),
        letterSpacing = 0.15.sp
    )

    // Caption and supporting text
    val caption = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight(400),
        letterSpacing = 0.4.sp
    )

    val overline = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight(500),
        letterSpacing = 1.5.sp
    )

    // Special effects text
    val glowText = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(700),
        letterSpacing = 0.5.sp
    )

    object AppTextStyles {
        // Hero text with dynamic weight
        val heroDisplay = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = (-0.5).sp,
            fontWeight = FontWeight(700)
        )

        // Hebrew display text
        val hebrewDisplay = TextStyle(
            fontFamily = HeeboFontFamily,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Black
        )

        val hebrewHeadline = TextStyle(
            fontFamily = HeeboFontFamily,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold
        )

        // Hebrew body text
        val hebrewBody = TextStyle(
            fontFamily = RubikFontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        )

        val hebrewBodyMedium = TextStyle(
            fontFamily = RubikFontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        )

        // Hebrew support helper
        val hebrewText = hebrewBody

        // Price displays with animated transitions
        val priceHero = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight(700),
            fontFeatureSettings = "tnum" // Tabular numbers
        )

        val priceLarge = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight(600),
            fontFeatureSettings = "tnum"
        )

        val priceMedium = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            fontFeatureSettings = "tnum"
        )

        val priceSmall = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(500),
            fontFeatureSettings = "tnum"
        )

        // Aliases for price displays
        val priceDisplay = priceLarge
        val priceDisplayLarge = priceHero
        val priceDisplaySmall = priceSmall

        // Product names with emphasis
        val productNameLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(600)
        )

        val productName = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(500)
        )

        // Store names with branding
        val storeNameLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            letterSpacing = 0.5.sp
        )

        val storeName = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.25.sp
        )

        // Button text styles
        val buttonLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(600),
            letterSpacing = 0.5.sp
        )

        val buttonMedium = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            letterSpacing = 0.5.sp
        )

        val buttonSmall = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(600),
            letterSpacing = 0.5.sp
        )

        // MISSING STYLES ADDED:
        // Alias for navigation button text (referenced in BottomNavBar.kt)
        val buttonText = buttonMedium

        // Chip and badge text
        val chip = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.25.sp
        )

        val badge = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight(600),
            letterSpacing = 0.5.sp
        )

        // MISSING STYLES ADDED:
        // Alias for badge text (referenced in BottomNavBar.kt and PriceComponents.kt)
        val badgeText = badge

        // Input field styles
        val inputLabel = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.5.sp
        )

        val inputText = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.15.sp
        )

        val inputHint = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.15.sp
        )

        // Caption and supporting text
        val caption = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.4.sp
        )

        val overline = TextStyle(
            fontFamily = InterFontFamily,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 1.5.sp
        )

        // Special effects text
        val glowText = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(700),
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Helper function to detect if text should use Hebrew fonts
 */
fun TextStyle.withHebrewSupport(text: String): TextStyle {
    val hasHebrew = text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }

    return if (hasHebrew) {
        when {
            fontSize >= 24.sp -> copy(fontFamily = HeeboFontFamily)
            else -> copy(fontFamily = RubikFontFamily)
        }
    } else {
        this
    }
}