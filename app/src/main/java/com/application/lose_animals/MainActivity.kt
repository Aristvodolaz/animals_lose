package com.application.lose_animals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.lose_animals.ui.theme.Lose_animalsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплэш-экран перед вызовом super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        setContent {
            Lose_animalsTheme {
                LostAnimalsApp() // Основной Composable
            }
        }
    }
}
