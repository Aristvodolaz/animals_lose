package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.domain.usecase.UpdatePersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPersonViewModel @Inject constructor(
    private val updatePersonUseCase: UpdatePersonUseCase
) : ViewModel() {

    fun updatePerson(person: Person, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                updatePersonUseCase(person)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
