package com.application.lose_animals.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.SosMessage
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.domain.repository.AuthRepository
import com.application.lose_animals.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    private val sosRepository: FirebaseSource,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _sosSuccess = MutableStateFlow(false)
    val sosSuccess: StateFlow<Boolean> = _sosSuccess.asStateFlow()

    fun resetState() {
        _isLoading.value = false
        _errorMessage.value = null
        _sosSuccess.value = false
    }

    fun sendSos(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        _sosSuccess.value = false

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser == null) {
                    _errorMessage.value = "Пользователь не авторизован"
                    _isLoading.value = false
                    onError("Пользователь не авторизован")
                    return@launch
                }

                val locationHelper = LocationHelper(context)
                locationHelper.getUserLocation(
                    onSuccess = { lat, lon ->
                        viewModelScope.launch {
                            try {
                                val sosMessage = SosMessage(
                                    userId = currentUser.id,
                                    userName = currentUser.username,
                                    latitude = lat,
                                    longitude = lon
                                )
                                sosRepository.sendSosMessage(sosMessage)
                                _sosSuccess.value = true
                                _isLoading.value = false
                                onSuccess()
                            } catch (e: Exception) {
                                _errorMessage.value = e.localizedMessage ?: "Ошибка отправки SOS"
                                _isLoading.value = false
                                onError(e.localizedMessage ?: "Ошибка отправки SOS")
                            }
                        }
                    },
                    onError = { error ->
                        _errorMessage.value = error
                        _isLoading.value = false
                        onError(error)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Ошибка отправки SOS"
                _isLoading.value = false
                onError(e.localizedMessage ?: "Ошибка отправки SOS")
            }
        }
    }
}
