package com.darkoled.app.ui.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.darkoled.app.model.Chat
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(chat: Chat, theme: ChatTheme = ChatTheme.PINK_GRADIENT, onBack: () -> Unit) {
    val messages = remember { mockMessages(chat.id) }
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.ui.graphics.Color(0xFFFFB6C1))
                        ) {
                            Text("A", color = Color.White,
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(chat.name, fontWeight = FontWeight.SemiBold)
                            Text("Online", style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF4CAF50))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.backgroundColors.first()
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.backgroundColors.first())
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* camera */ }) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera",
                        tint = androidx.compose.ui.graphics.Color(0xFFFF69B4))
                }
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color(0xFFFF69B4),
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFFFFB6C1)
                    ),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { text = "" }) {
                    Icon(Icons.Rounded.Send, contentDescription = "Send",
                        tint = androidx.compose.ui.graphics.Color(0xFFFF69B4))
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = theme.backgroundColors
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                items(messages.reversed(), key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(animationSpec = tween(300)) { it }
                    ) {
                        MessageBubble(
                            message = message,
                            theme = theme,
                            isOutgoing = message.senderId == "me"
                        )
                    }
                }
            }
        }
    }
}

private fun mockMessages(chatId: String) = listOf(
    Message("1", chatId, "them", text = "Hey! How are you?", timestamp = System.currentTimeMillis()),
    Message("2", chatId, "me", text = "Hi! I'm great, thanks! Want to grab coffee?", timestamp = System.currentTimeMillis() + 1000),
    Message("3", chatId, "them", text = "Sure! How about Starbucks at 3?", timestamp = System.currentTimeMillis() + 2000),
    Message("4", chatId, "me", text = "Perfect! See you there 🔥", isEncrypted = true, timestamp = System.currentTimeMillis() + 3000),
    Message("5", chatId, "them", text = "Can't wait! ☕️✨", timestamp = System.currentTimeMillis() + 4000)
)
