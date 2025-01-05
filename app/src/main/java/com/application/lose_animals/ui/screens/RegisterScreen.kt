package com.application.lose_animals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.viewModel.AuthState
import com.application.lose_animals.ui.viewModel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel = hiltViewModel(), onRegistrationSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            val message = (authState as AuthState.Error).message
            snackbarHostState.showSnackbar(message)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Создайте учетную запись",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Электронная почта"
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль",
                isPassword = true // Передаем true для поля пароля
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Имя пользователя"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.register(email, password, username) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться")
            }

            when (authState) {
                is AuthState.Registered -> {
                    LaunchedEffect(Unit) { onRegistrationSuccess() }
                }
                is AuthState.Error -> {
                    val message = (authState as AuthState.Error).message
                    Text(text = "Ошибка: $message", color = Color.Red)
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { onNavigateToLogin() }) {
                Text("Уже есть учетная запись? Войти")
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
