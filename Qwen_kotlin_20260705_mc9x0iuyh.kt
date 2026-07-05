@Composable
fun AccessibleChatItem(chat: Chat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Чат с ${chat.name}, последнее сообщение: ${chat.lastMessage}"
                role = Role.Button
            }
            .clickable { /* ... */ }
    ) {
        Image(
            painter = rememberAsyncImagePainter(chat.avatarUrl),
            contentDescription = "Аватар ${chat.name}",
            modifier = Modifier.size(56.dp).clip(CircleShape)  // Ваш размер
        )
        Spacer(Modifier.width(12.dp))  // Ваш отступ
        Column {
            Text(chat.name, style = MaterialTheme.typography.titleMedium)
            Text(chat.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Поддержка Dynamic Type — используйте sp вместо dp для текста
// Material3 автоматически учитывает системные настройки шрифта