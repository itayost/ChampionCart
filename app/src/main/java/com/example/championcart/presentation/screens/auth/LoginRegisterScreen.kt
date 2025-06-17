package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.championcart.presentation.components.LoadingDialog
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(
    onNavigateToHome: () -> Unit,
    onGuestMode: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Time-based greeting
    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 6..11 -> "Good Morning, Champion!"
            in 12..17 -> "Good Afternoon, Champion!"
            in 18..22 -> "Good Evening, Champion!"
            else -> "Welcome, Champion!"
        }
    }

    // Handle authentication success
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            delay(300)
            onNavigateToHome()
        }
    }

    // Handle errors
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Dismiss"
            )
            viewModel.clearError()
        }
    }

    // Loading dialog
    if (state.isLoading) {
        LoadingDialog(
            message = if (state.isLoginMode) "Signing you in..." else "Creating your account..."
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = MaterialTheme.extendedColors.backgroundGradient.colors
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))

                // Logo and greeting
                AuthHeader(greeting = greeting)

                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))

                // Auth form
                AuthForm(
                    state = state,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onConfirmPasswordChange = viewModel::updateConfirmPassword,
                    onToggleMode = viewModel::toggleMode,
                    onLogin = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        keyboardController?.hide()
                        viewModel.login()
                    },
                    onRegister = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        keyboardController?.hide()
                        viewModel.register()
                    },
                    onGuestMode = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onGuestMode()
                    }
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
            }
        }
    }
}

@Composable
private fun AuthHeader(
    greeting: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        // App logo/icon placeholder
        Card(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.electricMint
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.elevationLarge
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Champion Cart",
                    modifier = Modifier.size(Dimensions.iconSizeLarge),
                    tint = Color.White
                )
            }
        }

        // App title
        Text(
            text = "Champion Cart",
            style = AppTextStyles.hebrewDisplay,
            color = MaterialTheme.extendedColors.electricMint,
            fontWeight = FontWeight.Bold
        )

        // Greeting
        Text(
            text = greeting,
            style = AppTextStyles.hebrewHeadline,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthForm(
    state: AuthState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGuestMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(
            Dimensions.borderThin,
            MaterialTheme.extendedColors.glassFrostedBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationLarge
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode toggle buttons
            AuthModeToggle(
                isLoginMode = state.isLoginMode,
                onToggle = onToggleMode
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Email field
            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = state.emailError != null,
                errorMessage = state.emailError
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Password field
            var passwordVisible by remember { mutableStateOf(false) }
            AuthTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password",
                            modifier = Modifier.size(Dimensions.iconSizeMedium)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (state.isLoginMode) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.isLoginMode) {
                            keyboardController?.hide()
                            onLogin()
                        }
                    },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )

            // Confirm password field (only for register)
            AnimatedVisibility(
                visible = !state.isLoginMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                    AuthTextField(
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = "Confirm Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                onRegister()
                            }
                        ),
                        isError = state.confirmPasswordError != null,
                        errorMessage = state.confirmPasswordError
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Main action button
            PrimaryActionButton(
                text = if (state.isLoginMode) "Sign In" else "Create Account",
                onClick = if (state.isLoginMode) onLogin else onRegister,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Guest mode button
            SecondaryActionButton(
                text = "Continue as Guest",
                icon = Icons.Default.Person,
                onClick = onGuestMode
            )
        }
    }
}

@Composable
private fun AuthModeToggle(
    isLoginMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.extendedColors.glass,
                shape = ComponentShapes.Button
            )
            .padding(Dimensions.spacingExtraSmall)
    ) {
        // Login button
        ToggleButton(
            text = "Sign In",
            isSelected = isLoginMode,
            onClick = { if (!isLoginMode) onToggle() },
            modifier = Modifier.weight(1f)
        )

        // Register button
        ToggleButton(
            text = "Sign Up",
            isSelected = !isLoginMode,
            onClick = { if (isLoginMode) onToggle() },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.dampingRatioLowBounce,
            stiffness = SpringSpecs.stiffnessHigh
        ),
        label = "toggle_button_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ComponentShapes.Button)
            .background(
                color = if (isSelected)
                    MaterialTheme.extendedColors.electricMint
                else
                    Color.Transparent
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected)
                Color.White
            else
                MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = AppTextStyles.inputLabel
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeMedium),
                    tint = if (isError)
                        MaterialTheme.extendedColors.errorRed
                    else
                        MaterialTheme.extendedColors.electricMint
                )
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = ComponentShapes.TextField,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.extendedColors.electricMint,
                unfocusedBorderColor = MaterialTheme.extendedColors.glassBorder,
                errorBorderColor = MaterialTheme.extendedColors.errorRed,
                focusedLabelColor = MaterialTheme.extendedColors.electricMint,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.extendedColors.electricMint
            ),
            textStyle = AppTextStyles.inputText
        )

        // Error message
        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = AppTextStyles.caption,
                    color = MaterialTheme.extendedColors.errorRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.spacingExtraSmall)
                        .padding(horizontal = Dimensions.paddingMedium)
                )
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.dampingRatioLowBounce,
            stiffness = SpringSpecs.stiffnessHigh
        ),
        label = "primary_button_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.buttonHeight)
            .scale(scale),
        shape = ComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.extendedColors.electricMint,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationMedium,
            pressedElevation = Dimensions.elevationSmall
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = SpringSpecs.dampingRatioLowBounce,
            stiffness = SpringSpecs.stiffnessHigh
        ),
        label = "secondary_button_scale"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.buttonHeight)
            .scale(scale),
        shape = ComponentShapes.Button,
        border = BorderStroke(
            width = Dimensions.borderThin,
            color = MaterialTheme.extendedColors.glassBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.extendedColors.glass,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimensions.iconSizeSmall)
        )
        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal
        )
    }
}

/**
 * Note: AuthState and AuthViewModel are defined in AuthViewModel.kt
 * This screen uses the actual domain repositories and use cases
 */