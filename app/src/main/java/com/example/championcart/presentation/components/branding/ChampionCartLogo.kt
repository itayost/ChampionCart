package com.example.championcart.presentation.components.branding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.championcart.R
import com.example.championcart.ui.theme.*

/**
 * Reusable ChampionCart logo component that can be used throughout the app
 */
@Composable
fun ChampionCartLogo(
    modifier: Modifier = Modifier,
    size: LogoSize = LogoSize.Medium,
    variant: LogoVariant = LogoVariant.Full,
    showBackground: Boolean = true,
    backgroundColor: Color = BrandColors.ElectricMint
) {
    when (variant) {
        LogoVariant.Icon -> {
            LogoIcon(
                modifier = modifier,
                size = size.iconSize,
                showBackground = showBackground,
                backgroundColor = backgroundColor
            )
        }

        LogoVariant.Text -> {
            LogoText(
                modifier = modifier,
                size = size.textSize
            )
        }

        LogoVariant.Full -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoIcon(
                    size = size.iconSize,
                    showBackground = showBackground,
                    backgroundColor = backgroundColor
                )

                if (size != LogoSize.Small) {
                    Spacer(modifier = Modifier.height(size.spacing))

                    LogoText(
                        size = size.textSize
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    showBackground: Boolean = true,
    backgroundColor: Color = BrandColors.ElectricMint
) {
    if (showBackground) {
        Card(
            modifier = modifier.size(size),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (size > 40.dp) 8.dp else 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                backgroundColor,
                                backgroundColor.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_championcart),
                    contentDescription = "ChampionCart",
                    modifier = Modifier.size(size * 0.8f)
                )
            }
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.logo_championcart),
            contentDescription = "ChampionCart",
            modifier = modifier.size(size)
        )
    }
}

@Composable
private fun LogoText(
    modifier: Modifier = Modifier,
    size: Dp
) {
    Text(
        text = "ChampionCart",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.2f).sp
        ),
        color = BrandColors.ElectricMint
    )
}

/**
 * Available logo sizes with predefined proportions
 */
enum class LogoSize(
    val iconSize: Dp,
    val textSize: Dp,
    val spacing: Dp
) {
    Small(24.dp, 12.dp, 4.dp),
    Medium(48.dp, 24.dp, 8.dp),
    Large(72.dp, 36.dp, 12.dp),
    XLarge(96.dp, 48.dp, 16.dp),
    XXLarge(120.dp, 60.dp, 20.dp)
}

/**
 * Logo display variants
 */
enum class LogoVariant {
    Icon,        // Just the shopping cart icon
    Text,        // Just the "ChampionCart" text
    Full         // Icon + Text (vertical stack)
}

/**
 * Preview composable for logo variants
 */
@Composable
fun LogoShowcase() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Logo Variants",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChampionCartLogo(
                variant = LogoVariant.Icon,
                size = LogoSize.Medium
            )

            ChampionCartLogo(
                variant = LogoVariant.Text,
                size = LogoSize.Medium
            )

            ChampionCartLogo(
                variant = LogoVariant.Full,
                size = LogoSize.Small
            )
        }

        Text(
            text = "Size Variants",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChampionCartLogo(size = LogoSize.Small)
            ChampionCartLogo(size = LogoSize.Medium)
            ChampionCartLogo(size = LogoSize.Large)
        }
    }
}