package com.application.lose_animals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.components.CustomTextField
import com.application.lose_animals.ui.viewModel.AuthState
import com.application.lose_animals.ui.viewModel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel = hiltViewModel(), onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Button(
                onClick = {
                    isLoading = true
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Login", color = Color.White)
                }
            }

            when (authState) {
                is AuthState.Authenticated -> {
                    LaunchedEffect(Unit) {
                        delay(1000)
                        onLoginSuccess()
                    }
                    Text("Login successful!", color = Color.Green, modifier = Modifier.padding(top = 16.dp))
                }
                is AuthState.Error -> {
                    val message = (authState as AuthState.Error).message
                    Text(text = "Error: $message", color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = { onNavigateToRegister() }) {
                Text("Don't have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
