package com.example.championcart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.example.championcart.R

/**
 * Champion Cart - Hebrew-First Modern Typography System
 * Electric Harmony design with variable fonts and RTL support
 * Following 2025 mobile design trends with expressive hierarchy
 */

@OptIn(ExperimentalTextApi::class)
// Space Grotesk Variable - For display text and Latin headlines
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
    ),
    Font(
        R.font.space_grotesk_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(800)
        )
    )
)

// Heebo - Primary Hebrew display font
val HeeboFontFamily = FontFamily(
    Font(R.font.heebo_light, FontWeight.Light),
    Font(R.font.heebo_regular, FontWeight.Normal),
    Font(R.font.heebo_medium, FontWeight.Medium),
    Font(R.font.heebo_bold, FontWeight.Bold),
    Font(R.font.heebo_black, FontWeight.Black)
)

@OptIn(ExperimentalTextApi::class)
// Inter Variable - Primary body text font (300-800 weight range)
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

// Rubik - Hebrew body text font
val RubikFontFamily = FontFamily(
    Font(R.font.rubik_light, FontWeight.Light),
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold)
)

@OptIn(ExperimentalTextApi::class)
// JetBrains Mono Variable - For prices, numbers, and tabular data
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
    ),
    Font(
        R.font.jetbrains_mono_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(800)
        )
    )
)

// Material3 Typography - Electric Harmony Style (FIXED to match design system)
val Typography = Typography(
    // Display - FIXED sizes to match design system
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(800),
        fontSize = 48.sp,  // FIXED: Was 57sp, now matches Hero spec
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 36.sp,  // FIXED: Was 45sp
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 28.sp,  // FIXED: Was 36sp
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // Headlines - Inter Variable with proper weights (already correct)
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
        letterSpacing = 0.15.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // Titles - Inter Variable with medium weights
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 22.sp,
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

    // Body - Inter Variable with optimal readability
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Labels - Inter Variable with enhanced legibility
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

// Extended Typography System - Brand-Specific Styles
object AppTextStyles {
    // Hero Display - Maximum impact for onboarding and splash
    val heroDisplay = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(800),
        fontSize = 48.sp,  // Matches design system Hero spec
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    )

    val heroSubtitle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    // Hebrew Display Styles - First-class Hebrew support
    val hebrewDisplayLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.25).sp,
        textDirection = TextDirection.Rtl
    )

    val hebrewDisplayMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    )

    val hebrewHeadline = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    )

    val hebrewTitle = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    )

    // Hebrew Body Text - Rubik for optimal readability
    val hebrewBodyLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Rtl
    )

    val hebrewBodyMedium = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
        textDirection = TextDirection.Rtl
    )

    val hebrewBodySmall = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Rtl
    )

    // Price Display - JetBrains Mono for tabular numbers
    val priceHero = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = "tnum" // Tabular numbers for alignment
    )

    val priceLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum"
    )

    val priceMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum"
    )

    val priceSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum"
    )

    // Crossed out price (for original/sale prices)
    val priceCrossed = priceSmall.copy(
        fontWeight = FontWeight(400),
        color = Color(0xFF9E9E9E)
    )

    // Product & Store Names - Enhanced hierarchy
    val productNameLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    val productName = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    val productBrand = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val storeName = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    // UI Elements - Buttons, chips, badges
    val buttonText = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    val chipText = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp
    )

    val badge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )

    // Navigation & Headers
    val navigationLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

    val sectionHeader = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp
    )

    // Supporting Text
    val caption = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

    val overline = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    )

    // Special Effects - For glassmorphic design
    val glowText = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    // Aliases for backward compatibility
    val badgeText = badge
}

/**
 * Smart Hebrew Detection & Font Selection
 * Automatically applies Hebrew fonts when Hebrew characters are detected
 */
fun TextStyle.withSmartHebrewSupport(text: String): TextStyle {
    val hasHebrew = text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }

    return if (hasHebrew) {
        when {
            fontSize >= 24.sp -> copy(
                fontFamily = HeeboFontFamily,
                textDirection = TextDirection.Rtl
            )
            fontSize >= 16.sp -> copy(
                fontFamily = RubikFontFamily,
                textDirection = TextDirection.Rtl
            )
            else -> copy(
                fontFamily = RubikFontFamily,
                textDirection = TextDirection.Rtl
            )
        }
    } else {
        this
    }
}

/**
 * Apply appropriate font based on content language
 */
fun TextStyle.withContentLanguage(isHebrew: Boolean): TextStyle {
    return if (isHebrew) {
        when {
            fontSize >= 24.sp -> copy(
                fontFamily = HeeboFontFamily,
                textDirection = TextDirection.Rtl
            )
            else -> copy(
                fontFamily = RubikFontFamily,
                textDirection = TextDirection.Rtl
            )
        }
    } else {
        this
    }
}

/**
 * Price-specific text style with tabular numbers
 */
fun TextStyle.asPrice(): TextStyle = copy(
    fontFamily = JetBrainsMonoFontFamily,
    fontFeatureSettings = "tnum"
)

/**
 * Display text with maximum visual impact
 */
fun TextStyle.asDisplay(): TextStyle = copy(
    fontFamily = SpaceGroteskFontFamily,
    fontWeight = FontWeight(700)
)