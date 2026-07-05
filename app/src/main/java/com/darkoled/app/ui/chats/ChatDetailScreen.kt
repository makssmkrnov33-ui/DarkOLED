package com.darkoled.app.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Chat
import com.darkoled.app.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(chat: Chat, onBack: () -> Unit) {
    val messages = remember { mockMessages(chat.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chat.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            items(messages) { message ->
                MessageBubble(message = message)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private fun mockMessages(chatId: String) = listOf(
    Message("1", chatId, "me", text = "Hello!", timestamp = System.currentTimeMillis()),
    Message("2", chatId, "them", text = "Hi! How are you?", timestamp = System.currentTimeMillis() + 1000),
    Message("3", chatId, "me", text = "All good!", isEncrypted = true, timestamp = System.currentTimeMillis() + 2000)
)
