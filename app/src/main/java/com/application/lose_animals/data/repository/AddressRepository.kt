package com.application.lose_animals.data.repository

import com.application.lose_animals.data.api.DaDataApiService
import com.application.lose_animals.data.model.AddressSuggestion
import com.application.lose_animals.data.model.DaDataRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val daDataApiService: DaDataApiService
) {
    /**
     * Получает предложения адресов на основе введенного текста
     */
    suspend fun getSuggestedAddresses(query: String): Result<List<AddressSuggestion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = daDataApiService.suggestAddress(DaDataRequest(query))
                Result.success(response.suggestions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Форматирует адрес для геокодирования
     */
    fun formatAddressForGeocoding(suggestion: AddressSuggestion): String {
        val data = suggestion.data
        
        // Формируем адрес в формате, подходящем для геокодирования
        val addressParts = mutableListOf<String>()
        
        data.country?.let { addressParts.add(it) }
        data.regionWithType?.let { addressParts.add(it) }
        data.cityWithType?.let { addressParts.add(it) }
        data.streetWithType?.let { addressParts.add(it) }
        data.house?.let { addressParts.add("дом $it") }
        
        return addressParts.joinToString(", ")
    }
    
    /**
     * Получает координаты из предложения адреса
     */
    fun getCoordinatesFromSuggestion(suggestion: AddressSuggestion): Pair<Double, Double>? {
        val lat = suggestion.data.geoLat?.toDoubleOrNull()
        val lon = suggestion.data.geoLon?.toDoubleOrNull()
        
        return if (lat != null && lon != null) {
            Pair(lat, lon)
        } else {
            null
        }
    }
} 