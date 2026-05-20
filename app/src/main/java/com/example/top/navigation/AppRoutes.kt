package com.example.top.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Login : AppRoute("login")
    data object CreateAccount : AppRoute("create_account")
    data object ForgotPassword : AppRoute("forgot_password")
    data object Home : AppRoute("home")
    data object CreateGroup : AppRoute("create_group")
}
