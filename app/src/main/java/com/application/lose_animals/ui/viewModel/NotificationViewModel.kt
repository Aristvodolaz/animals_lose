package com.application.lose_animals.ui.viewModel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.application.lose_animals.service.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE
    )
    
    // Получение настроек уведомлений
    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
    }
    
    fun areLocationNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOCATION_NOTIFICATIONS_ENABLED, false)
    }
    
    fun getNotificationRadius(): Float {
        return sharedPreferences.getFloat(KEY_NOTIFICATION_RADIUS, 10f)
    }
    
    fun areNewAnimalsNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NEW_ANIMALS_NOTIFICATIONS_ENABLED, false)
    }
    
    fun areFoundAnimalsNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_FOUND_ANIMALS_NOTIFICATIONS_ENABLED, false)
    }
    
    fun areChatNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_CHAT_NOTIFICATIONS_ENABLED, false)
    }
    
    // Сохранение настроек уведомлений
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        updateNotificationService()
    }
    
    fun setLocationNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_LOCATION_NOTIFICATIONS_ENABLED, enabled).apply()
        updateNotificationService()
    }
    
    fun setNotificationRadius(radius: Float) {
        sharedPreferences.edit().putFloat(KEY_NOTIFICATION_RADIUS, radius).apply()
        updateNotificationService()
    }
    
    fun setNewAnimalsNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NEW_ANIMALS_NOTIFICATIONS_ENABLED, enabled).apply()
        updateNotificationService()
    }
    
    fun setFoundAnimalsNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FOUND_ANIMALS_NOTIFICATIONS_ENABLED, enabled).apply()
        updateNotificationService()
    }
    
    fun setChatNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_CHAT_NOTIFICATIONS_ENABLED, enabled).apply()
        updateNotificationService()
    }
    
    // Обновление сервиса уведомлений при изменении настроек
    private fun updateNotificationService() {
        val notificationsEnabled = areNotificationsEnabled()
        val locationNotificationsEnabled = areLocationNotificationsEnabled()
        val newAnimalsNotificationsEnabled = areNewAnimalsNotificationsEnabled()
        val foundAnimalsNotificationsEnabled = areFoundAnimalsNotificationsEnabled()
        val chatNotificationsEnabled = areChatNotificationsEnabled()
        
        // Проверяем, нужно ли запустить или остановить сервис
        val shouldRunService = notificationsEnabled && (
            locationNotificationsEnabled || 
            newAnimalsNotificationsEnabled || 
            foundAnimalsNotificationsEnabled || 
            chatNotificationsEnabled
        )
        
        if (shouldRunService) {
            startNotificationService()
        } else {
            stopNotificationService()
        }
    }
    
    // Запуск сервиса уведомлений
    private fun startNotificationService() {
        val serviceIntent = Intent(context, NotificationService::class.java)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
    
    // Остановка сервиса уведомлений
    private fun stopNotificationService() {
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)
    }
    
    // Запрос разрешений для уведомлений
    fun requestPermissions(activity: Activity) {
        // Проверяем и запрашиваем разрешение на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        
        // Проверяем и запрашиваем разрешение на отправку уведомлений (для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    companion object {
        private const val NOTIFICATION_PREFERENCES = "notification_preferences"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LOCATION_NOTIFICATIONS_ENABLED = "location_notifications_enabled"
        private const val KEY_NOTIFICATION_RADIUS = "notification_radius"
        private const val KEY_NEW_ANIMALS_NOTIFICATIONS_ENABLED = "new_animals_notifications_enabled"
        private const val KEY_FOUND_ANIMALS_NOTIFICATIONS_ENABLED = "found_animals_notifications_enabled"
        private const val KEY_CHAT_NOTIFICATIONS_ENABLED = "chat_notifications_enabled"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }
} 