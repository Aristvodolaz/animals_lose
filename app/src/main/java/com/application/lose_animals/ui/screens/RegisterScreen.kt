package com.application.lose_animals.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.viewModel.AuthState
import com.application.lose_animals.ui.viewModel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel = hiltViewModel(), onRegistrationSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.register(email, password, username) }) {
            Text("Register")
        }

        when (authState) {
            is AuthState.Registered -> {
                Text("Registration successful!")
                LaunchedEffect(Unit) { onRegistrationSuccess() }
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Text(text = "Error: $message")
            }
            else -> {}
        }
    }
}
