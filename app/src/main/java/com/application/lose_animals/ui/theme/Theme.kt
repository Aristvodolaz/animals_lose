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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,
    
    secondary = AccentOrange,
    onSecondary = Color.White,
    secondaryContainer = OrangeLight,
    onSecondaryContainer = OrangeDark,
    
    tertiary = AccentGray,
    onTertiary = Color.White,
    tertiaryContainer = Gray200,
    onTertiaryContainer = Gray700,
    
    background = Gray50,
    onBackground = Gray900,
    
    surface = Color.White,
    onSurface = Gray900,
    
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.Black,
    primaryContainer = OrangeDark,
    onPrimaryContainer = OrangeLight,
    
    secondary = AccentOrange,
    onSecondary = Color.Black,
    secondaryContainer = OrangeDark,
    onSecondaryContainer = OrangeLight,
    
    tertiary = AccentGray,
    onTertiary = Color.Black,
    tertiaryContainer = Gray700,
    onTertiaryContainer = Gray200,
    
    background = Gray900,
    onBackground = Gray100,
    
    surface = Gray700,
    onSurface = Gray100,
    
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,
    
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun Lose_animalsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Отключаем динамические цвета по умолчанию для использования нашей темы
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
