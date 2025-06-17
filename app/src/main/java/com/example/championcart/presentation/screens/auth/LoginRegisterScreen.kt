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
import androidx.compose.ui.unit.sp
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
            in 6..11 -> "Good Morning, Champion! ☀️"
            in 12..17 -> "Good Afternoon, Champion! 🌤️"
            in 18..22 -> "Good Evening, Champion! 🌙"
            else -> "Welcome, Champion! 🌟"
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    // Show loading dialog
    if (state.isLoading) {
        LoadingDialog(message = if (state.isLoginMode) "Signing in..." else "Creating account...")
    }

    // Handle authentication success
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(500) // Small delay for animation
            onNavigateToHome()
        }
    }

    // Handle errors
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Floating orbs for decoration
            FloatingOrb(
                modifier = Modifier
                    .offset(x = (-50).dp, y = 100.dp)
                    .size(120.dp),
                color = MaterialTheme.extendedColors.electricMint.copy(alpha = 0.2f),
                animationDuration = 8000
            )

            FloatingOrb(
                modifier = Modifier
                    .offset(x = 200.dp, y = 400.dp)
                    .size(80.dp),
                color = MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.3f),
                animationDuration = 10000
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimensions.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Logo and greeting
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { -it }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 40.dp)
                    ) {
                        // App logo placeholder
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.extendedColors.electricMint,
                                            MaterialTheme.extendedColors.electricMintLight
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Greeting with animated text
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "Save money on groceries with smart price comparison",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Auth mode tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabSelector(
                        selected = state.isLoginMode,
                        onClick = { viewModel.toggleAuthMode() },
                        text = "Sign In"
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    TabSelector(
                        selected = !state.isLoginMode,
                        onClick = { viewModel.toggleAuthMode() },
                        text = "Sign Up"
                    )
                }

                // Auth form
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Email field
                        ModernTextField(
                            value = state.email,
                            onValueChange = viewModel::updateEmail,
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password field
                        ModernTextField(
                            value = state.password,
                            onValueChange = viewModel::updatePassword,
                            label = "Password",
                            leadingIcon = Icons.Default.Lock,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (state.isLoginMode) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (state.isLoginMode) {
                                        keyboardController?.hide()
                                        viewModel.login()
                                    }
                                },
                                onNext = {
                                    if (!state.isLoginMode) {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                }
                            ),
                            isError = state.passwordError != null,
                            errorMessage = state.passwordError
                        )

                        // Confirm password for registration
                        AnimatedVisibility(
                            visible = !state.isLoginMode,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                ModernTextField(
                                    value = state.confirmPassword,
                                    onValueChange = viewModel::updateConfirmPassword,
                                    label = "Confirm Password",
                                    leadingIcon = Icons.Default.Lock,
                                    visualTransformation = if (passwordVisible)
                                        VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            viewModel.register()
                                        }
                                    ),
                                    isError = state.confirmPasswordError != null,
                                    errorMessage = state.confirmPasswordError
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Primary action button
                        PrimaryButton(
                            text = if (state.isLoginMode) "Sign In" else "Create Account",
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (state.isLoginMode) viewModel.login() else viewModel.register()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Guest mode button
                        OutlinedButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onGuestMode()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = ComponentShapes.Button,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.extendedColors.electricMint.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue as Guest")
                        }
                    }
                }

                // Terms and privacy
                Row(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "By continuing, you agree to our ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Terms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.clickable { /* TODO: Open terms */ }
                    )
                    Text(
                        text = " and ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.electricMint,
                        modifier = Modifier.clickable { /* TODO: Open privacy policy */ }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun TabSelector(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.extendedColors.electricMint
        else
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        label = "tab_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = animatedColor,
            modifier = Modifier.clickable { onClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tab indicator
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .background(
                    color = if (selected) animatedColor else Color.Transparent,
                    shape = ComponentShapes.Badge
                )
        )
    }
}

@Composable
private fun FloatingOrb(
    modifier: Modifier = Modifier,
    color: Color,
    animationDuration: Int = 8000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_offset"
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun ModernTextField(
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
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
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
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.glassFrosted
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.extendedColors.glassFrostedBorder
        )
    ) {
        Column(content = content)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimensions.buttonHeight)
            .scale(scale),
        enabled = enabled,
        shape = ComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.extendedColors.electricMint,
                            MaterialTheme.extendedColors.success // Use success instead of successGreen
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}