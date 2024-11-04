package com.application.lose_animals
import androidx.activity.compose.setContent


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.application.lose_animals.ui.theme.Lose_animalsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lose_animalsTheme{
                LostAnimalsApp() // Основной Composable
            }
        }
    }
}
