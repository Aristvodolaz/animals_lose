package com.application.lose_animals.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.*

class LocationHelper(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getUserLocation(onSuccess: (latitude: Double, longitude: Double) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onSuccess(it.latitude, it.longitude)
            }
        }
    }


}
