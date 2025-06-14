package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.championcart.data.local.preferences.TokenManager
import com.example.championcart.presentation.ViewModelFactory
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(factory = ViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val tokenManager = remember { TokenManager(context) }

    // City selection
    var selectedCity by remember { mutableStateOf("Tel Aviv") }
    val (currentCity, showCityDialog) = rememberCitySelectionDialog(
        tokenManager = tokenManager,
        onCitySelected = { city ->
            selectedCity = city
        }
    )

    // Success state animation
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Navigate when registration is successful
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
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
            RegisterContent(
                uiState = uiState,
                selectedCity = selectedCity,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onRegister = {
                    focusManager.clearFocus()
                    viewModel.register()
                },
                onCityClick = showCityDialog,
                onNavigateToLogin = onNavigateToLogin,
                focusManager = focusManager
            )
        }

        // Success animation overlay
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
                ) {
                    AuthSuccessAnimation(
                        onAnimationEnd = onNavigateToHome
                    )

                    Text(
                        text = "Account created successfully!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.success
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterContent(
    uiState: RegisterUiState,
    selectedCity: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onCityClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
        Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

        // Back to login button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                Text(
                    text = "Back to Login",
                    style = AppTextStyles.buttonText
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Logo (smaller version)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

        // Registration form card
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
                // Title
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Start saving on your groceries today",
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
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError,
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
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                // Confirm password field
                PasswordTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirm Password",
                    enabled = !uiState.isLoading,
                    isError = uiState.confirmPasswordError != null,
                    errorMessage = uiState.confirmPasswordError,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                // City selection
                Surface(
                    onClick = onCityClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = ComponentShapes.TextField,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = Dimensions.borderThin,
                        color = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(Dimensions.iconSizeMedium)
                            )
                            Column {
                                Text(
                                    text = "City",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedCity,
                                    style = AppTextStyles.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change city",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Password requirements
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = ComponentShapes.Small
                ) {
                    Row(
                        modifier = Modifier.padding(Dimensions.paddingSmall),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeSmall),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Password must be at least 6 characters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

                // Register button
                ActionButton(
                    text = "Create Account",
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.email.isNotBlank() &&
                            uiState.password.isNotBlank() &&
                            uiState.confirmPassword.isNotBlank() &&
                            uiState.emailError == null &&
                            uiState.passwordError == null &&
                            uiState.confirmPasswordError == null &&
                            !uiState.isLoading,
                    loading = uiState.isLoading,
                    icon = Icons.Default.PersonAdd,
                    size = ButtonSize.LARGE
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

        // Login link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Login",
                    style = AppTextStyles.buttonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

        // Terms text
        TermsText(
            onTermsClick = { /* TODO: Show terms */ },
            modifier = Modifier.padding(horizontal = Dimensions.paddingLarge)
        )

        // Bottom spacing
        Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
    }
}