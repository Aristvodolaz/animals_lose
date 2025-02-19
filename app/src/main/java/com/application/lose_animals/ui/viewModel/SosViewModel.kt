package com.application.lose_animals.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.SosMessage
import com.application.lose_animals.data.source.FirebaseSource
import com.application.lose_animals.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    private val sosRepository: FirebaseSource
) : ViewModel() {

    fun sendSos(context: Context, userId: String, userName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val locationHelper = LocationHelper(context)

        locationHelper.getUserLocation { lat, lon ->
            viewModelScope.launch {
                try {
                    val sosMessage = SosMessage(
                        userId = userId,
                        userName = userName,
                        latitude = lat,
                        longitude = lon
                    )
                    sosRepository.sendSosMessage(sosMessage)
                    onSuccess()
                } catch (e: Exception) {
                    onError(e.localizedMessage ?: "Ошибка отправки SOS")
                }
            }
        }
    }
}
