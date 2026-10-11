package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = CampusBlueLight,
    onPrimaryContainer = NavyDark,
    secondary = CampusBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBAE6FD),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = StatusBusy,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = CampusBlue,
    onPrimary = Color.White,
    primaryContainer = NavyLight,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = NavyDark,
    background = Color(0xFF0B132B),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1C2541),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF283655),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color.Black
)

@Composable
fun FacultyFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
