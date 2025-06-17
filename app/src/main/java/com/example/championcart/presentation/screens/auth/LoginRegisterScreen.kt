package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.presentation.components.LoadingDialog
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

/**
 * LoginRegisterScreen - USING OUR EXISTING UI SYSTEM
 * ✨ Now properly uses:
 * - FloatingOrbs from GlowEffects.kt
 * - modernPressAnimation, organicEntrance from EnhancedAnimations.kt
 * - electricGlow, successGlow from GlowEffects.kt
 * - enhancedGlass from GlassEffects.kt
 * - All ExtendedColors from ExtendedTheme.kt
 * - Proper ComponentShapes and Dimensions
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(
    onNavigateToHome: () -> Unit,
    onGuestMode: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Time-based greeting using our existing system
    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 6..11 -> "בוקר טוב, צ'מפיון! ☀️"
            in 12..17 -> "צהריים טובים, צ'מפיון! 🌤️"
            in 18..22 -> "ערב טוב, צ'מפיון! 🌙"
            else -> "ברוך הבא, צ'מפיון! ✨"
        }
    }

    val subtitle = remember(state.isLoginMode) {
        if (state.isLoginMode) "היכנס כדי להתחיל לחסוך" else "הצטרף למהפכת החיסכון"
    }

    // Handle authentication success
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(300)
            onNavigateToHome()
        }
    }

    // Handle errors
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // Loading dialog using our existing component
    if (state.isLoading) {
        LoadingDialog(
            message = if (state.isLoginMode) "נכנס..." else "יוצר חשבון..."
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✨ USING OUR gradientBackground from Enhanced Colors
        Box(
            modifier = Modifier
                .fillMaxSize()
                .gradientBackground(
                    startColor = MaterialTheme.extendedColors.morningGradientStart,
                    endColor = MaterialTheme.extendedColors.morningGradientEnd
                )
        )

        // ✨ USING OUR FloatingOrbs from GlowEffects.kt
        FloatingOrbs(
            modifier = Modifier.fillMaxSize(),
            orbCount = 3,
            colors = listOf(
                MaterialTheme.extendedColors.electricMintGlow,
                MaterialTheme.extendedColors.cosmicPurpleGlow,
                MaterialTheme.extendedColors.neonCoralGlow
            )
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimensions.screenPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge * 2))

                // ✨ USING OUR organicEntrance animation
                AuthHeader(
                    greeting = greeting,
                    subtitle = subtitle,
                    modifier = Modifier.organicEntrance(delay = 0)
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))

                // ✨ USING OUR organicEntrance animation
                AuthFormCard(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier.organicEntrance(delay = 200)
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

                // ✨ USING OUR organicEntrance animation
                GuestModeButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onGuestMode()
                    },
                    modifier = Modifier.organicEntrance(delay = 400)
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
            }
        }
    }
}

@Composable
private fun AuthHeader(
    greeting: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        // ✨ USING OUR modernPressAnimation + electricGlow
        Card(
            modifier = Modifier
                .size(Dimensions.iconSizeHuge + 16.dp)
                .modernPressAnimation()
                .electricGlow(
                    glowColor = MaterialTheme.extendedColors.electricMintGlow,
                    intensity = 1f
                ),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.electricMint
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.elevationLarge
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.cosmicPurple
                            )
                        )
                    ),
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

        // Dynamic greeting with our typography
        Text(
            text = greeting,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.extendedColors.electricMint,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Subtitle
        Text(
            text = subtitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthFormCard(
    state: AuthState,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // ✨ USING OUR enhancedGlass effect
    Card(
        modifier = modifier
            .fillMaxWidth()
            .enhancedGlass(
                backgroundColor = MaterialTheme.extendedColors.glassFrosted,
                borderColor = MaterialTheme.extendedColors.glassFrostedBorder
            ),
        shape = ComponentShapes.CardLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationLarge
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode toggle using our glass effects
            AuthModeToggle(
                isLoginMode = state.isLoginMode,
                onToggle = viewModel::toggleMode
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // ✨ USING OUR staggeredListAnimation
            EnhancedTextField(
                value = state.email,
                onValueChange = viewModel::updateEmail,
                label = "אימייל",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = state.emailError != null,
                errorMessage = state.emailError,
                modifier = Modifier.staggeredListAnimation(index = 0)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // ✨ USING OUR staggeredListAnimation
            EnhancedTextField(
                value = state.password,
                onValueChange = viewModel::updatePassword,
                label = "סיסמה",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(
                        onClick = viewModel::togglePasswordVisibility,
                        modifier = Modifier.modernPressAnimation()
                    ) {
                        Icon(
                            imageVector = if (state.showPassword) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.electricMint
                        )
                    }
                },
                visualTransformation = if (state.showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (state.isLoginMode) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = if (state.isLoginMode) {
                        {
                            keyboardController?.hide()
                            if (viewModel.isFormValid()) viewModel.login()
                        }
                    } else {
                        { focusManager.moveFocus(FocusDirection.Down) }
                    }
                ),
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                modifier = Modifier.staggeredListAnimation(index = 1)
            )

            // Password strength indicator using our success colors
            if (!state.isLoginMode && state.password.isNotBlank()) {
                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                PasswordStrengthIndicator(
                    strength = viewModel.getPasswordStrength(),
                    modifier = Modifier.staggeredListAnimation(index = 2)
                )
            }

            // Confirm password field (register mode only)
            AnimatedVisibility(
                visible = !state.isLoginMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                    EnhancedTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        label = "אימות סיסמה",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(
                                onClick = viewModel::toggleConfirmPasswordVisibility,
                                modifier = Modifier.modernPressAnimation()
                            ) {
                                Icon(
                                    imageVector = if (state.showConfirmPassword) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.extendedColors.electricMint
                                )
                            }
                        },
                        visualTransformation = if (state.showConfirmPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                if (viewModel.isFormValid()) viewModel.register()
                            }
                        ),
                        isError = state.confirmPasswordError != null,
                        errorMessage = state.confirmPasswordError
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))

            // Terms checkbox for register mode
            if (!state.isLoginMode) {
                TermsCheckbox(
                    checked = state.agreedToTerms,
                    onCheckedChange = { viewModel.toggleTermsAgreement() }
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
            }

            // ✨ USING OUR enhanced button with gradient
            PrimaryActionButton(
                text = if (state.isLoginMode) "כניסה" else "הרשמה",
                onClick = if (state.isLoginMode) viewModel::login else viewModel::register,
                enabled = viewModel.isFormValid() && !state.isLoading,
                isLoading = state.isLoading
            )

            // Remember me toggle (login mode only)
            if (state.isLoginMode) {
                Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
                RememberMeToggle(
                    checked = state.rememberMe,
                    onCheckedChange = { viewModel.toggleRememberMe() }
                )
            }
        }
    }
}

// ✨ USING OUR glass effects and animations
@Composable
private fun AuthModeToggle(
    isLoginMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .enhancedGlass(
                backgroundColor = MaterialTheme.extendedColors.glass,
                borderColor = MaterialTheme.extendedColors.glassBorder
            )
            .padding(Dimensions.spacingExtraSmall)
    ) {
        ToggleButton(
            text = "כניסה",
            isSelected = isLoginMode,
            onClick = { if (!isLoginMode) onToggle() },
            modifier = Modifier.weight(1f)
        )

        ToggleButton(
            text = "הרשמה",
            isSelected = !isLoginMode,
            onClick = { if (isLoginMode) onToggle() },
            modifier = Modifier.weight(1f)
        )
    }
}

// ✨ USING OUR modernPressAnimation
@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(ComponentShapes.Button)
            .background(
                color = if (isSelected) MaterialTheme.extendedColors.electricMint
                else Color.Transparent
            )
            .modernPressAnimation()
            .clickable { onClick() }
            .padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ✨ USING OUR enhanced glass effects
@Composable
private fun EnhancedTextField(
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
            label = { Text(text = label) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dimensions.iconSizeMedium)
                        .electricGlow(
                            glowColor = if (isError) MaterialTheme.extendedColors.errorGlow
                            else MaterialTheme.extendedColors.electricMintGlow,
                            intensity = 0.5f
                        ),
                    tint = if (isError) MaterialTheme.extendedColors.errorRed
                    else MaterialTheme.extendedColors.electricMint
                )
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .enhancedGlass(
                    backgroundColor = MaterialTheme.extendedColors.glass,
                    borderColor = MaterialTheme.extendedColors.glassBorder
                ),
            shape = ComponentShapes.TextField,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.extendedColors.electricMint,
                unfocusedBorderColor = MaterialTheme.extendedColors.glassBorder,
                errorBorderColor = MaterialTheme.extendedColors.errorRed,
                focusedLabelColor = MaterialTheme.extendedColors.electricMint,
                cursorColor = MaterialTheme.extendedColors.electricMint,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        // ✨ USING OUR attentionBounce for errors
        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.extendedColors.errorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(
                            start = Dimensions.paddingMedium,
                            top = Dimensions.spacingExtraSmall
                        )
                        .attentionBounce(trigger = isError)
                )
            }
        }
    }
}

// ✨ USING OUR success/warning/error colors and animations
@Composable
private fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val (strengthText, strengthColor, progress) = when (strength) {
        PasswordStrength.WEAK -> Triple(
            "חלשה",
            MaterialTheme.extendedColors.errorRed,
            0.33f
        )
        PasswordStrength.MEDIUM -> Triple(
            "בינונית",
            MaterialTheme.extendedColors.warning,
            0.66f
        )
        PasswordStrength.STRONG -> Triple(
            "חזקה",
            MaterialTheme.extendedColors.success,
            1f
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "חוזק הסיסמה:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = strengthText,
                style = MaterialTheme.typography.bodySmall,
                color = strengthColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.successGlow(intensity = if (strength == PasswordStrength.STRONG) 1f else 0f)
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingExtraSmall))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = strengthColor,
            trackColor = strengthColor.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .modernPressAnimation()
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.extendedColors.electricMint,
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkmarkColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))

        Text(
            text = "אני מסכים לתנאים והתקנות",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RememberMeToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .modernPressAnimation()
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.extendedColors.electricMint,
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkmarkColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))

        Text(
            text = "זכור אותי",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ✨ USING OUR gradient system and modernPressAnimation
@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.buttonHeight)
            .modernPressAnimation(enabled = enabled),
        shape = ComponentShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint,
                                MaterialTheme.extendedColors.cosmicPurple
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.extendedColors.electricMint.copy(alpha = 0.5f),
                                MaterialTheme.extendedColors.cosmicPurple.copy(alpha = 0.5f)
                            )
                        )
                    },
                    shape = ComponentShapes.Button
                )
                .electricGlow(
                    glowColor = MaterialTheme.extendedColors.electricMintGlow,
                    intensity = if (enabled) 1f else 0.3f
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ✨ USING OUR glass effects and modernPressAnimation
@Composable
private fun GuestModeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.buttonHeight)
            .modernPressAnimation()
            .enhancedGlass(
                backgroundColor = MaterialTheme.extendedColors.glass,
                borderColor = MaterialTheme.extendedColors.glassBorder
            ),
        shape = ComponentShapes.Button,
        border = BorderStroke(
            width = Dimensions.borderThin,
            color = MaterialTheme.extendedColors.glassBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier
                .size(Dimensions.iconSizeSmall)
                .electricGlow(
                    glowColor = MaterialTheme.extendedColors.electricMintGlow,
                    intensity = 0.3f
                )
        )
        Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
        Text(
            text = "המשך כאורח",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal
        )
    }
}