package com.darkoled.app.ui.chats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Message

@Composable
fun MessageBubble(message: Message) {
    val rotation by animateFloatAsState(
        targetValue = if (message.isEncrypted) 360f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 150f),
        label = "lockRotation"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyLarge
        )
        if (message.isEncrypted) {
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Rounded.Lock,
                contentDescription = "Encrypted",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(16.dp).height(16.dp).rotate(rotation)
            )
        }
    }
}
