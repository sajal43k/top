package com.example.top.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.top.ui.screens.auth.CreateAccountScreen
import com.example.top.ui.screens.auth.ForgotPasswordScreen
import com.example.top.ui.screens.auth.LoginScreen
import com.example.top.ui.screens.home.HomeScreen
import com.example.top.ui.screens.splash.SplashScreen
import com.example.top.ui.state.AuthState
import com.example.top.ui.viewmodel.AuthViewModel
import com.example.top.ui.viewmodel.AuthViewModelFactory

@Composable
fun TopScoreApp() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(application))
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.authState) {
        val route = navController.currentBackStackEntry?.destination?.route
        if (authState.authState == AuthState.Unauthenticated && route == AppRoute.Home.route) {
            navController.navigate(AppRoute.Login.route) {
                popUpTo(AppRoute.Home.route) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = AppRoute.Splash.route) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                authState = authState.authState,
                onNavigateAuthenticated = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateUnauthenticated = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onCreateAccount = { navController.navigate(AppRoute.CreateAccount.route) },
                onForgotPassword = { navController.navigate(AppRoute.ForgotPassword.route) }
            )
        }
        composable(AppRoute.CreateAccount.route) {
            CreateAccountScreen(
                viewModel = authViewModel,
                onAccountCreated = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(AppRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(AppRoute.Home.route) {
            HomeScreen(onCreateGroup = {}, authViewModel = authViewModel)
        }
    }
}
