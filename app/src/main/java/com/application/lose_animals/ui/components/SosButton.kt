package com.application.lose_animals.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.viewModel.SosViewModel

@Composable
fun SosButton(
    context: Context,
    userId: String,
    userName: String,
    viewModel: SosViewModel = hiltViewModel()
) {
    var isSending by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isSending = true
            viewModel.sendSos(
                context = context,
                userId = userId,
                userName = userName,
                onSuccess = {
                    isSending = false
                    playSosSound(context)
                    Toast.makeText(context, "SOS отправлен!", Toast.LENGTH_LONG).show()
                },
                onError = { error ->
                    isSending = false
                    Toast.makeText(context, "Ошибка: $error", Toast.LENGTH_LONG).show()
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(50)
    ) {
        if (isSending) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text("🔴 SOS", color = Color.White)
        }
    }
}


fun playSosSound(context: Context) {
    // Воспроизведение стандартного звука будильника
    val mediaPlayer = MediaPlayer.create(
        context,
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    )
    mediaPlayer.start()

    // Вибрация
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(500)
    }
}