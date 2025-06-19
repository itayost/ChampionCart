package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@Composable
fun LoginRegisterScreen(
    onNavigateToHome: () -> Unit,
    onSkipLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    ChampionCartScreen(
        topBar = {
            // Custom minimal top bar for auth screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSkipLogin()
                    }
                ) {
                    Text(
                        text = "דלג",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background effect
            AnimatedBackground()

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(SpacingTokens.XL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(SpacingTokens.XXL))

                // Logo and title
                LogoSection()

                Spacer(modifier = Modifier.height(SpacingTokens.XXL))

                // Auth form
                AuthForm(
                    isLogin = uiState.isLoginMode,
                    email = uiState.email,
                    password = uiState.password,
                    confirmPassword = uiState.confirmPassword,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onConfirmPasswordChange = viewModel::updateConfirmPassword,
                    onSubmit = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (uiState.isLoginMode) {
                            viewModel.login()
                        } else {
                            viewModel.register()
                        }
                    },
                    onToggleMode = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleAuthMode()
                    },
                    isLoading = uiState.isLoading,
                    error = uiState.error
                )

                Spacer(modifier = Modifier.height(SpacingTokens.L))

                // Terms checkbox for registration
                if (!uiState.isLoginMode) {
                    TermsCheckbox(
                        isChecked = uiState.acceptedTerms,
                        onCheckedChange = viewModel::updateAcceptedTerms
                    )
                }
            }
        }
    }

    // Loading overlay
    LoadingDialogOverlay(
        isLoading = uiState.isLoading,
        message = if (uiState.isLoginMode) "מתחבר..." else "נרשם..."
    )

    // Success handling
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToHome()
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.extended.cosmicPurple.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.05f)
                    ),
                    startY = offset,
                    endY = offset + 1000f
                )
            )
    )
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.extended.electricMint,
                            MaterialTheme.colorScheme.extended.cosmicPurple
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }

        // App name
        Text(
            text = "Champion Cart",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Tagline
        Text(
            text = "חסכו חכם, קנו נכון",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AuthForm(
    isLogin: Boolean,
    email: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.XL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = if (isLogin) "התחברות" else "הרשמה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(SpacingTokens.L))

            // Email field
            EmailTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "אימייל",
                placeholder = "your@email.com",
                enabled = !isLoading,
                isError = error != null && email.isNotEmpty(),
                keyboardActions = KeyboardActions(
                    onNext = { /* Focus password */ }
                )
            )

            FormFieldSpace()

            // Password field
            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "סיסמה",
                enabled = !isLoading,
                isError = error != null && password.isNotEmpty(),
                keyboardActions = KeyboardActions(
                    onNext = if (!isLogin) {
                        { /* Focus confirm password */ }
                    } else null,
                    onDone = if (isLogin) {
                        { onSubmit() }
                    } else null
                )
            )

            // Confirm password for registration
            AnimatedVisibility(
                visible = !isLogin,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    FormFieldSpace()

                    PasswordTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = "אימות סיסמה",
                        enabled = !isLoading,
                        isError = !isLogin && confirmPassword.isNotEmpty() && password != confirmPassword,
                        errorMessage = if (password != confirmPassword) "הסיסמאות אינן תואמות" else null,
                        keyboardActions = KeyboardActions(
                            onDone = { onSubmit() }
                        )
                    )
                }
            }

            // Error message
            AnimatedVisibility(
                visible = error != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = SpacingTokens.S),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Submit button
            PrimaryButton(
                text = if (isLogin) "התחבר" else "הירשם",
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() &&
                        (isLogin || password == confirmPassword),
                leadingIcon = Icons.Default.Check
            )

            Spacer(modifier = Modifier.height(SpacingTokens.M))

            // Toggle mode
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLogin) "אין לך חשבון?" else "כבר יש לך חשבון?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(onClick = onToggleMode) {
                    Text(
                        text = if (isLogin) "הירשם" else "התחבר",
                        color = MaterialTheme.colorScheme.extended.electricMint,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TermsCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.extended.electricMint
            )
        )

        Spacer(modifier = Modifier.width(SpacingTokens.S))

        Text(
            text = "אני מסכים/ה לתנאי השימוש ומדיניות הפרטיות",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}