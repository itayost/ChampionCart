package com.example.championcart.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

/**
 * Champion Cart - Theme Preview System
 * Quick preview of all theme components for development
 */

class ThemePreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true) // Light and Dark
}

@Composable
fun ThemePreviewContainer(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ChampionCartTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Preview(name = "Colors - Light", showBackground = true)
@Preview(name = "Colors - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ColorPreview() {
    ThemePreviewContainer {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Electric Harmony Colors",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ColorSection("Brand Colors", brandColors)
            }

            item {
                ColorSection("Price Colors", priceColors)
            }

            item {
                ColorSection("Store Colors", storeColors)
            }

            item {
                ColorSection("Glass Effects", glassColors)
            }
        }
    }
}

@Composable
private fun ColorSection(title: String, colors: List<Pair<String, Color>>) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colors) { (name, color) ->
                ColorSwatch(name, color)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColorSwatch(name: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Text(
            name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(name = "Typography - Light", showBackground = true)
@Preview(name = "Typography - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TypographyPreview() {
    ThemePreviewContainer {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Typography System",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                TypographySection("Display", displayStyles)
            }

            item {
                TypographySection("Headlines", headlineStyles)
            }

            item {
                TypographySection("Body Text", bodyStyles)
            }

            item {
                TypographySection("Hebrew Text", hebrewStyles)
            }
        }
    }
}

@Composable
private fun TypographySection(
    title: String,
    styles: List<Pair<String, @Composable () -> Unit>>
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        styles.forEach { (name, content) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.3f)
                )
                Box(modifier = Modifier.weight(0.7f)) {
                    content()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(name = "Shapes", showBackground = true)
@Composable
fun ShapesPreview() {
    ThemePreviewContainer {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Shape System",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ShapeSection("Material Shapes", materialShapes)
            }

            item {
                ShapeSection("Glassmorphic Shapes", glassmorphicShapes)
            }
        }
    }
}

@Composable
private fun ShapeSection(title: String, shapes: List<Pair<String, RoundedCornerShape>>) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shapes) { (name, shape) ->
                ShapeSwatch(name, shape)
            }
        }
    }
}

@Composable
private fun ShapeSwatch(name: String, shape: RoundedCornerShape) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(shape)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
        )
        Text(
            name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(name = "Components", showBackground = true)
@Composable
fun ComponentsPreview() {
    ThemePreviewContainer {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Component Previews",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ComponentSection("Buttons") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}) { Text("Primary") }
                        OutlinedButton(onClick = {}) { Text("Outlined") }
                        TextButton(onClick = {}) { Text("Text") }
                    }
                }
            }

            item {
                ComponentSection("Cards") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            modifier = Modifier.size(100.dp, 60.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Card")
                            }
                        }

                        OutlinedCard(
                            modifier = Modifier.size(100.dp, 60.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Outlined")
                            }
                        }
                    }
                }
            }

            item {
                ComponentSection("Glass Effects") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(80.dp, 50.dp)
                                .glassmorphic(
                                    intensity = GlassIntensity.Light,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Light", style = MaterialTheme.typography.bodySmall)
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp, 50.dp)
                                .glassmorphic(
                                    intensity = GlassIntensity.Medium,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Medium", style = MaterialTheme.typography.bodySmall)
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp, 50.dp)
                                .glassmorphic(
                                    intensity = GlassIntensity.Heavy,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Heavy", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

// Preview data
private val brandColors = listOf(
    "Electric Mint" to BrandColors.ElectricMint,
    "Cosmic Purple" to BrandColors.CosmicPurple,
    "Deep Navy" to BrandColors.DeepNavy,
    "Neon Coral" to BrandColors.NeonCoral
)

private val priceColors = listOf(
    "Best Price" to BrandColors.BestPrice,
    "Mid Price" to BrandColors.MidPrice,
    "High Price" to BrandColors.HighPrice
)

private val storeColors = listOf(
    "Shufersal" to Color(0xFF0066CC),
    "Rami Levy" to Color(0xFFFF6B35),
    "Victory" to Color(0xFF8BC34A),
    "Mega" to Color(0xFFBA68C8)
)

private val glassColors = listOf(
    "Glass Light" to BrandColors.GlassLight,
    "Glass Medium" to BrandColors.GlassMedium,
    "Glass Heavy" to BrandColors.GlassHeavy
)

// Fixed: Properly define composable lambdas
private val displayStyles = listOf<Pair<String, @Composable () -> Unit>>(
    "Display Large" to { Text("Display Large", style = MaterialTheme.typography.displayLarge) },
    "Display Medium" to { Text("Display Medium", style = MaterialTheme.typography.displayMedium) },
    "Display Small" to { Text("Display Small", style = MaterialTheme.typography.displaySmall) }
)

private val headlineStyles = listOf<Pair<String, @Composable () -> Unit>>(
    "Headline Large" to { Text("Headline Large", style = MaterialTheme.typography.headlineLarge) },
    "Headline Medium" to { Text("Headline Medium", style = MaterialTheme.typography.headlineMedium) },
    "Headline Small" to { Text("Headline Small", style = MaterialTheme.typography.headlineSmall) }
)

private val bodyStyles = listOf<Pair<String, @Composable () -> Unit>>(
    "Body Large" to { Text("Body Large", style = MaterialTheme.typography.bodyLarge) },
    "Body Medium" to { Text("Body Medium", style = MaterialTheme.typography.bodyMedium) },
    "Body Small" to { Text("Body Small", style = MaterialTheme.typography.bodySmall) }
)

private val hebrewStyles = listOf<Pair<String, @Composable () -> Unit>>(
    "Hebrew Display" to {
        Text(
            "תצוגה עברית גדולה",
            style = MaterialTheme.typography.displayLarge,
            fontFamily = HeeboFontFamily
        )
    },
    "Hebrew Headline" to {
        Text(
            "כותרת עברית",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = HeeboFontFamily
        )
    },
    "Hebrew Body" to {
        Text(
            "טקסט גוף עברי רגיל",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = InterFontFamily
        )
    }
)

private val materialShapes = listOf(
    "Extra Small" to RoundedCornerShape(4.dp),
    "Small" to RoundedCornerShape(8.dp),
    "Medium" to RoundedCornerShape(16.dp),
    "Large" to RoundedCornerShape(24.dp),
    "Extra Large" to RoundedCornerShape(32.dp)
)

private val glassmorphicShapes = listOf(
    "Glass Card" to GlassmorphicShapes.GlassCard,
    "Button" to GlassmorphicShapes.Button,
    "Chip" to GlassmorphicShapes.Chip,
    "Search Field" to GlassmorphicShapes.SearchField,
    "Bottom Sheet" to GlassmorphicShapes.BottomSheet
)