package com.darkoled.app.ui.chats

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.LocalPhone
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.Chat
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.model.Message

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatDetailScreen(chat: Chat, theme: ChatTheme = ChatTheme.PINK_GRADIENT, onBack: () -> Unit) {
    val messages = remember { mockMessages(chat.id) }
    var text by remember { mutableStateOf("") }
    var showAttach by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape)) {
                            AsyncImage(
                                model = AnimeAvatar.getAvatarUrl(chat.name),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(chat.name, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text("в сети", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* call */ }) {
                        Icon(Icons.Rounded.LocalPhone, contentDescription = "Call", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF69B4)
                )
            )
        },
        bottomBar = {
            BottomInputBar(
                text = text,
                onTextChange = { text = it },
                onSend = { text = "" },
                onCamera = { photoPicker.launch("image/*") },
                onAttach = { showAttach = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF0F5))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val starColor = Color(0xFFFFD700).copy(alpha = 0.3f)
                for (i in 0..30) {
                    val x = (i * 37 + 13) % size.width
                    val y = (i * 53 + 7) % size.height
                    drawCircle(starColor, 2f, Offset(x, y))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = true
            ) {
                items(messages.reversed(), key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it }
                    ) {
                        KawaiiBubble(message = message, isOutgoing = message.senderId == "me")
                    }
                }
            }
        }
    }

    if (showAttach) {
        ModalBottomSheet(
            onDismissRequest = { showAttach = false },
            sheetState = sheetState,
            containerColor = Color(0xFFFFF0F5)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Прикрепить файл", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFF69B4))
                Spacer(Modifier.height(20.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AttachOption("📸", "Фото") { photoPicker.launch("image/*"); showAttach = false }
                    AttachOption("🎥", "Видео") { videoPicker.launch("video/mp4"); showAttach = false }
                    AttachOption("🎵", "Аудио") { audioPicker.launch("audio/mpeg"); showAttach = false }
                    AttachOption("📄", "Файлы") { filePicker.launch("*/*"); showAttach = false }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AttachOption(emoji: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFFFE4E1)),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 28.sp) }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF666666))
    }
}

@Composable
private fun BottomInputBar(
    text: String, onTextChange: (String) -> Unit, onSend: () -> Unit,
    onCamera: () -> Unit, onAttach: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF0F5)).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCamera) {
            Icon(Icons.Rounded.CameraAlt, "Camera", tint = Color(0xFFFF69B4))
        }
        IconButton(onClick = onAttach) {
            Icon(Icons.Rounded.AttachFile, "Attach", tint = Color(0xFFFF69B4))
        }
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier.weight(1f).height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty()) {
                Text("Сообщение...", color = Color(0xFFCCCCCC), fontSize = 15.sp)
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                cursorBrush = SolidColor(Color(0xFFFF69B4)),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF333333))
            )
        }
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier.size(44.dp)
                .shadow(4.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0084FF))
                .clickable { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Send, "Send", tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun KawaiiBubble(message: Message, isOutgoing: Boolean) {
    val bgColor = if (isOutgoing) Color.White else Color(0xFFFFB6C1)
    val bubbleShape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (isOutgoing) 18.dp else 4.dp,
        bottomEnd = if (isOutgoing) 4.dp else 18.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier.width(260.dp)
                .shadow(3.dp, bubbleShape)
                .clip(bubbleShape)
                .background(bgColor)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isOutgoing) Color(0xFF333333) else Color.White,
                fontSize = 15.sp
            )
        }
    }
}

private fun mockMessages(chatId: String) = listOf(
    Message("1", chatId, "them", text = "Привет! Как дела?", timestamp = System.currentTimeMillis()),
    Message("2", chatId, "me", text = "Привет! Всё отлично, спасибо! Как насчёт кофе?", timestamp = System.currentTimeMillis() + 1000),
    Message("3", chatId, "them", text = "Давай! В 3 часа в Starbucks?", timestamp = System.currentTimeMillis() + 2000),
    Message("4", chatId, "me", text = "Идеально! До встречи 🔥", isEncrypted = true, timestamp = System.currentTimeMillis() + 3000),
    Message("5", chatId, "them", text = "Не могу дождаться! ☕️✨", timestamp = System.currentTimeMillis() + 4000)
)
