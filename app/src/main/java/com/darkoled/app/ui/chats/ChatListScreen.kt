package com.darkoled.app.ui.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Chat

@Composable
fun ChatListScreen(modifier: Modifier = Modifier) {
    val chats = remember { mockChats() }
    var showContactPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).fillMaxSize().padding(16.dp)) {
            Text(
                "Chats",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats, key = { it.id }) { chat ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        ChatItemWithSwipe(chat = chat)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showContactPicker = true },
            modifier = Modifier.padding(16.dp).align(Alignment.End),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                if (showContactPicker) Icons.Rounded.People else Icons.Rounded.Add,
                contentDescription = "Add contact",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    if (showContactPicker) {
        ContactPickerDialog(onDismiss = { showContactPicker = false })
    }
}

private fun mockChats() = listOf(
    Chat("1", "Alice", lastMessage = "Hey! How are you?", lastMessageTime = System.currentTimeMillis()),
    Chat("2", "Bob", isGroup = true, members = listOf("alice", "bob", "charlie"), lastMessage = "See you tomorrow"),
    Chat("3", "Charlie", lastMessage = "Did you see the news?", lastMessageTime = System.currentTimeMillis() - 3600000)
)
