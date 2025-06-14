package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Success state animation
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Navigate when login is successful
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            showSuccessAnimation = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Main content
        AnimatedVisibility(
            visible = !showSuccessAnimation,
            exit = fadeOut()
        ) {
            LoginContent(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLogin = {
                    focusManager.clearFocus()
                    viewModel.login()
                },
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToHome = onNavigateToHome,
                focusManager = focusManager
            )
        }

        // Success animation overlay
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AuthSuccessAnimation(
                    onAnimationEnd = onNavigateToHome
                )
            }
        }
    }
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensions.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top spacing
        Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))

        // Logo and branding
        AuthScreenLogo()

        Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))

        // Login form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.elevationSmall
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                // Welcome text
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to access your saved carts and preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

                // Error message
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AuthErrorCard(
                        error = uiState.error ?: "",
                        onDismiss = null,
                        modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
                    )
                }

                // Email field
                AuthTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    enabled = !uiState.isLoading,
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                // Password field
                PasswordTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    enabled = !uiState.isLoading,
                    imeAction = ImeAction.Done,
                    onImeAction = onLogin
                )

                // Forgot password link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = { /* TODO: Implement forgot password */ },
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = "Forgot password?",
                            style = AppTextStyles.buttonText.copy(
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

                // Login button
                ActionButton(
                    text = "Login",
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.email.isNotBlank() &&
                            uiState.password.isNotBlank() &&
                            !uiState.isLoading,
                    loading = uiState.isLoading,
                    icon = Icons.Default.Login,
                    size = ButtonSize.LARGE
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

        // Register section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ComponentShapes.Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
            ) {
                Text(
                    text = "New to Champion Cart?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                SecondaryButton(
                    text = "Create Account",
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.PersonAdd,
                    size = ButtonSize.LARGE
                )

                AuthDivider()

                // Guest mode button with special styling
                Surface(
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ComponentShapes.Button,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = Dimensions.borderThin,
                        color = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimensions.paddingMedium),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeMedium),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                        Column {
                            Text(
                                text = "Continue as Guest",
                                style = AppTextStyles.buttonText,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Limited features",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeMedium),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

        // Terms text
        TermsText(
            onTermsClick = { /* TODO: Show terms */ },
            modifier = Modifier.padding(horizontal = Dimensions.paddingLarge)
        )

        // Bottom spacing
        Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
    }
}