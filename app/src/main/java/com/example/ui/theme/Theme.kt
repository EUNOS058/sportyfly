package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = Color.White,
    primaryContainer = RedVariant,
    onPrimaryContainer = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = DarkNavyBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun SportyFlyTheme(
    themeMode: String = "Dark", // "Dark", "Light", "System"
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Light" -> false
        "System" -> isSystemInDarkTheme()
        else -> true // Default to Dark mode
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
