package com.nathakusuma.neriva.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nathakusuma.neriva.ui.auth.LoginScreen
import com.nathakusuma.neriva.ui.auth.SignUpScreen
import com.nathakusuma.neriva.ui.onboarding.OnboardingScreen

/**
 * Main app navigation that manages screen transitions using Jetpack Navigation Component
 *
 * @param modifier Modifier to be applied to the NavHost
 * @param navController The NavController that manages navigation
 * @param startDestination The starting destination route
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding Screen
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        // Clear onboarding from back stack so user can't go back to it
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // Clear auth screens from back stack
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Sign Up Screen
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // Clear auth screens from back stack
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen (placeholder)
        composable(route = Screen.Home.route) {
            // TODO: Replace with actual HomeScreen implementation
            PlaceholderHomeScreen()
        }
    }
}

/**
 * Placeholder Home Screen
 * TODO: Replace with actual home screen implementation
 */
@Composable
private fun PlaceholderHomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = "Home Screen\n(Coming Soon)",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}
