package com.example.championcart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.championcart.ui.theme.*

@Composable
fun CityIndicator(
    city: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = ComponentShapes.Chip,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = city,
                // Use appropriate font based on whether the city name is in Hebrew
                style = if (isHebrewText(city)) {
                    AppTextStyles.hebrewText.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    // Use Assistant for English city names
                    MaterialTheme.typography.labelLarge
                },
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change city",
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

// Helper function to detect Hebrew text
private fun isHebrewText(text: String): Boolean {
    return text.any { char ->
        Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HEBREW
    }
}