package com.application.lose_animals.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.ui.viewModel.NotificationViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Состояния настроек уведомлений
    var notificationsEnabled by remember { mutableStateOf(viewModel.areNotificationsEnabled()) }
    var locationNotificationsEnabled by remember { mutableStateOf(viewModel.areLocationNotificationsEnabled()) }
    var notificationRadius by remember { mutableStateOf(viewModel.getNotificationRadius()) }
    var newAnimalsNotifications by remember { mutableStateOf(viewModel.areNewAnimalsNotificationsEnabled()) }
    var foundAnimalsNotifications by remember { mutableStateOf(viewModel.areFoundAnimalsNotificationsEnabled()) }
    var chatNotifications by remember { mutableStateOf(viewModel.areChatNotificationsEnabled()) }
    
    // Проверка разрешений
    var locationPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    var notificationPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    // Состояние для отслеживания запроса разрешений
    var showPermissionRationale by remember { mutableStateOf(false) }
    
    // Обработчик запроса разрешений
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        
        if (allGranted) {
            // Все разрешения предоставлены, обновляем состояние
            locationPermissionGranted = true
            notificationPermissionGranted = true
        } else {
            // Не все разрешения предоставлены, показываем объяснение
            showPermissionRationale = true
        }
    }
    
    // Сохранение настроек при изменении
    fun saveSettings() {
        viewModel.setNotificationsEnabled(notificationsEnabled)
        viewModel.setLocationNotificationsEnabled(locationNotificationsEnabled)
        viewModel.setNotificationRadius(notificationRadius)
        viewModel.setNewAnimalsNotificationsEnabled(newAnimalsNotifications)
        viewModel.setFoundAnimalsNotificationsEnabled(foundAnimalsNotifications)
        viewModel.setChatNotificationsEnabled(chatNotifications)
    }
    
    // Функция для запроса разрешений
    fun requestPermissions() {
        val activity = context as? Activity
        if (activity != null) {
            viewModel.requestPermissions(activity)
        } else {
            val permissionsToRequest = mutableListOf<String>()
            
            if (!locationPermissionGranted) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            
            if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            
            if (permissionsToRequest.isNotEmpty()) {
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }
    
    // Диалог с объяснением необходимости разрешений
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Требуются разрешения") },
            text = { 
                Text(
                    "Для работы уведомлений о пропавших людях поблизости необходимо предоставить разрешения на доступ к местоположению и отправку уведомлений. Пожалуйста, предоставьте эти разрешения в настройках приложения."
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        // Открываем настройки приложения
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Открыть настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки уведомлений",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Карточка с информацией о разрешениях
            if (!locationPermissionGranted || !notificationPermissionGranted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "Требуются разрешения",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "Для работы уведомлений о пропавших людях поблизости необходимо предоставить следующие разрешения:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        if (!locationPermissionGranted) {
                            Text(
                                text = "• Доступ к местоположению",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        if (!notificationPermissionGranted) {
                            Text(
                                text = "• Разрешение на отправку уведомлений",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { requestPermissions() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                contentColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text("Предоставить разрешения")
                        }
                    }
                }
            }
            
            // Основной переключатель уведомлений
            SettingsSwitchItem(
                title = "Уведомления",
                description = "Включить все уведомления",
                icon = Icons.Outlined.Notifications,
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    if (!it) {
                        // Если основные уведомления выключены, выключаем и остальные
                        locationNotificationsEnabled = false
                        newAnimalsNotifications = false
                        foundAnimalsNotifications = false
                        chatNotifications = false
                    }
                    saveSettings()
                }
            )
            
            AnimatedVisibility(
                visible = notificationsEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Уведомления о животных поблизости
                    SettingsSwitchItem(
                        title = "Уведомления о пропавших людях поблизости",
                        description = "Получать уведомления о пропавших и найденных людях рядом с вами",
                        icon = Icons.Default.LocationOn,
                        checked = locationNotificationsEnabled,
                        onCheckedChange = {
                            locationNotificationsEnabled = it
                            saveSettings()
                        },
                        enabled = notificationsEnabled && locationPermissionGranted
                    )
                    
                    // Настройка радиуса уведомлений
                    AnimatedVisibility(
                        visible = locationNotificationsEnabled && notificationsEnabled,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 40.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Радиус уведомлений: ${notificationRadius.toInt()} км",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Slider(
                                    value = notificationRadius,
                                    onValueChange = {
                                        notificationRadius = it
                                        saveSettings()
                                    },
                                    valueRange = 1f..50f,
                                    steps = 49,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Text(
                                    text = "Вы будете получать уведомления о людях в радиусе ${notificationRadius.toInt()} км от вашего текущего местоположения",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Уведомления о новых животных
                    SettingsSwitchItem(
                        title = "Новые пропавшие люди",
                        description = "Получать уведомления о новых пропавших людях",
                        icon = Icons.Default.Person,
                        checked = newAnimalsNotifications,
                        onCheckedChange = {
                            newAnimalsNotifications = it
                            saveSettings()
                        },
                        enabled = notificationsEnabled
                    )
                    
                    // Уведомления о найденных животных
                    SettingsSwitchItem(
                        title = "Найденные люди",
                        description = "Получать уведомления, когда человек отмечен как найденный",
                        icon = Icons.Default.CheckCircle,
                        checked = foundAnimalsNotifications,
                        onCheckedChange = {
                            foundAnimalsNotifications = it
                            saveSettings()
                        },
                        enabled = notificationsEnabled
                    )
                    
                    // Уведомления о сообщениях в чате
                    SettingsSwitchItem(
                        title = "Сообщения в чате",
                        description = "Получать уведомления о новых сообщениях в чате",
                        icon = Icons.Default.Chat,
                        checked = chatNotifications,
                        onCheckedChange = {
                            chatNotifications = it
                            saveSettings()
                        },
                        enabled = notificationsEnabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Информационная карточка
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "О системе уведомлений",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = "Система уведомлений помогает быстрее находить пропавших людей, оповещая вас о новых случаях поблизости.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Text(
                        text = "Для работы геолокационных уведомлений необходимо разрешение на доступ к местоположению устройства.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Text(
                        text = "Вы можете настроить типы уведомлений и радиус поиска в соответствии с вашими предпочтениями.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
} 