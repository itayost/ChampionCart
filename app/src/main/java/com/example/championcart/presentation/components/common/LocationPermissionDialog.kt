package com.example.championcart.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.*

@Composable
fun LocationPermissionDialog(
    visible: Boolean,
    onGrantPermission: () -> Unit,
    onDenyPermission: () -> Unit
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDenyPermission,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // Apply RTL layout
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.l),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Padding.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.l)
                    ) {
                        // Icon with animated background
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Glassmorphic background circle
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = Shapes.chip,
                                color = BrandColors.ElectricMint.copy(alpha = 0.1f),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = BrandColors.ElectricMint.copy(alpha = 0.3f)
                                )
                            ) {}

                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = BrandColors.ElectricMint
                            )
                        }

                        // Title
                        Text(
                            text = "גישה למיקום",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        // Description
                        Text(
                            text = "סל ניצחון צריך גישה למיקום שלך כדי לזהות אוטומטית את העיר שלך ולהציג לך את המחירים הרלוונטיים באזורך.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = Spacing.m)
                        )

                        // Benefits list
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.s),
                            verticalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            BenefitItem(text = "זיהוי אוטומטי של העיר שלך")
                            BenefitItem(text = "מחירים מדויקים מחנויות באזורך")
                            BenefitItem(text = "חיסכון זמן בבחירת מיקום")
                        }

                        // Buttons in Column
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.m),
                            verticalArrangement = Arrangement.spacedBy(Spacing.m)
                        ) {
                            // Grant button (Primary - on top)
                            Button(
                                onClick = onGrantPermission,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Size.buttonHeight),
                                shape = Shapes.button,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandColors.ElectricMint,
                                    contentColor = Color.Black
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 2.dp,
                                    pressedElevation = 8.dp
                                )
                            ) {
                                Text(
                                    text = "אפשר גישה",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Deny button (Secondary - below)
                            OutlinedButton(
                                onClick = onDenyPermission,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Size.buttonHeight),
                                shape = Shapes.button,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Text(
                                    text = "לא עכשיו",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Privacy note
                        Text(
                            text = "המיקום שלך נשאר פרטי ומשמש רק לזיהוי העיר",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = Spacing.xs)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkmark icon
        Surface(
            modifier = Modifier.size(20.dp),
            shape = Shapes.chip,
            color = SemanticColors.Success.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SemanticColors.Success
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.m))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}