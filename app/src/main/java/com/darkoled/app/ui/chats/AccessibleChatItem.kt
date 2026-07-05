package com.darkoled.app.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.Chat

@Composable
fun AccessibleChatItem(chat: Chat) {
    val avatarUrl = chat.avatarUrl ?: AnimeAvatar.getAvatarUrl(chat.name)
    Row(
        modifier = Modifier
            .padding(16.dp)
            .semantics {
                contentDescription = "Chat with ${chat.name}. Last message: ${chat.lastMessage}"
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "${chat.name} avatar",
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(chat.name, style = MaterialTheme.typography.bodyLarge)
    }
}

