package com.darkoled.app.ui.chats

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkoled.app.engine.MessengerManager
import com.darkoled.app.engine.PermissionHelper
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.Chat
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.model.Message
import com.darkoled.app.model.MessageType
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatDetailScreen(chat: Chat, theme: ChatTheme = ChatTheme.PINK_GRADIENT, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val engine = remember { MessengerManager.engine }
    var text by remember { mutableStateOf("") }
    var showAttach by remember { mutableStateOf(false) }
    var showMicPermissionDialog by remember { mutableStateOf(false) }
    var showCamPermissionDialog by remember { mutableStateOf(false) }
    var showGalleryPermissionDialog by remember { mutableStateOf(false) }
    var chatAvatarUri by remember { mutableStateOf(chat.avatarUrl) }
    val sheetState = rememberModalBottomSheetState()
    var voiceMessages by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }

    val engineChatId = chat.id.hashCode().toLong().let { if (it < 0) -it else it }
    val messages = remember { mutableStateOf(engine.getMessages(engineChatId)) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) scope.launch { snackbarHost.showSnackbar("Photo taken!") }
    }
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            chatAvatarUri = uri.toString()
            scope.launch { snackbarHost.showSnackbar("Avatar updated!") }
        }
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            engine.sendMessage(engineChatId, "📷 Photo", listOf(com.darkoled.app.engine.Attachment.Image(uri.toString())))
            scope.launch { snackbarHost.showSnackbar("Image attached!") }
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            engine.sendMessage(engineChatId, "🎬 Video", listOf(com.darkoled.app.engine.Attachment.Video(uri.toString())))
            scope.launch { snackbarHost.showSnackbar("Video attached!") }
        }
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            engine.sendMessage(engineChatId, "🎵 Audio", listOf(com.darkoled.app.engine.Attachment.Audio(uri.toString())))
            scope.launch { snackbarHost.showSnackbar("Audio attached!") }
        }
    }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            engine.sendMessage(engineChatId, "📄 File", listOf(com.darkoled.app.engine.Attachment.File(uri.toString(), uri.lastPathSegment ?: "file", 0)))
            scope.launch { snackbarHost.showSnackbar("File attached!") }
        }
    }

    val camPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val file = File(ctx.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            photoUri.value = Uri.fromFile(file)
            cameraLauncher.launch(photoUri.value!!)
        } else scope.launch { snackbarHost.showSnackbar("Camera permission denied") }
    }
    val micPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch {
                isRecording = true
                val recorder = com.darkoled.app.ui.voice.VoiceRecorder()
                voiceMessages = recorder.record(5000)
                isRecording = false
                if (voiceMessages.isNotEmpty()) {
                    engine.sendMessage(engineChatId, "🎤 Voice message (${voiceMessages.size} samples)")
                    snackbarHost.showSnackbar("Voice recorded!")
                }
            }
        } else scope.launch { snackbarHost.showSnackbar("Microphone permission denied") }
    }
    val contactPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            engine.importContactsAsChats()
            scope.launch { snackbarHost.showSnackbar("Contacts imported!") }
        } else scope.launch { snackbarHost.showSnackbar("Contacts permission denied") }
    }
    val storagePermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) photoPicker.launch("image/*")
        else scope.launch { snackbarHost.showSnackbar("Storage permission denied") }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).clickable {
                            if (PermissionHelper.isGranted(ctx, PermissionHelper.READ_STORAGE))
                                photoPicker.launch("image/*")
                            else
                                storagePermLauncher.launch(PermissionHelper.READ_STORAGE)
                        }) {
                            AsyncImage(
                                model = chatAvatarUri ?: AnimeAvatar.getAvatarUrl(chat.name),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(chat.name, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(
                                if (isRecording) "🎤 Recording..." else "online",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (PermissionHelper.isGranted(ctx, Manifest.permission.READ_CONTACTS))
                            engine.importContactsAsChats()
                        else
                            contactPermLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }) {
                        Icon(Icons.Rounded.LocalPhone, contentDescription = "Contacts", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF69B4))
            )
        },
        bottomBar = {
            Box(Modifier.imePadding()) {
                BottomInputBar(
                text = text, onTextChange = { text = it },
                onSend = {
                    if (text.isNotBlank()) {
                        engine.sendMessage(engineChatId, text)
                        messages.value = engine.getMessages(engineChatId)
                        text = ""
                    }
                },
                onCamera = {
                    if (PermissionHelper.isGranted(ctx, Manifest.permission.CAMERA)) {
                        val file = File(ctx.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
                        photoUri.value = Uri.fromFile(file)
                        cameraLauncher.launch(photoUri.value!!)
                    } else camPermLauncher.launch(Manifest.permission.CAMERA)
                },
                onMic = {
                    if (PermissionHelper.isGranted(ctx, Manifest.permission.RECORD_AUDIO)) {
                        scope.launch {
                            isRecording = true
                            val recorder = com.darkoled.app.ui.voice.VoiceRecorder()
                            voiceMessages = recorder.record(5000)
                            isRecording = false
                            if (voiceMessages.isNotEmpty()) {
                                engine.sendMessage(engineChatId, "🎤 Voice message")
                                messages.value = engine.getMessages(engineChatId)
                                snackbarHost.showSnackbar("Voice recorded!")
                            }
                        }
                    } else micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onAttach = { showAttach = true }
            )
        }
    }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFFFF0F5))
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
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = true
            ) {
                items(messages.value.reversed(), key = { it.id }) { msg ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it }
                    ) {
                        KawaiiBubble(
                            text = msg.text,
                            isOutgoing = msg.type == com.darkoled.app.engine.MessageType.OUTGOING,
                            isAI = msg.type == com.darkoled.app.engine.MessageType.AI
                        )
                    }
                }
                if (messages.value.isEmpty()) {
                    item {
                        Text(
                            "No messages yet. Start chatting! 💬",
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            textAlign = TextAlign.Center,
                            color = Color(0xFFCCCCCC),
                            fontSize = 16.sp
                        )
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
                Text("Attach file", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFF69B4))
                Spacer(Modifier.height(20.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AttachOption("📸", "Photo") { imagePicker.launch("image/*"); showAttach = false }
                    AttachOption("🎥", "Video") { videoPicker.launch("video/mp4"); showAttach = false }
                    AttachOption("🎵", "Audio") { audioPicker.launch("audio/mpeg"); showAttach = false }
                    AttachOption("📄", "Files") { filePicker.launch("*/*"); showAttach = false }
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
    onCamera: () -> Unit, onMic: () -> Unit, onAttach: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF0F5)).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCamera) { Icon(Icons.Rounded.CameraAlt, "Camera", tint = Color(0xFFFF69B4)) }
        IconButton(onClick = onMic) { Icon(Icons.Rounded.Mic, "Mic", tint = Color(0xFFFF69B4)) }
        IconButton(onClick = onAttach) { Icon(Icons.Rounded.AttachFile, "Attach", tint = Color(0xFFFF69B4)) }
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier.weight(1f).heightIn(min = 44.dp, max = 150.dp)
                .animateContentSize()
                .clip(RoundedCornerShape(22.dp)).background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty()) Text("Message...", color = Color(0xFFCCCCCC), fontSize = 15.sp)
            BasicTextField(
                value = text, onValueChange = onTextChange,
                singleLine = false, cursorBrush = SolidColor(Color(0xFFFF69B4)),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF333333))
            )
        }
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier.size(44.dp)
                .shadow(4.dp, RoundedCornerShape(22.dp)).clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0084FF)).clickable { onSend() },
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Rounded.Send, "Send", tint = Color.White, modifier = Modifier.size(22.dp)) }
    }
}

@Composable
private fun KawaiiBubble(text: String, isOutgoing: Boolean, isAI: Boolean = false) {
    val bgColor = when {
        isAI -> Color(0xFFE8D5F5)
        isOutgoing -> Color.White
        else -> Color(0xFFFFB6C1)
    }
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
            modifier = Modifier.width(260.dp).shadow(3.dp, bubbleShape).clip(bubbleShape).background(bgColor).padding(12.dp)
        ) {
            Row {
                if (isAI) Text("🤖 ", fontSize = 16.sp)
                Text(text = text, color = if (isOutgoing) Color(0xFF333333) else Color.White, fontSize = 15.sp)
            }
        }
    }
}
