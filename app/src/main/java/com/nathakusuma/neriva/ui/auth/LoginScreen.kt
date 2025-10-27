package com.nathakusuma.neriva.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nathakusuma.neriva.ui.theme.NerivaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Login screen for user authentication
 *
 * @param modifier Modifier to be applied to the root composable
 * @param onNavigateToSignUp Callback when user wants to navigate to sign up screen
 * @param onLoginSuccess Callback when login is successful
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Hi, Welcome Back! 👋",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Hello again, you've been missed!",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // Email
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Email Address",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = errorMessage != null
                )
            }

            // Password
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = { Text("Please Enter Your Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Text(
                                text = if (isPasswordVisible) "Hide" else "Show",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focus.clearFocus() }
                    ),
                    isError = errorMessage != null
                )
            }

            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            // Login Button
            Button(
                onClick = {
                    focus.clearFocus()
                    // TODO(API): Replace this with REST API integration,
                    // including proper error handling, token storage, and loading states.
                    // viewModel.login(email, password)

                    isLoading = true
                    scope.launch {
                        try {
                            // Mock API call
                            delay(1000)
                            // Simulate validation
                            if (email.contains("@") && password.length >= 8) {
                                onLoginSuccess()
                            } else {
                                errorMessage = "Invalid email or password"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Login failed"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

        // Bottom "Sign Up" link
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sign Up",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable {
                    onNavigateToSignUp()
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewLoginScreen() {
    NerivaTheme {
        LoginScreen()
    }
}

