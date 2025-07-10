package com.example.championcart.presentation.screens.cart.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import com.example.championcart.presentation.components.common.TextInputDialog

/**
 * Dialog for saving the cart with a custom name
 * Uses the enhanced TextInputDialog component
 */
@Composable
fun SaveCartDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    isLoading: Boolean = false
) {
    TextInputDialog(
        visible = visible,
        onDismiss = onDismiss,
        onConfirm = onSave,
        title = "שמור עגלה",
        description = "תן שם לעגלה שלך כדי שתוכל לגשת אליה בקלות בעתיד",
        icon = Icons.Rounded.Save,
        label = "שם העגלה",
        placeholder = "לדוגמה: קניות שבועיות",
        validator = { value ->
            when {
                value.isBlank() -> "חובה להזין שם לעגלה"
                value.length < 2 -> "השם חייב להכיל לפחות 2 תווים"
                value.length > 50 -> "השם ארוך מדי (מקסימום 50 תווים)"
                else -> null
            }
        },
        confirmText = "שמור",
        dismissText = "ביטול",
        isLoading = isLoading
    )
}