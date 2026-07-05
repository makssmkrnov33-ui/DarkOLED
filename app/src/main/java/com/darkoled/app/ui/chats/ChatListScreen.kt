package com.darkoled.app.ui.chats

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.darkoled.app.engine.MessengerManager
import com.darkoled.app.engine.PermissionHelper
import com.darkoled.app.model.Chat
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(modifier: Modifier = Modifier, onChatClick: (Chat) -> Unit = {}) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val engine = remember { MessengerManager.engine }
    val engineChats by engine.chats.collectAsState()
    var showContactPicker by remember { mutableStateOf(false) }
    val engineContacts by remember { mutableStateOf(engine.loadContacts()) }

    val contactsPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            engine.importContactsAsChats()
            showContactPicker = true
        }
    }

    val chats = engineChats.map { ec ->
        Chat(
            id = ec.id.toString(),
            name = ec.name,
            avatarUrl = if (ec.avatar != "👤") ec.avatar else null,
            lastMessage = engine.getMessages(ec.id).lastOrNull()?.text ?: "",
            lastMessageTime = engine.getMessages(ec.id).lastOrNull()?.timestamp ?: 0L
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).fillMaxSize().padding(16.dp)) {
            Text("Chats", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            if (chats.isEmpty()) {
                Text(
                    "No chats yet. Tap + to import contacts or start a new chat!",
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(chats, key = { it.id }) { chat ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) { ChatItemWithSwipe(chat = chat, onClick = { onChatClick(chat) }) }
                }
            }
        }

        val fabInteraction = remember { MutableInteractionSource() }
        val isFabPressed by fabInteraction.collectIsPressedAsState()
        val fabScale by animateFloatAsState(
            targetValue = if (isFabPressed) 0.85f else 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "fabBounce"
        )
        FloatingActionButton(
            onClick = {
                if (PermissionHelper.isGranted(ctx, Manifest.permission.READ_CONTACTS)) {
                    engine.importContactsAsChats()
                    showContactPicker = true
                } else contactsPermLauncher.launch(Manifest.permission.READ_CONTACTS)
            },
            interactionSource = fabInteraction,
            modifier = Modifier.padding(16.dp).align(Alignment.End).scale(fabScale),
            containerColor = MaterialTheme.colorScheme.primary
        ) { Icon(Icons.Rounded.People, contentDescription = "Import contacts", tint = MaterialTheme.colorScheme.onPrimary) }
    }

    if (showContactPicker) {
        ContactPickerDialog(contacts = engineContacts, onDismiss = { showContactPicker = false })
    }
}
