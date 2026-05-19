package com.example.top.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.top.ui.components.DarkGradientBackground
import com.example.top.ui.state.AuthState

@Composable
fun SplashScreen(
    authState: AuthState,
    onNavigateAuthenticated: () -> Unit,
    onNavigateUnauthenticated: () -> Unit
) {
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Authenticated -> onNavigateAuthenticated()
            AuthState.Unauthenticated -> onNavigateUnauthenticated()
            AuthState.Loading -> Unit
        }
    }

    DarkGradientBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Top Score", style = MaterialTheme.typography.displayMedium, color = Color.White, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}
