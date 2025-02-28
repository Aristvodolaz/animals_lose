package com.application.lose_animals.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.application.lose_animals.data.model.ChatMessage
import com.application.lose_animals.di.FirebaseDatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val messageText = remember { mutableStateOf("") }
    val database = FirebaseDatabaseHelper.chatRef
    val auth = FirebaseAuth.getInstance()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: "unknown"
    val userName = currentUser?.displayName ?: "Аноним"
    
    var isLoading by remember { mutableStateOf(true) }
    var showChatInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let { 
                    messages.add(it)
                    coroutineScope.launch {
                        delay(100) // Небольшая задержка для анимации
                        scrollState.animateScrollToItem(0)
                    }
                }
                isLoading = false
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
        
        // Имитация загрузки данных, если нет сообщений
        delay(1500)
        isLoading = false
    }
    
    val sendMessage = {
        if (messageText.value.isNotBlank()) {
            val newMessage = ChatMessage(
                senderId = userId,
                senderName = userName,
                message = messageText.value,
                timestamp = System.currentTimeMillis()
            )
            database.push().setValue(newMessage)
            messageText.value = ""
            keyboardController?.hide()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Чат поиска животных",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
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
                actions = {
                    IconButton(onClick = { 
                        // Показываем информацию о чате
                        showChatInfoDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Информация о чате"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText.value,
                        onValueChange = { messageText.value = it },
                        placeholder = { Text("Введите сообщение...") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage() }),
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    FloatingActionButton(
                        onClick = sendMessage,
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Filled.Send, 
                            contentDescription = "Отправить",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn()
            ) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Пока нет сообщений",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Будьте первым, кто начнет обсуждение!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        state = scrollState,
                        reverseLayout = true,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages.reversed()) { message ->
                            ChatBubble(message, isCurrentUser = message.senderId == userId)
                        }
                    }
                }
            }
        }
    }

    // Диалог с информацией о чате
    if (showChatInfoDialog) {
        AlertDialog(
            onDismissRequest = { showChatInfoDialog = false },
            title = { Text("Информация о чате") },
            text = { 
                Column {
                    Text("Чат для связи с родственниками пропавших людей и волонтерами.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Участники: ${chatData.participants.size} человек")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Создан: ${chatData.createdAt}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showChatInfoDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = dateFormat.format(Date(message.timestamp))
    
    val bubbleShape = if (isCurrentUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }
    
    val bubbleColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val timeColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isCurrentUser) "Вы" else message.senderName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isCurrentUser) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) 
            else 
                MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
        )
        
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .animateEnterExit(
                    enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 }
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = time, 
                    style = MaterialTheme.typography.labelSmall,
                    color = timeColor,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun Modifier.animateEnterExit(
    enter: androidx.compose.animation.EnterTransition,
    exit: androidx.compose.animation.ExitTransition = androidx.compose.animation.ExitTransition.None
) = composed {
    val visible = remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible.value = true
    }
    
    AnimatedVisibility(
        visible = visible.value,
        enter = enter,
        exit = exit
    ) {
        this@animateEnterExit
    }
}
