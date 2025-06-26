package com.example.championcart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.championcart.R

/**
 * Champion Cart - Typography System
 * Electric Harmony design with Hebrew-first considerations
 * Following Material Design 3 with optimized mobile readability
 */

// Font Families - Primary Latin fonts
val SpaceGroteskFontFamily = FontFamily(
    Font(R.font.space_grotesk_light, FontWeight.Light),
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold)
)

val InterFontFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_black, FontWeight.Black)
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono_thin, FontWeight.Thin),
    Font(R.font.jetbrains_mono_extralight, FontWeight.ExtraLight),
    Font(R.font.jetbrains_mono_light, FontWeight.Light),
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_semibold, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold),
    Font(R.font.jetbrains_mono_extrabold, FontWeight.ExtraBold),
    Font(R.font.jetbrains_mono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.jetbrains_mono_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

// Font Families - Hebrew fonts
val HeeboFontFamily = FontFamily(
    Font(R.font.heebo_thin, FontWeight.Thin),
    Font(R.font.heebo_extralight, FontWeight.ExtraLight),
    Font(R.font.heebo_light, FontWeight.Light),
    Font(R.font.heebo_regular, FontWeight.Normal),
    Font(R.font.heebo_medium, FontWeight.Medium),
    Font(R.font.heebo_semibold, FontWeight.SemiBold),
    Font(R.font.heebo_bold, FontWeight.Bold),
    Font(R.font.heebo_extrabold, FontWeight.ExtraBold),
    Font(R.font.heebo_black, FontWeight.Black)
)

val RubikFontFamily = FontFamily(
    Font(R.font.rubik_light, FontWeight.Light),
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_semibold, FontWeight.SemiBold),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_extrabold, FontWeight.ExtraBold),
    Font(R.font.rubik_black, FontWeight.Black)
)

// Material3 Typography - FIXED: Mobile-optimized sizes
val Typography = Typography(
    // Display - Space Grotesk for impact (reduced sizes for mobile)
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(800),
        fontSize = 45.sp,  // FIXED: Was 57sp
        lineHeight = 52.sp,
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

    // Headlines - Inter Variable with proper weights
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
        letterSpacing = 0.1.sp
    ),

    // Labels - Inter Variable for UI elements
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

// Custom Typography Extensions
object ExtendedTypography {
    // Price Typography - JetBrains Mono for tabular numbers
    val priceDisplay = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )

    val priceLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val priceMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    val priceSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    // Hebrew Typography - Heebo for display, Rubik for body
    val hebrewDisplayLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight(900),
        fontSize = 48.sp,  // Slightly larger for Hebrew emphasis
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    )

    val hebrewHeadlineLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 26.sp,  // 10% larger than Latin
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    )

    val hebrewBodyLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight(400),
        fontSize = 17.sp,  // Slightly larger for readability
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    )

    // Marketing/Promotional Typography
    val promoDisplay = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight(800),
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1).sp
    )

    val dealBadge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(700),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    // UI Component Typography
    val buttonLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(600),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    val chipText = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    )

    val navLabel = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

// Typography Helper Functions
fun getHebrewTypography(): Typography {
    return Typography.copy(
        displayLarge = ExtendedTypography.hebrewDisplayLarge,
        headlineLarge = ExtendedTypography.hebrewHeadlineLarge,
        bodyLarge = ExtendedTypography.hebrewBodyLarge
    )
}

fun getPriceTypography(size: PriceSize): TextStyle {
    return when (size) {
        PriceSize.Display -> ExtendedTypography.priceDisplay
        PriceSize.Large -> ExtendedTypography.priceLarge
        PriceSize.Medium -> ExtendedTypography.priceMedium
        PriceSize.Small -> ExtendedTypography.priceSmall
    }
}

enum class PriceSize {
    Display, Large, Medium, Small
}