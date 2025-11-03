package com.nathakusuma.neriva.ui.navigation

/**
 * Sealed class representing all navigation destinations in the app
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object EditProfile : Screen("edit_profile")
    data object Mail : Screen("mail")
}
