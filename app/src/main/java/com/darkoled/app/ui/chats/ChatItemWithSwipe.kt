package com.darkoled.app.ui.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.darkoled.app.model.Chat
import kotlin.math.roundToInt

@Composable
fun ChatItemWithSwipe(chat: Chat, onClick: () -> Unit = {}) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { offsetX = 0f },
                    onHorizontalDrag = { _, dragAmount -> offsetX += dragAmount }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AccessibleChatItem(chat = chat)
    }
}
