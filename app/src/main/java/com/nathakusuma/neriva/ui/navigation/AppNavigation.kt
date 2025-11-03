package com.nathakusuma.neriva.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nathakusuma.neriva.ui.auth.LoginScreen
import com.nathakusuma.neriva.ui.auth.SignUpScreen
import com.nathakusuma.neriva.ui.home.HomeScreen
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
            HomeScreen(
                onNavigateToChat = {
                    // TODO: Implement chat screen navigation
                    // navController.navigate(Screen.Chat.route)
                },
                onNavigateToInbox = {
                    // TODO: Implement inbox screen navigation
                    // navController.navigate(Screen.Inbox.route)
                }
            )
        }
    }
}
