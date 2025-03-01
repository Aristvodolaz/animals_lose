package com.application.lose_animals.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.theme.ButtonShape
import com.application.lose_animals.ui.theme.ErrorRed
import com.application.lose_animals.ui.theme.SuccessGreen
import com.application.lose_animals.ui.viewModel.SosViewModel

@Composable
fun SosButton(
    context: Context,
    viewModel: SosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val sosSuccess by viewModel.sosSuccess.collectAsState()
    
    // Эффект для воспроизведения звука при успешной отправке SOS
    LaunchedEffect(sosSuccess) {
        if (sosSuccess) {
            playSosSound(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "Экстренная помощь",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Описание
        Text(
            text = "Нажмите кнопку SOS для отправки сигнала о помощи. Ваше текущее местоположение будет отправлено в службу поддержки.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Кнопка SOS
        Button(
            onClick = {
                viewModel.sendSos(
                    context = context,
                    onSuccess = {
                        // Успешная отправка обрабатывается через состояние sosSuccess
                    },
                    onError = { _error ->
                        // Ошибка обрабатывается через состояние errorMessage
                    }
                )
            },
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
            shape = ButtonShape,
            enabled = !isLoading && !sosSuccess
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White, 
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "SOS", 
                        color = Color.White, 
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Сообщение о статусе
        AnimatedVisibility(
            visible = sosSuccess || errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = if (sosSuccess) 
                    SuccessGreen.copy(alpha = 0.1f) 
                else 
                    ErrorRed.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (sosSuccess) 
                            Icons.Default.CheckCircle 
                        else 
                            Icons.Default.Error,
                        contentDescription = null,
                        tint = if (sosSuccess) SuccessGreen else ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (sosSuccess) 
                            "SOS сигнал успешно отправлен! Служба поддержки получила ваше местоположение." 
                        else 
                            errorMessage ?: "Произошла ошибка",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Кнопка возврата
        if (sosSuccess || errorMessage != null) {
            Button(
                onClick = { 
                    viewModel.resetState()
                    onNavigateBack() 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Вернуться")
            }
        }
    }
}

fun playSosSound(context: Context) {
    try {
        // Воспроизведение стандартного звука будильника
        val mediaPlayer = MediaPlayer.create(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
        mediaPlayer.start()
        
        // Освобождаем ресурсы после воспроизведения
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }

        // Вибрация
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(1000)
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка воспроизведения звука: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}