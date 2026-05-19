package com.example.top.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TopDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color(0xFF001D2E),
    secondary = MintGreen,
    tertiary = RoyalBlue,
    background = NightBackground,
    onBackground = Color.White,
    surface = NightSurface,
    onSurface = Color.White,
    surfaceVariant = NightCard,
    onSurfaceVariant = SoftText,
    error = ErrorRed
)

@Composable
fun TopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TopDarkColorScheme,
        typography = Typography,
        content = content
    )
}
