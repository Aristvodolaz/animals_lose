package com.application.lose_animals.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.application.lose_animals.data.model.ChatMessage
import com.application.lose_animals.di.FirebaseDatabaseHelper
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen() {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val messageText = remember { mutableStateOf("") }
    val database = FirebaseDatabaseHelper.chatRef
    val auth = FirebaseAuth.getInstance()

    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: "unknown"
    val userName = currentUser?.displayName ?: "Аноним"

    LaunchedEffect(Unit) {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let { messages.add(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Чат поиска людей",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message, isCurrentUser = message.senderId == userId)
            }
        }

        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            placeholder = { Text("Введите сообщение...") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            ),
            trailingIcon = {
                IconButton(onClick = {
                    if (messageText.value.isNotBlank()) {
                        val newMessage = ChatMessage(
                            senderId = userId,
                            senderName = userName,
                            message = messageText.value,
                            timestamp = System.currentTimeMillis()
                        )
                        database.push().setValue(newMessage)
                        messageText.value = ""
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Отправить", tint = MaterialTheme.colorScheme.primary)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = dateFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCurrentUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 2.dp,
            color = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCurrentUser) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}
