package com.nathakusuma.neriva.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathakusuma.neriva.ui.theme.NerivaTheme

/**
 * Sign Up screen for user registration
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel ViewModel for managing sign up state and business logic
 * @param onNavigateToLogin Callback when user wants to navigate to login screen
 * @param onSignUpSuccess Callback when sign up is successful
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val focus = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    // Handle sign up success
    LaunchedEffect(uiState.isSignUpSuccessful) {
        if (uiState.isSignUpSuccessful) {
            onSignUpSuccess()
            viewModel.resetSignUpSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Create an account",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Connect with your friends today!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(4.dp))

            // Name
            FieldLabel("Name")
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                isError = uiState.errorMessage != null && uiState.name.isBlank()
            )

            // Email
            FieldLabel("Email Address")
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = uiState.errorMessage != null && !uiState.email.contains("@")
            )

            // Password
            FieldLabel("Password")
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Please Enter Your Password") },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = viewModel::togglePasswordVisibility) {
                        Text(
                            text = if (uiState.isPasswordVisible) "Show" else "Hide",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focus.clearFocus()
                    }
                ),
                isError = uiState.errorMessage != null && uiState.password.length < 6
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    focus.clearFocus()
                    viewModel.signUp()
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        "Sign Up",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Footer: "Already have an account? Login"
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    onNavigateToLogin()
                }
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    NerivaTheme {
        SignUpScreen()
    }
}
