package com.application.lose_animals.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.lose_animals.data.model.AddressSuggestion
import com.application.lose_animals.data.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    // Состояние для текста в поле ввода адреса
    private val _addressQuery = MutableStateFlow("")
    val addressQuery = _addressQuery.asStateFlow()

    // Состояние для списка предложений адресов
    private val _addressSuggestions = MutableStateFlow<List<AddressSuggestion>>(emptyList())
    val addressSuggestions = _addressSuggestions.asStateFlow()

    // Состояние для выбранного адреса
    private val _selectedAddress = MutableStateFlow<AddressSuggestion?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Координаты выбранного адреса
    private val _coordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val coordinates = _coordinates.asStateFlow()

    init {
        // Настраиваем debounce для запросов к API
        addressQuery
            .debounce(300) // Задержка в 300 мс перед отправкой запроса
            .filter { it.length >= 3 } // Фильтруем запросы короче 3 символов
            .distinctUntilChanged() // Избегаем дублирования запросов
            .onEach { query ->
                if (query.isNotEmpty()) {
                    fetchAddressSuggestions(query)
                } else {
                    _addressSuggestions.value = emptyList()
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Обновляет текст запроса адреса
     */
    fun updateAddressQuery(query: String) {
        _addressQuery.value = query
    }

    /**
     * Выбирает предложение адреса
     */
    fun selectAddress(suggestion: AddressSuggestion) {
        _selectedAddress.value = suggestion
        _addressQuery.value = suggestion.value
        _addressSuggestions.value = emptyList()
        
        // Получаем координаты из выбранного адреса
        addressRepository.getCoordinatesFromSuggestion(suggestion)?.let { coords ->
            _coordinates.value = coords
        }
    }

    /**
     * Очищает выбранный адрес
     */
    fun clearSelectedAddress() {
        _selectedAddress.value = null
        _addressQuery.value = ""
        _coordinates.value = null
    }

    /**
     * Получает форматированный адрес для геокодирования
     */
    fun getFormattedAddress(): String? {
        return _selectedAddress.value?.let { suggestion ->
            addressRepository.formatAddressForGeocoding(suggestion)
        }
    }

    /**
     * Получает предложения адресов из API
     */
    private fun fetchAddressSuggestions(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            addressRepository.getSuggestedAddresses(query)
                .onSuccess { suggestions ->
                    _addressSuggestions.value = suggestions
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Ошибка при получении адресов"
                    _addressSuggestions.value = emptyList()
                    _isLoading.value = false
                }
        }
    }
} 