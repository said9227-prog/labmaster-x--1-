package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTeal400,
    onPrimary = MedicalNavy900,
    primaryContainer = MedicalNavy700,
    onPrimaryContainer = MedicalCyan400,
    secondary = MedicalCyan400,
    onSecondary = MedicalNavy900,
    tertiary = MedicalGold400,
    onTertiary = MedicalNavy900,
    background = MedicalNavy900,
    onBackground = Color.White,
    surface = MedicalNavy800,
    onSurface = Color.White,
    surfaceVariant = MedicalNavy700,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = MedicalCoral400,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTeal700,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = MedicalTeal700,
    secondary = MedicalCyan700,
    onSecondary = Color.White,
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = MedicalNavy50,
    onBackground = MedicalNavy900,
    surface = Color.White,
    onSurface = MedicalNavy900,
    surfaceVariant = MedicalNavy100,
    onSurfaceVariant = MedicalNavy700,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun LabMasterXTheme(
    darkTheme: Boolean = true, // Dark Mode is the default as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
