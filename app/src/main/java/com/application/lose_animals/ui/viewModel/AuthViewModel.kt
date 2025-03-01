package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.repository.AuthRepositoryImpl
import com.application.lose_animals.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                val success = authRepository.registerUser(email, password, username)
                _authState.value = if (success) AuthState.Registered else AuthState.Error("Registration failed")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val success = authRepository.loginUser(email, password)
                _authState.value = if (success) AuthState.Authenticated else AuthState.Error("Login failed")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }
}

// Состояния аутентификации
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Registered : AuthState()
    data class Error(val message: String) : AuthState()
}
