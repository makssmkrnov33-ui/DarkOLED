package com.darkoled.app.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Chat

@Composable
fun ChatListScreen(modifier: Modifier = Modifier) {
    val chats = remember { mockChats() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Chats")
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(chats) { chat ->
                ChatItemWithSwipe(chat = chat)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private fun mockChats() = listOf(
    Chat("1", "Alice", lastMessage = "Hey! How are you?", lastMessageTime = System.currentTimeMillis()),
    Chat("2", "Bob", isGroup = true, members = listOf("alice", "bob", "charlie"), lastMessage = "See you tomorrow"),
    Chat("3", "Charlie", lastMessage = "Did you see the news?", lastMessageTime = System.currentTimeMillis() - 3600000)
)
