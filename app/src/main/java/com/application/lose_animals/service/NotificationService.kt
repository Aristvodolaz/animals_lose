package com.application.lose_animals.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.lose_animals.MainActivity
import com.application.lose_animals.R
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.data.repository.PersonRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.location.Geocoder
import android.location.Address
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.resume
import java.util.Locale

@AndroidEntryPoint
class NotificationService : Service() {

    @Inject
    lateinit var personRepository: PersonRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    
    private lateinit var sharedPreferences: SharedPreferences
    private var currentLocation: Location? = null
    private var notificationRadius: Float = 10f
    
    private val recentlyNotifiedPersons = mutableSetOf<String>()
    
    private val personsChildEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val person = snapshot.getValue(Person::class.java) ?: return
            checkAndNotify(person)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val person = snapshot.getValue(Person::class.java) ?: return
            checkAndNotify(person)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    override fun onCreate() {
        super.onCreate()
        
        sharedPreferences = getSharedPreferences("notification_preferences", Context.MODE_PRIVATE)
        
        // Создаем канал уведомлений
        createNotificationChannel()
        
        // Запускаем сервис в режиме переднего плана
        startForeground(NOTIFICATION_ID, createForegroundNotification())
        
        // Инициализируем клиент местоположения
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Настраиваем обратный вызов для обновления местоположения
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocation = location
                    // Обновляем радиус уведомлений из настроек
                    notificationRadius = sharedPreferences.getFloat("notification_radius", 10f)
                }
            }
        }
        
        // Запускаем отслеживание местоположения
        startLocationUpdates()
        
        // Начинаем слушать изменения в базе данных
        startDatabaseListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Если сервис перезапущен системой, продолжаем работу
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Останавливаем отслеживание местоположения
        stopLocationUpdates()
        
        // Останавливаем слушатель базы данных
        stopDatabaseListener()
        
        // Отменяем все корутины
        serviceJob.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.notification_service_running))
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                TimeUnit.MINUTES.toMillis(15)
            ).build()
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Обработка отсутствия разрешений
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startDatabaseListener() {
        FirebaseDatabase.getInstance().getReference("persons")
            .addChildEventListener(personsChildEventListener)
    }

    private fun stopDatabaseListener() {
        FirebaseDatabase.getInstance().getReference("persons")
            .removeEventListener(personsChildEventListener)
    }

    private fun checkAndNotify(person: Person) {
        // Проверяем, включены ли уведомления
        if (!sharedPreferences.getBoolean("notifications_enabled", false)) {
            return
        }
        
        // Проверяем, не отправляли ли мы уже уведомление для этого животного
        if (recentlyNotifiedPersons.contains(person.id)) {
            return
        }
        
        // Проверяем тип уведомления в зависимости от статуса животного
        val isNewAnimal = person.status == "Пропал" && 
                sharedPreferences.getBoolean("new_animals_notifications_enabled", false)
        
        val isFoundAnimal = person.status == "Найден" && 
                sharedPreferences.getBoolean("found_animals_notifications_enabled", false)
        
        // Если не подходит ни один тип уведомления, выходим
        if (!isNewAnimal && !isFoundAnimal) {
            return
        }
        
        // Проверяем, включены ли уведомления о животных поблизости
        val locationNotificationsEnabled = 
            sharedPreferences.getBoolean("location_notifications_enabled", false)
        
        if (locationNotificationsEnabled && currentLocation != null) {
            // Проверяем расстояние до животного
            serviceScope.launch {
                val personLocation = getLocationFromAddress(person.lastSeenLocation)
                
                if (personLocation != null) {
                    val distance = calculateDistance(
                        currentLocation!!.latitude, currentLocation!!.longitude,
                        personLocation.first, personLocation.second
                    )
                    
                    // Если животное находится в радиусе уведомлений, отправляем уведомление
                    if (distance <= notificationRadius) {
                        sendNotification(person, distance)
                        recentlyNotifiedPersons.add(person.id)
                    }
                } else {
                    // Если не удалось получить координаты, но уведомления о новых/найденных животных включены
                    // отправляем уведомление без информации о расстоянии
                    if ((isNewAnimal || isFoundAnimal) && !locationNotificationsEnabled) {
                        sendNotification(person, null)
                        recentlyNotifiedPersons.add(person.id)
                    }
                }
            }
        } else {
            // Если уведомления о местоположении выключены, но уведомления о новых/найденных животных включены
            if (isNewAnimal || isFoundAnimal) {
                sendNotification(person, null)
                recentlyNotifiedPersons.add(person.id)
            }
        }
    }

    private fun sendNotification(person: Person, distance: Float?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Создаем Intent для открытия экрана с деталями человека
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("PERSON_ID", person.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        // Формируем текст уведомления
        val title = when (person.status) {
            "Пропал" -> "Пропавший человек поблизости"
            "Найден" -> "Найден человек поблизости"
            else -> "Новое объявление о пропавшем"
        }
        
        val text = if (distance != null) {
            "${person.name} (${person.status.lowercase()}) в ${distance.toInt()} км от вас: ${person.lastSeenLocation}"
        } else {
            "${person.name} (${person.status.lowercase()}): ${person.lastSeenLocation}"
        }
        
        // Создаем и отправляем уведомление
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(person.id.hashCode(), notification)
    }

    private suspend fun getLocationFromAddress(address: String): Pair<Double, Double>? {
        // Реализация геокодирования адреса в координаты
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            
            // Проверяем доступность Geocoder API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Для Android 13+ используем асинхронный API
                val result = suspendCancellableCoroutine<Pair<Double, Double>?> { continuation ->
                    geocoder.getFromLocationName(address, 1) { addresses ->
                        if (addresses.isNotEmpty() && addresses[0] != null) {
                            val location = addresses[0]
                            continuation.resume(Pair(location.latitude, location.longitude))
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
                return result
            } else {
                // Для более старых версий Android используем синхронный API
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(address, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val location = addresses[0]
                    return Pair(location.latitude, location.longitude)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка геокодирования: ${e.message}")
        }
        
        return null
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        // Преобразуем метры в километры
        return results[0] / 1000
    }

    companion object {
        private const val CHANNEL_ID = "nearby_animals_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "NotificationService"
    }
} 