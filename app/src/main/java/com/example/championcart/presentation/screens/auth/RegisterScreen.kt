package com.example.championcart.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            snackbarHostState.showSnackbar("נרשמת בהצלחה!")
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            ChampionTopBar(
                title = "",
                navigationIcon = {
                    BackButton(onClick = onNavigateToLogin)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    ChampionSnackbar(snackbarData = data)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = BrandColors.ElectricMint
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                Text(
                    text = "צור חשבון חדש",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.s))

                Text(
                    text = "הצטרף למהפכת החיסכון בסופר",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Registration Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l)
            ) {
                // Email
                ChampionTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "כתובת מייל",
                    placeholder = "your@email.com",
                    leadingIcon = Icons.Rounded.Email,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError,
                    enabled = !uiState.isLoading
                )

                // Password
                PasswordField(
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    placeholder = "צור סיסמה חזקה",
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError,
                    enabled = !uiState.isLoading
                )

                // Confirm Password
                PasswordField(
                    password = uiState.confirmPassword,
                    onPasswordChange = viewModel::onConfirmPasswordChange,
                    label = "אימות סיסמה",
                    placeholder = "הכנס שוב את הסיסמה",
                    isError = uiState.confirmPasswordError != null,
                    errorMessage = uiState.confirmPasswordError,
                    enabled = !uiState.isLoading
                )

                // Password Strength Indicator
                if (uiState.password.isNotEmpty()) {
                    PasswordStrengthIndicator(
                        password = uiState.password
                    )
                }

                // Terms Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = uiState.acceptedTerms,
                        onCheckedChange = viewModel::onAcceptTermsChange,
                        enabled = !uiState.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrandColors.ElectricMint
                        )
                    )
                    Column(
                        modifier = Modifier.padding(start = Spacing.s)
                    ) {
                        Row {
                            Text(
                                text = "אני מסכים/ה ל",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "תנאי השימוש",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrandColors.ElectricMint,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clickable { /* TODO: Show terms */ }
                            )
                        }
                        Row {
                            Text(
                                text = "ו",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "מדיניות הפרטיות",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrandColors.ElectricMint,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable { /* TODO: Show privacy */ }
                            )
                        }

                        if (uiState.termsError != null) {
                            Text(
                                text = uiState.termsError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = SemanticColors.Error,
                                modifier = Modifier.padding(top = Spacing.xs)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.m))

                // Register Button
                PrimaryButton(
                    text = "הירשם",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.register()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isRegisterEnabled && !uiState.isLoading,
                    isLoading = uiState.isLoading
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Login Link
            Row(
                modifier = Modifier.padding(bottom = Spacing.xl),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "כבר יש לך חשבון? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "התחבר",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandColors.ElectricMint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = calculatePasswordStrength(password)
    val (color, text) = when (strength) {
        PasswordStrength.WEAK -> PriceColors.High to "חלשה"
        PasswordStrength.MEDIUM -> PriceColors.Mid to "בינונית"
        PasswordStrength.STRONG -> PriceColors.Best to "חזקה"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(Shapes.badge)
                        .background(
                            if (index < strength.level) color
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Text(
            text = "חוזק סיסמה: $text",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

enum class PasswordStrength(val level: Int) {
    WEAK(1),
    MEDIUM(2),
    STRONG(3)
}

fun calculatePasswordStrength(password: String): PasswordStrength {
    var score = 0

    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when {
        score >= 3 -> PasswordStrength.STRONG
        score >= 2 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.WEAK
    }
}