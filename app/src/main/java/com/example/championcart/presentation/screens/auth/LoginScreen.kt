package com.example.championcart.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.championcart.presentation.components.*
import com.example.championcart.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Login Screen
 * Electric Harmony themed authentication
 */

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ChampionCartTheme.colors.surface,
                        ChampionCartTheme.colors.surface.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        // Background decoration
        AnimatedBackgroundShapes()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Screen.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Logo and Title
            LogoSection()

            Spacer(modifier = Modifier.height(Spacing.giant))

            // Login Form
            LoginForm(
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = {
                    focusManager.clearFocus()
                    viewModel.login()
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Social Login
            SocialLoginSection(
                onGoogleLogin = viewModel::loginWithGoogle,
                isEnabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Link
            SignUpSection(
                onSignUpClick = {
                    navController.navigate("register")
                }
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Skip Login Option
            GlassTextButton(
                onClick = {
                    viewModel.skipLogin()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                text = "המשך כאורח"
            )
        }

        // Loading Overlay
        if (uiState.isLoading) {
            LoadingDialog(
                isLoading = true,
                message = "מתחבר..."
            )
        }

        // Handle navigation
        LaunchedEffect(uiState.isLoginSuccessful) {
            if (uiState.isLoginSuccessful) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // Error handling
        uiState.generalError?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }
    }
}

@Composable
private fun AnimatedBackgroundShapes() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top right shape
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 150.dp, y = (-100).dp)
                .graphicsLayer { rotationZ = rotation }
                .glass(
                    intensity = GlassIntensity.Light,
                    shape = CircleShape
                )
                .background(
                    ChampionCartColors.Brand.ElectricMint.copy(alpha = 0.05f),
                    shape = CircleShape
                )
        )

        // Bottom left shape
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-100).dp, y = 500.dp)
                .graphicsLayer { rotationZ = -rotation * 0.7f }
                .glass(
                    intensity = GlassIntensity.Light,
                    shape = CircleShape
                )
                .background(
                    ChampionCartColors.Brand.CosmicPurple.copy(alpha = 0.05f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Logo placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .glass(
                    intensity = GlassIntensity.Medium,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = ChampionCartColors.Brand.ElectricMint
            )
        }

        Text(
            text = "ChampionCart",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ChampionCartTheme.colors.onSurface
        )

        Text(
            text = "חוסכים חכם, קונים נכון",
            style = MaterialTheme.typography.bodyLarge,
            color = ChampionCartTheme.colors.onSurfaceVariant
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isLoading: Boolean,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Email Field
        GlassTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "דוא״ל",
            placeholder = "your@email.com",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            isError = emailError != null,
            errorMessage = emailError,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        // Password Field
        GlassPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            label = "סיסמה",
            isError = passwordError != null,
            errorMessage = passwordError,
            enabled = !isLoading
        )

        // Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "שכחת סיסמה?",
                style = MaterialTheme.typography.bodyMedium,
                color = ChampionCartColors.Brand.ElectricMint,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.m))

        // Login Button
        GlassButton(
            onClick = onLoginClick,
            text = "התחברות",
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth(),
            icon = {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun SocialLoginSection(
    onGoogleLogin: () -> Unit,
    isEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "או",
                modifier = Modifier.padding(horizontal = Spacing.m),
                style = MaterialTheme.typography.bodyMedium,
                color = ChampionCartTheme.colors.onSurfaceVariant
            )
            Divider(modifier = Modifier.weight(1f))
        }

        SecondaryGlassButton(
            onClick = onGoogleLogin,
            text = "התחבר עם Google",
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth(),
            icon = {
                // Google icon placeholder
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun SignUpSection(
    onSignUpClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "אין לך חשבון? ",
            style = MaterialTheme.typography.bodyMedium,
            color = ChampionCartTheme.colors.onSurfaceVariant
        )
        Text(
            text = "הירשם עכשיו",
            style = MaterialTheme.typography.bodyMedium,
            color = ChampionCartColors.Brand.ElectricMint,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSignUpClick() }
        )
    }
}

// Login UI State
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false
)