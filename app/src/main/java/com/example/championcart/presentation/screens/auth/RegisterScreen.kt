package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }

    val focusManager = LocalFocusManager.current
    val config = ChampionCartTheme.config

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated Background
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpacingTokens.XL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "חזור",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Header Section
            RegisterHeader(currentStep = currentStep)

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Progress Indicator
            RegistrationProgress(
                currentStep = currentStep,
                totalSteps = 2
            )

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Registration Form
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "stepTransition"
            ) { step ->
                when (step) {
                    0 -> BasicInfoStep(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        email = email,
                        onEmailChange = { email = it },
                        city = city,
                        onCityChange = { city = it },
                        onNext = { currentStep = 1 }
                    )

                    1 -> PasswordStep(
                        password = password,
                        onPasswordChange = { password = it },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                        agreedToTerms = agreedToTerms,
                        onAgreedToTermsChange = { agreedToTerms = it },
                        onBack = { currentStep = 0 },
                        onRegister = {
                            isLoading = true
                            // Simulate registration
                            onNavigateToHome()
                        },
                        isLoading = isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XL))

            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "כבר יש לך חשבון? ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "התחבר",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChampionCartColors.Brand.ElectricMint,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.XXL))
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ChampionCartColors.Brand.NeonCoral.copy(alpha = 0.03f),
                        ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.05f),
                        ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    start = Offset(0f, gradientOffset),
                    end = Offset(1000f, gradientOffset + 1000f)
                )
            )
    )
}

@Composable
private fun RegisterHeader(currentStep: Int) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ChampionCartColors.Brand.NeonCoral,
                                ChampionCartColors.Brand.ElectricMint
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }

            Text(
                text = when (currentStep) {
                    0 -> "צור חשבון חדש"
                    1 -> "כמעט סיימנו!"
                    else -> "הרשמה"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (currentStep) {
                    0 -> "הצטרף למהפכת החיסכון"
                    1 -> "עוד רגע ותתחיל לחסוך"
                    else -> "התחל לחסוך עוד היום"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RegistrationProgress(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        repeat(totalSteps) { step ->
            val isActive = step <= currentStep
            val animatedWidth by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "progressWidth"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedWidth)
                        .fillMaxHeight()
                        .background(
                            if (isActive) {
                                ChampionCartColors.Brand.ElectricMint
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun BasicInfoStep(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isFormValid = fullName.isNotEmpty() && email.isNotEmpty() && city.isNotEmpty()

    ModernGlassCard(
        shape = RoundedCornerShape(32.dp),
        borderGradient = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.XL),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            Text(
                text = "פרטים בסיסיים",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ModernTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "שם מלא",
                placeholder = "ישראל ישראלי",
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            ModernTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "אימייל",
                placeholder = "your@email.com",
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            ModernTextField(
                value = city,
                onValueChange = onCityChange,
                label = "עיר",
                placeholder = "תל אביב",
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (isFormValid) onNext()
                    }
                )
            )

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            ElectricButton(
                onClick = onNext,
                text = "המשך",
                enabled = isFormValid,
                icon = {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large
            )
        }
    }
}

@Composable
private fun PasswordStep(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    agreedToTerms: Boolean,
    onAgreedToTermsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onRegister: () -> Unit,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current
    val passwordsMatch = password == confirmPassword && password.isNotEmpty()
    val isFormValid = passwordsMatch && agreedToTerms && password.length >= 6

    ModernGlassCard(
        shape = RoundedCornerShape(32.dp),
        borderGradient = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.XL),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L)
        ) {
            Text(
                text = "אבטחת החשבון",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ModernTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "סיסמה",
                placeholder = "לפחות 6 תווים",
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityChange) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) "הסתר סיסמה" else "הצג סיסמה"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = password.isNotEmpty() && password.length < 6
            )

            ModernTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "אימות סיסמה",
                placeholder = "הקלד שוב את הסיסמה",
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = ChampionCartColors.Brand.ElectricMint
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onConfirmPasswordVisibilityChange) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (confirmPasswordVisible) "הסתר סיסמה" else "הצג סיסמה"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (isFormValid) onRegister()
                    }
                ),
                isError = confirmPassword.isNotEmpty() && !passwordsMatch
            )

            // Terms Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAgreedToTermsChange(!agreedToTerms) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = onAgreedToTermsChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = ChampionCartColors.Brand.ElectricMint
                    )
                )
                Spacer(modifier = Modifier.width(SpacingTokens.S))
                Text(
                    text = "אני מסכים/ה ל",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "תנאי השימוש",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChampionCartColors.Brand.ElectricMint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* TODO: Show terms */ }
                )
            }

            Spacer(modifier = Modifier.height(SpacingTokens.S))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
            ) {
                GlassButton(
                    onClick = onBack,
                    text = "חזור",
                    icon = {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                ElectricButton(
                    onClick = onRegister,
                    text = "הירשם",
                    enabled = isFormValid,
                    loading = isLoading,
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Large
                )
            }
        }
    }
}

/**
 * Modern TextField Component
 */
@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ChampionCartColors.Brand.ElectricMint,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            errorBorderColor = ChampionCartColors.Semantic.Error,
            focusedLabelColor = ChampionCartColors.Brand.ElectricMint,
            errorLabelColor = ChampionCartColors.Semantic.Error,
            cursorColor = ChampionCartColors.Brand.ElectricMint
        ),
        modifier = modifier.fillMaxWidth()
    )
}