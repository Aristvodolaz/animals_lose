package com.application.lose_animals.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.application.lose_animals.data.model.Person
import com.application.lose_animals.ui.viewModel.PersonListViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognitionScreen(
    onNavigateBack: () -> Unit = {},
    onPersonClick: (String) -> Unit = {},
    viewModel: PersonListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val persons by viewModel.persons.collectAsState()
    
    // Состояния для управления процессом распознавания
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recognitionResults by remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }
    var matchedPersons by remember { mutableStateOf<List<Person>>(emptyList()) }
    var showNoMatchDialog by remember { mutableStateOf(false) }
    
    // Запуск камеры для получения фото
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImageBitmap = it
            selectedImageUri = null
            analyzeImage(it, persons) { results, matches ->
                recognitionResults = results
                matchedPersons = matches
                isAnalyzing = false
                if (matches.isEmpty() && results.isNotEmpty()) {
                    showNoMatchDialog = true
                }
            }
            isAnalyzing = true
        }
    }
    
    // Выбор изображения из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            selectedImageBitmap = null
            try {
                val bitmap = InputImage.fromFilePath(context, it).bitmapInternal
                bitmap?.let { safeBitmap ->
                    analyzeImage(safeBitmap, persons) { results, matches ->
                        recognitionResults = results
                        matchedPersons = matches
                        isAnalyzing = false
                        if (matches.isEmpty() && results.isNotEmpty()) {
                            showNoMatchDialog = true
                        }
                    }
                } ?: run {
                    isAnalyzing = false
                }
            } catch (e: IOException) {
                isAnalyzing = false
            }
        }
    }
    
    // Диалог, если не найдено совпадений
    if (showNoMatchDialog) {
        AlertDialog(
            onDismissRequest = { showNoMatchDialog = false },
            title = { Text("Совпадений не найдено") },
            text = { 
                Text(
                    "Мы не смогли найти совпадений с пропавшими людьми в нашей базе данных. " +
                    "Возможно, этот человек еще не был зарегистрирован в системе."
                ) 
            },
            confirmButton = {
                TextButton(onClick = { showNoMatchDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Распознавание людей",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Область для отображения выбранного изображения
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(selectedImageUri)
                                .build()
                        ),
                        contentDescription = "Выбранное изображение",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (selectedImageBitmap != null) {
                    Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Сделанное фото",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Выберите или сделайте фото для распознавания",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                
                // Индикатор загрузки при анализе изображения
                if (isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Кнопки для выбора источника изображения
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сделать фото")
                }
                
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Из галереи")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Результаты распознавания
            AnimatedVisibility(
                visible = recognitionResults.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Результаты распознавания:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            recognitionResults.take(5).forEach { (label, confidence) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Text(
                                        text = "${(confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Список совпадений с пропавшими людьми
            AnimatedVisibility(
                visible = matchedPersons.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Возможные совпадения:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(matchedPersons) { person ->
                            MatchedPersonItem(
                                person = person,
                                onClick = { onPersonClick(person.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchedPersonItem(
    person: Person,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар или фото человека
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (person.photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(person.photoUrl)
                                .build()
                        ),
                        contentDescription = "Фото ${person.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Последнее местонахождение: ${person.lastSeenLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            RecognitionStatusChip(status = person.status)
        }
    }
}

// Функция для анализа изображения и поиска совпадений
private fun analyzeImage(
    bitmap: Bitmap,
    persons: List<Person>,
    onResult: (List<Pair<String, Float>>, List<Person>) -> Unit
) {
    // Настройка детектора лиц с высокой точностью
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f) // Минимальный размер лица (15% от изображения)
        .enableTracking() // Включаем отслеживание лиц
        .build()
    
    val detector = FaceDetection.getClient(options)
    val image = InputImage.fromBitmap(bitmap, 0)
    
    // Обнаружение лиц на изображении
    detector.process(image)
        .addOnSuccessListener { faces ->
            // Если лица обнаружены
            if (faces.isNotEmpty()) {
                // Собираем информацию о лицах
                val faceAttributes = faces.map { face ->
                    // Получаем атрибуты лица
                    val smileProbability = face.smilingProbability ?: 0f
                    val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f
                    val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
                    val trackingId = face.trackingId
                    val boundingBox = face.boundingBox
                    val headEulerAngleY = face.headEulerAngleY // Поворот головы по оси Y
                    val headEulerAngleZ = face.headEulerAngleZ // Поворот головы по оси Z
                    
                    // Создаем вектор признаков лица
                    "face_${trackingId}_smile_${smileProbability}_eyes_${(rightEyeOpenProbability + leftEyeOpenProbability) / 2}_rotation_${headEulerAngleY}_${headEulerAngleZ}" to 0.9f
                }
                
                // Для демонстрации используем также классификацию изображений
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        val labelResults = labels.map { it.text to it.confidence }
                        
                        // Объединяем результаты распознавания лиц и классификации изображений
                        val results = faceAttributes + labelResults
                        
                        // Поиск совпадений с людьми в базе данных
                        // Улучшенный алгоритм сравнения
                        val matches = persons.filter { person ->
                            // Проверяем совпадения по ключевым словам в описании
                            val descriptionMatch = person.description?.let { description ->
                                labelResults.any { (label, confidence) ->
                                    description.contains(label, ignoreCase = true) && confidence > 0.6f
                                }
                            } ?: false
                            
                            // Проверяем совпадения по имени
                            val nameMatch = labelResults.any { (label, confidence) ->
                                person.name.contains(label, ignoreCase = true) && confidence > 0.7f
                            }
                            
                            // Если есть хотя бы одно совпадение, считаем человека потенциальным совпадением
                            descriptionMatch || nameMatch
                        }
                        
                        onResult(results, matches)
                    }
                    .addOnFailureListener {
                        onResult(faceAttributes, emptyList())
                    }
            } else {
                // Если лица не обнаружены, используем только классификацию изображений
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        val results = labels.map { it.text to it.confidence }
                        
                        // Поиск совпадений с людьми в базе данных
                        val matches = persons.filter { person ->
                            results.any { (label, confidence) ->
                                (person.description?.contains(label, ignoreCase = true) == true ||
                                 person.name.contains(label, ignoreCase = true)) &&
                                confidence > 0.6f
                            }
                        }
                        
                        onResult(results, matches)
                    }
                    .addOnFailureListener {
                        onResult(emptyList(), emptyList())
                    }
            }
        }
        .addOnFailureListener {
            onResult(emptyList(), emptyList())
        }
}

@Composable
fun RecognitionStatusChip(status: String) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "пропавший" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "найден" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
} 