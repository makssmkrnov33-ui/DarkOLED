package com.darkoled.app.ui.chats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.model.Message

@Composable
fun MessageBubble(
    message: Message,
    theme: ChatTheme = ChatTheme.PINK_GRADIENT,
    isOutgoing: Boolean = true
) {
    val rotation by animateFloatAsState(
        targetValue = if (message.isEncrypted) 360f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 150f),
        label = "lockRotation"
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = if (isOutgoing) Arrangement.End
        else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .clip(RoundedCornerShape(16.dp, 16.dp, if (isOutgoing) 4.dp else 16.dp, if (isOutgoing) 16.dp else 4.dp))
                .background(
                    if (isOutgoing) theme.outgoingBubbleColor
                    else theme.incomingBubbleColor
                )
                .then(
                    if (isOutgoing) Modifier.padding(1.dp).background(theme.outgoingBorderColor, RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    else Modifier
                )
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isOutgoing) Color.Black else Color.Black,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                if (message.isEncrypted) {
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = "Encrypted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp).rotate(rotation)
                    )
                }
            }
        }
    }
}
