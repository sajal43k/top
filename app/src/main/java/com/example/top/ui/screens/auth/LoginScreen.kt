package com.example.top.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.top.ui.components.AuthCard
import com.example.top.ui.components.DarkGradientBackground
import com.example.top.ui.components.InlineLink
import com.example.top.ui.components.PrimaryActionButton
import com.example.top.ui.components.ScreenTitle
import com.example.top.ui.components.SecondaryActionButton
import com.example.top.ui.components.TopScoreTextField
import com.example.top.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    DarkGradientBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            ScreenTitle("Login", "Use your name or phone number and password to open your score groups.")
            AuthCard {
                TopScoreTextField(email, { email = it }, "Email")
                TopScoreTextField(password, { password = it }, "Password", visualTransformation = PasswordVisualTransformation())
                PrimaryActionButton("Login", uiState.isLoading) {
                    viewModel.login(email, password, onLoginSuccess)
                }
                SecondaryActionButton("Create account", onCreateAccount)
                InlineLink("Forgot password?", onForgotPassword)
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}
