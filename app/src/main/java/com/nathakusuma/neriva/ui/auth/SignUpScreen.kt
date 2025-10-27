package com.nathakusuma.neriva.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.nathakusuma.neriva.ui.theme.NerivaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Sign Up screen for user registration
 *
 * @param modifier Modifier to be applied to the root composable
 * @param onNavigateToLogin Callback when user wants to navigate to login screen
 * @param onSignUpSuccess Callback when sign up is successful
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    fun isValid(): Boolean {
        if (name.isBlank()) {
            error = "Name is required"
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            error = "Enter a valid email"
            return false
        }
        if (password.length < 6) {
            error = "Password must be at least 6 characters"
            return false
        }
        error = null
        return true
    }

    suspend fun performSignUp() {
        // TODO(REST API): Replace with real REST call
        delay(1000)
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
                value = name,
                onValueChange = {
                    name = it
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                isError = error != null && name.isBlank()
            )

            // Email
            FieldLabel("Email Address")
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = error != null && !email.contains("@")
            )

            // Password
            FieldLabel("Password")
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Please Enter Your Password") },
                singleLine = true,
                visualTransformation = if (passwordHidden) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                trailingIcon = {
                    TextButton(onClick = { passwordHidden = !passwordHidden }) {
                        Text(
                            text = if (passwordHidden) "Show" else "Hide",
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
                isError = error != null && password.length < 8
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    focus.clearFocus()
                    if (!isValid()) return@Button
                    isLoading = true
                    scope.launch {
                        try {
                            performSignUp() // TODO(REST API)
                            onSignUpSuccess()
                        } catch (t: Throwable) {
                            error = t.message ?: "Sign up failed"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
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
