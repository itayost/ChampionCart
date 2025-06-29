package com.example.championcart.presentation.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.screens.auth.SocialProvider
import com.example.championcart.ui.theme.*

/**
 * Authentication-specific components
 */

@Composable
fun SocialLoginButton(
    text: String,
    provider: SocialProvider,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val (icon, containerColor, contentColor) = when (provider) {
        SocialProvider.Google -> Triple(
            Icons.Rounded.Language, // Placeholder for Google icon
            Color.White,
            Color(0xFF4285F4)
        )
        SocialProvider.Facebook -> Triple(
            Icons.Rounded.Facebook,
            Color(0xFF1877F2),
            Color.White
        )
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Size.buttonHeight),
        enabled = enabled,
        shape = Shapes.button,
        border = BorderStroke(
            width = 1.dp,
            color = if (provider == SocialProvider.Google) {
                MaterialTheme.colorScheme.outline
            } else {
                containerColor
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (provider == SocialProvider.Google) {
                Color.White
            } else {
                containerColor
            },
            contentColor = contentColor
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.s))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandColors.ElectricMint
                )
            )

            Column(
                modifier = Modifier.padding(start = Spacing.s)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "אני מסכים/ה ל",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = onTermsClick,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            text = "תנאי השימוש",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrandColors.ElectricMint,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "ול",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = onPrivacyClick,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            text = "מדיניות הפרטיות",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrandColors.ElectricMint,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                error?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = SemanticColors.Error,
                        modifier = Modifier.padding(top = Spacing.xs)
                    )
                }
            }
        }
    }
}

@Composable
fun GuestModeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.l),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.PersonOutline,
                    contentDescription = null,
                    modifier = Modifier.size(Size.iconLarge),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column {
                    Text(
                        text = "המשך כאורח",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "גלה את האפליקציה ללא הרשמה",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AuthDivider(
    text: String = "או",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.m)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}