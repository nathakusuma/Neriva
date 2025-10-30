package com.nathakusuma.neriva.ui.navigation

import androidx.compose.runtime.*
import com.nathakusuma.neriva.ui.auth.LoginScreen
import com.nathakusuma.neriva.ui.auth.SignUpScreen
import com.nathakusuma.neriva.ui.onboarding.OnboardingScreen

/**
 * Navigation states for the app
 */
enum class Screen {
    ONBOARDING,
    LOGIN,
    SIGNUP,
    HOME // TODO: Implement home screen
}

/**
 * Main app navigation that manages screen transitions
 *
 * TODO(NAVIGATION): Replace this with proper Jetpack Navigation Component when implementing more complex navigation flows
 */
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.ONBOARDING) }

    when (currentScreen) {
        Screen.ONBOARDING -> {
            OnboardingScreen(
                onFinish = {
                    currentScreen = Screen.LOGIN
                }
            )
        }
        Screen.LOGIN -> {
            LoginScreen(
                onNavigateToSignUp = {
                    currentScreen = Screen.SIGNUP
                },
                onLoginSuccess = {
                    // TODO(NAVIGATION): Navigate to Home/Dashboard screen
                    currentScreen = Screen.HOME
                    println("Login successful - Navigate to Home")
                }
            )
        }
        Screen.SIGNUP -> {
            SignUpScreen(
                onNavigateToLogin = {
                    currentScreen = Screen.LOGIN
                },
                onSignUpSuccess = {
                    // TODO(NAVIGATION): Navigate to Home/Dashboard screen
                    // Option 1: Go to home directly
                    currentScreen = Screen.HOME
                    println("Sign up successful - Navigate to Home")
                }
            )
        }
        Screen.HOME -> {
            // TODO: Implement home screen
            // HomeScreen()
        }
    }
}
