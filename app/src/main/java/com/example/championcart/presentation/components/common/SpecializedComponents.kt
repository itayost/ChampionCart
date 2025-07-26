package com.example.championcart.presentation.components.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Padding
import com.example.championcart.ui.theme.SemanticColors
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing
import com.example.championcart.ui.theme.TextStyles
import com.example.championcart.ui.theme.glass

/**
 * Specialized components for ChampionCart features
 */

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier
            .glass(
                shape = Shapes.button,
                elevation = 0.dp
            )
            .padding(Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (quantity > minQuantity) onQuantityChange(quantity - 1)
            },
            enabled = quantity > minQuantity,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "הפחת",
                modifier = Modifier.size(20.dp),
                tint = if (quantity > minQuantity) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }

        AnimatedContent(
            targetState = quantity,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "quantity"
        ) { targetQuantity ->
            Text(
                text = targetQuantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.widthIn(min = 32.dp),
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = {
                if (quantity < maxQuantity) onQuantityChange(quantity + 1)
            },
            enabled = quantity < maxQuantity,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "הוסף",
                modifier = Modifier.size(20.dp),
                tint = if (quantity < maxQuantity) {
                    BrandColors.ElectricMint
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

@Composable
fun CartSummaryCard(
    itemCount: Int,
    totalPrice: String,
    savings: String? = null,
    onFindBestStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.cardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "סה״כ בעגלה",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$itemCount פריטים",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "טווח מחירים:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalPrice,
                        style = TextStyles.priceLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                savings?.let {
                    Box(
                        modifier = Modifier
                            .background(
                                color = SemanticColors.Success.copy(alpha = 0.12f),
                                shape = Shapes.badge
                            )
                            .padding(horizontal = Spacing.m, vertical = Spacing.s)
                    ) {
                        Text(
                            text = "חיסכון: $it",
                            style = MaterialTheme.typography.labelMedium,
                            color = SemanticColors.Success,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            PrimaryButton(
                text = "מצא את החנות הזולה",
                onClick = onFindBestStore,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Rounded.TrendingDown
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trend: Float? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    shape = Shapes.card,
                    elevation = 0.dp
                )
                .padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(Size.icon),
                    tint = BrandColors.ElectricMint
                )

                trend?.let {
                    val isPositive = it > 0
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPositive) {
                                Icons.AutoMirrored.Rounded.TrendingUp
                            } else {
                                Icons.AutoMirrored.Rounded.TrendingDown
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isPositive) {
                                SemanticColors.Success
                            } else {
                                SemanticColors.Error
                            }
                        )
                        Text(
                            text = "${kotlin.math.abs(it)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPositive) {
                                SemanticColors.Success
                            } else {
                                SemanticColors.Error
                            }
                        )
                    }
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryCard(
    name: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(100.dp)
            .clickable { onClick() },
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelector(
    selectedCity: String,
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    text = selectedCity,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.menuAnchor(),
            shape = Shapes.chip,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    },
                    leadingIcon = if (city == selectedCity) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = BrandColors.ElectricMint
                            )
                        }
                    } else null
                )
            }
        }
    }
}