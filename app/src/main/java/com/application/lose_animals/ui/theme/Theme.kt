package com.application.lose_animals.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    secondary = LightGreen,
    onSecondary = Color.Black,
    tertiary = Orange,
    onTertiary = Color.Black,
    background = Color(0xFFF5F5F5), // Light grey background
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// New Colors for Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = Color.Black,
    secondary = DarkGreen,
    onSecondary = Color.White,
    tertiary = DarkOrange,
    onTertiary = Color.White,
    background = Color(0xFF121212), // Dark background
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun Lose_animalsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
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
