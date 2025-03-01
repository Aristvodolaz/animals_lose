package com.application.lose_animals.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlin.math.*

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    
    @SuppressLint("MissingPermission")
    fun getUserLocation(onSuccess: (latitude: Double, longitude: Double) -> Unit, onError: (String) -> Unit = {}) {
        try {
            // Сначала пробуем получить последнее известное местоположение
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location.latitude, location.longitude)
                } else {
                    // Если последнее местоположение недоступно, запрашиваем обновление
                    requestLocationUpdate(onSuccess, onError)
                }
            }.addOnFailureListener { e ->
                onError("Ошибка получения местоположения: ${e.localizedMessage}")
            }
        } catch (e: Exception) {
            onError("Ошибка доступа к службе местоположения: ${e.localizedMessage}")
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate(onSuccess: (latitude: Double, longitude: Double) -> Unit, onError: (String) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()
            
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    onSuccess(location.latitude, location.longitude)
                    fusedLocationClient.removeLocationUpdates(this)
                    return
                }
                onError("Не удалось получить текущее местоположение")
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            
            // Устанавливаем таймаут для запроса местоположения
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                fusedLocationClient.removeLocationUpdates(locationCallback)
                onError("Превышено время ожидания для получения местоположения")
            }, 15000) // 15 секунд таймаут
        } catch (e: Exception) {
            onError("Ошибка запроса обновления местоположения: ${e.localizedMessage}")
        }
    }
}
