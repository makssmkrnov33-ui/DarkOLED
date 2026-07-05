package com.darkoled.app.engine

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class MessengerEngine(private val context: Context) {

    private val _messages = MutableSharedFlow<MessageEvent>(extraBufferCapacity = 100)
    val messages: SharedFlow<MessageEvent> = _messages.asSharedFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _aiResponses = MutableSharedFlow<Pair<Long, String>>(extraBufferCapacity = 10)
    val aiResponses: SharedFlow<Pair<Long, String>> = _aiResponses.asSharedFlow()

    private val messageStore = ConcurrentHashMap<Long, MutableList<Message>>()
    private val chatStore = ConcurrentHashMap<Long, Chat>()
    private val aiEngine = AIEngine()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        createChat("AI Assistant", isAI = true)
        addDemoContacts()
    }

    private fun addDemoContacts() {
        val russianNames = listOf(
            "Алексей Иванов", "Мария Петрова", "Дмитрий Смирнов",
            "Елена Кузнецова", "Сергей Васильев", "Анна Попова",
            "Андрей Новиков", "Ольга Фёдорова", "Павел Морозов",
            "Наталья Волкова", "Артём Козлов", "Юлия Лебедева"
        )
        russianNames.forEach { name ->
            val id = System.currentTimeMillis() + name.hashCode()
            val phone = "+7" + (9000000000L..9999999999L).random().toString().take(10)
            val chat = Chat(id, name, "👤", phone, false, false)
            chatStore[id] = chat
            messageStore[id] = mutableListOf()
            val msg = Message(
                id = System.currentTimeMillis() + name.hashCode(),
                chatId = id, text = "Привет! Как дела?",
                type = MessageType.INCOMING, senderName = name,
                timestamp = System.currentTimeMillis() - (1000..86400000).random()
            )
            messageStore[id]?.add(msg)
        }
        updateChatsFlow()
    }

    fun createChat(name: String, phone: String = "", isAI: Boolean = false, avatar: String = ""): Long {
        val id = System.currentTimeMillis()
        val chat = Chat(id, name, avatar.ifEmpty { "👤" }, phone, isAI, isAI)
        chatStore[id] = chat
        messageStore[id] = mutableListOf()
        updateChatsFlow()
        return id
    }

    fun deleteChat(chatId: Long) {
        chatStore.remove(chatId)
        messageStore.remove(chatId)
        updateChatsFlow()
    }

    fun getChat(chatId: Long): Chat? = chatStore[chatId]
    fun getMessages(chatId: Long): List<Message> = messageStore[chatId]?.toList() ?: emptyList()

    fun sendMessage(chatId: Long, text: String, attachments: List<Attachment> = emptyList()) {
        val msg = Message(
            id = System.currentTimeMillis(),
            chatId = chatId,
            text = text,
            type = MessageType.OUTGOING,
            attachments = attachments,
            timestamp = System.currentTimeMillis()
        )
        addMessage(msg)
        val chat = chatStore[chatId]
        if (chat?.aiEnabled == true && chat.isAI) generateAIResponse(chatId, text)
    }

    fun receiveMessage(chatId: Long, text: String, from: String) {
        val msg = Message(
            id = System.currentTimeMillis(),
            chatId = chatId, text = text,
            type = MessageType.INCOMING, senderName = from,
            timestamp = System.currentTimeMillis()
        )
        addMessage(msg)
    }

    private fun addMessage(msg: Message) {
        messageStore[msg.chatId]?.add(msg)
        _messages.tryEmit(MessageEvent.NewMessage(msg))
        updateChatsFlow()
    }

    private fun generateAIResponse(chatId: Long, userText: String) {
        scope.launch {
            delay(800)
            val response = aiEngine.respond(userText)
            val aiMsg = Message(
                id = System.currentTimeMillis(), chatId = chatId, text = response,
                type = MessageType.AI, isFromAI = true, timestamp = System.currentTimeMillis()
            )
            messageStore[chatId]?.add(aiMsg)
            _aiResponses.emit(Pair(chatId, response))
            _messages.emit(MessageEvent.NewMessage(aiMsg))
            updateChatsFlow()
        }
    }

    fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun loadContacts(): List<Contact> {
        if (!checkPermission(android.Manifest.permission.READ_CONTACTS)) return emptyList()
        val contacts = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.use {
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIdx) ?: continue
                val phone = it.getString(phoneIdx)?.replace("\\s".toRegex(), "") ?: ""
                contacts.add(Contact(name, phone))
            }
        }
        return contacts.distinctBy { it.phone }
    }

    fun importContactsAsChats() {
        loadContacts().forEach { contact ->
            if (chatStore.values.none { it.phone == contact.phone })
                createChat(contact.name, contact.phone)
        }
    }

    private fun updateChatsFlow() {
        _chats.value = chatStore.values.sortedByDescending { chat ->
            messageStore[chat.id]?.lastOrNull()?.timestamp ?: 0
        }
    }
}

data class Chat(
    val id: Long, val name: String, val avatar: String = "👤",
    val phone: String = "", val isAI: Boolean = false, val aiEnabled: Boolean = false
)

data class Message(
    val id: Long, val chatId: Long, val text: String,
    val type: MessageType, val senderName: String = "",
    val attachments: List<Attachment> = emptyList(),
    val isFromAI: Boolean = false, val timestamp: Long
)

enum class MessageType { INCOMING, OUTGOING, AI }
sealed class Attachment {
    data class Video(val url: String, val title: String = "") : Attachment()
    data class Image(val uri: String) : Attachment()
    data class Audio(val uri: String, val duration: Int = 0) : Attachment()
    data class File(val uri: String, val name: String, val size: Long) : Attachment()
}

data class Contact(val name: String, val phone: String)
sealed class MessageEvent {
    data class NewMessage(val message: Message) : MessageEvent()
    data class MessageUpdated(val messageId: Long) : MessageEvent()
    data class ChatUpdated(val chatId: Long) : MessageEvent()
}

class AIEngine {
    private val responses = mapOf(
        "привет" to "Привет! 👋 Я AI-ассистент. Готов помочь!",
        "видео" to "Могу отправлять видео с YouTube и по прямым ссылкам!",
        "камера" to "Камера готова — нажми 🎥 для съёмки!",
        "микрофон" to "Микрофон активен. Голосовые сообщения работают 🎤",
        "контакт" to "Контакты загружены через loadContacts()",
        "помощь" to "Команды: чаты, сообщения, AI, контакты, видео",
        "пока" to "До встречи! 🚀"
    )
    fun respond(input: String): String {
        val lower = input.lowercase()
        return responses.entries.find { lower.contains(it.key) }?.value
            ?: "Расскажи подробнее! Спроси про AI, видео или контакты ✨"
    }
}
