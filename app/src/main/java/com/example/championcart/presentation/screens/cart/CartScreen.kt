package com.example.championcart.presentation.screens.cart

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.championcart.ui.theme.*

/**
 * Champion Cart - Cart Screen (Coming Soon)
 * Placeholder screen while cart functionality is being developed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "העגלה שלי",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "חזור"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            ComingSoonContent()
        }
    }
}

@Composable
private fun ComingSoonContent() {
    val infiniteTransition = rememberInfiniteTransition()

    // Floating animation for the icon
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Scale animation for the badge
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingTokens.XL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
    ) {
        // Animated icon container
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(y = floatAnimation.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Coming Soon text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            Text(
                text = "בקרוב!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "אנחנו עובדים על תכונת העגלה החכמה",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Feature preview badge
        Surface(
            modifier = Modifier.scale(scaleAnimation),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = SpacingTokens.L,
                    vertical = SpacingTokens.M
                ),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(SizingTokens.IconS),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "השוואת מחירים אוטומטית",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        // Feature list
        Column(
            modifier = Modifier.padding(top = SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Text(
                text = "מה מחכה לך:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = SpacingTokens.S)
            )

            ComingSoonFeature(
                icon = Icons.Default.CompareArrows,
                text = "השוואת מחירים בין כל החנויות"
            )

            ComingSoonFeature(
                icon = Icons.Default.Savings,
                text = "מציאת העגלה הזולה ביותר"
            )

            ComingSoonFeature(
                icon = Icons.Default.LocationOn,
                text = "חנויות קרובות למיקום שלך"
            )

            ComingSoonFeature(
                icon = Icons.Default.Analytics,
                text = "ניתוח חיסכון חכם"
            )
        }
    }
}

@Composable
private fun ComingSoonFeature(
    icon: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(SizingTokens.IconM),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}