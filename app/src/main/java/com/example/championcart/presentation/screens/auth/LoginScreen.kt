package com.example.championcart.presentation.screens.auth

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.auth.SocialLoginButton
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            snackbarHostState.showSnackbar("התחברת בהצלחה!")
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
            AuthHeader(
                modifier = Modifier.padding(top = 48.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l)
            ) {
                // Title
                Text(
                    text = "ברוכים השבים!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "התחבר כדי להתחיל לחסוך",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.m))

                // Email Field
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

                // Password Field
                PasswordField(
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    placeholder = "הכנס סיסמה",
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError,
                    enabled = !uiState.isLoading
                )

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = viewModel::onRememberMeChange,
                            enabled = !uiState.isLoading,
                            colors = CheckboxDefaults.colors(
                                checkedColor = BrandColors.ElectricMint
                            )
                        )
                        Text(
                            text = "זכור אותי",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                viewModel.onRememberMeChange(!uiState.rememberMe)
                            }
                        )
                    }

                    TextButton(
                        text = "שכחת סיסמה?",
                        onClick = onNavigateToForgotPassword,
                        color = BrandColors.ElectricMint
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.m))

                // Login Button
                PrimaryButton(
                    text = "התחבר",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.login()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isLoginEnabled && !uiState.isLoading,
                    isLoading = uiState.isLoading
                )

                // Or Divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.l),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChampionDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "או",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Spacing.m)
                    )
                    ChampionDivider(modifier = Modifier.weight(1f))
                }

                // Guest Mode
                SecondaryButton(
                    text = "המשך כאורח",
                    onClick = onGuestMode,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.PersonOutline,
                    enabled = !uiState.isLoading
                )

                // Social Login (Future)
                if (false) { // Hidden for now
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.m)
                    ) {
                        SocialLoginButton(
                            text = "התחבר עם Google",
                            provider = SocialProvider.Google,
                            onClick = { /* TODO */ },
                            modifier = Modifier.fillMaxWidth()
                        )

                        SocialLoginButton(
                            text = "התחבר עם Facebook",
                            provider = SocialProvider.Facebook,
                            onClick = { /* TODO */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Register Link
            Row(
                modifier = Modifier.padding(bottom = Spacing.xl),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "אין לך חשבון? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "הירשם עכשיו",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandColors.ElectricMint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Composable
private fun AuthHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Card(
            modifier = Modifier.size(100.dp),
            shape = Shapes.cardLarge,
            colors = CardDefaults.cardColors(
                containerColor = BrandColors.ElectricMint
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.l))

        Text(
            text = "ChampionCart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = BrandColors.ElectricMint
        )
    }
}

// Social provider enum
enum class SocialProvider {
    Google, Facebook
}