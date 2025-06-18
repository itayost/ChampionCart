package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.championcart.presentation.components.FloatingOrbsBackground
import com.example.championcart.presentation.components.LoadingDialog
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalTime

/**
 * LoginRegisterScreen - Using Champion Cart's Premium UI System
 */

// Add these modifier extensions
fun Modifier.gradientBackground(
    colors: List<Color> = listOf(Color.Transparent)
) = composed {
    this.background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    )
}

fun Modifier.modernPressAnimation(
    enabled: Boolean = true
) = composed {
    if (!enabled) return@composed this

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = SpringSpecs.Bouncy,
        label = "press_scale"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

fun Modifier.organicEntrance(
    delay: Int = 0
) = composed {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "entrance_alpha"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "entrance_offset"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    this
        .graphicsLayer {
            this.alpha = alpha
            translationY = offsetY
        }
}

fun Modifier.electricGlow(
    glowColor: Color,
    intensity: Float = 1f
) = composed {
    this.drawWithCache {
        onDrawBehind {
            // Simple glow effect using shadow
            drawCircle(
                color = glowColor.copy(alpha = 0.3f * intensity),
                radius = size.minDimension / 2 + 20f
            )
        }
    }
}

private fun Modifier.composed(factory: @Composable() (Modifier.() -> Unit)): Modifier {
    TODO("Not yet implemented")
}

fun Modifier.attentionBounce() = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_offset"
    )

    this.offset(y = offset.dp)
}

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
    val colors = LocalExtendedColors.current

    // Time-based greeting
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

    // Loading dialog
    if (state.isLoading) {
        LoadingDialog(
            isLoading = true,
            message = if (state.isLoginMode) "נכנס..." else "יוצר חשבון..."
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .gradientBackground(
                    colors = listOf(
                        colors.gradientStart.copy(alpha = 0.8f),
                        colors.gradientMiddle.copy(alpha = 0.6f),
                        colors.gradientEnd.copy(alpha = 0.8f)
                    )
                )
        )

        // Using existing FloatingOrbsBackground
        FloatingOrbsBackground(
            modifier = Modifier.fillMaxSize(),
            orbCount = 3,
            alpha = 0.6f
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Auth Header
                AuthHeader(
                    greeting = greeting,
                    subtitle = subtitle,
                    modifier = Modifier.organicEntrance(delay = 0)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Auth Form Card
                AuthFormCard(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier.organicEntrance(delay = 200)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Guest Mode Button
                GuestModeButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onGuestMode()
                    },
                    modifier = Modifier.organicEntrance(delay = 400)
                )

                Spacer(modifier = Modifier.height(32.dp))
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
    val colors = LocalExtendedColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo
        Card(
            modifier = Modifier
                .size(96.dp)
                .modernPressAnimation(),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = colors.electricMint
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }

        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
    val colors = LocalExtendedColors.current

    // Use Box for proper layering instead of Card with glassmorphic
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.GlassCardLarge)
    ) {
        // Glass background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .glassmorphic(
                    intensity = GlassIntensity.Light,
                    shape = GlassmorphicShapes.GlassCardLarge
                )
        )

        // Content layer on top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode toggle
            AuthModeToggle(
                isLoginMode = state.isLoginMode,
                onToggle = viewModel::toggleMode
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("אימייל") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = colors.electricMint
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = GlassmorphicShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.electricMint,
                    unfocusedBorderColor = colors.borderDefault.copy(alpha = 0.5f),
                    errorBorderColor = colors.highPrice,
                    focusedLabelColor = colors.electricMint,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = colors.electricMint,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    // For outlined text fields, use container colors like this:
                    focusedContainerColor = Color.White.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    errorContainerColor = Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                )
            )

            // Password field
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("סיסמה") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = colors.electricMint
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = viewModel::togglePasswordVisibility
                    ) {
                        Icon(
                            imageVector = if (state.showPassword) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = colors.electricMint
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
                    onDone = {
                        if (state.isLoginMode) {
                            viewModel.login()
                        }
                    }
                ),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = GlassmorphicShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.electricMint,
                    unfocusedBorderColor = colors.borderDefault.copy(alpha = 0.5f),
                    errorBorderColor = colors.highPrice,
                    focusedLabelColor = colors.electricMint,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = colors.electricMint,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTrailingIconColor = colors.electricMint,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedContainerColor = Color.White.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    errorContainerColor = Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                )
            )

            // Confirm password for registration
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                label = { Text("אשר סיסמה") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = colors.electricMint
                    )
                },
                visualTransformation = if (state.showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.register() }
                ),
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = GlassmorphicShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.electricMint,
                    unfocusedBorderColor = colors.borderDefault.copy(alpha = 0.5f),
                    errorBorderColor = colors.highPrice,
                    focusedLabelColor = colors.electricMint,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = colors.electricMint,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedContainerColor = Color.White.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    errorContainerColor = Color.White.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                )
            )

            // Remember me checkbox (login only)
            AnimatedVisibility(
                visible = state.isLoginMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.electricMint,
                            uncheckedColor = colors.borderDefault
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "זכור אותי",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Primary action button
            PrimaryActionButton(
                text = if (state.isLoginMode) "התחבר" else "הירשם",
                onClick = {
                    if (state.isLoginMode) {
                        viewModel.login()
                    } else {
                        viewModel.register()
                    }
                },
                enabled = !state.isLoading, // Fixed: was using state.isAuthenticated
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Terms text
            Text(
                text = "בהמשך, אתה מסכים לתנאי השימוש ומדיניות הפרטיות",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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
    val colors = LocalExtendedColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassmorphicShapes.Chip)
            .background(colors.glassLight)
            .border(
                width = 1.dp,
                color = colors.borderGlass,
                shape = GlassmorphicShapes.Chip
            )
            .padding(4.dp)
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

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current

    Box(
        modifier = modifier
            .clip(GlassmorphicShapes.Button)
            .background(
                color = if (isSelected) colors.electricMint else Color.Transparent
            )
            .modernPressAnimation()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(56.dp)
            .modernPressAnimation(enabled = enabled),
        shape = GlassmorphicShapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.electricMint,
            contentColor = Color.White,
            disabledContainerColor = colors.electricMint.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
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

@Composable
private fun GuestModeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .modernPressAnimation(),
        shape = GlassmorphicShapes.Button,
        border = BorderStroke(
            width = 1.dp,
            color = colors.borderGlass
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.electricMint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "המשך כאורח",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal
        )
    }
}