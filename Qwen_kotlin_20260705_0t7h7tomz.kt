@Composable
fun MessageBubble(message: Message) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(message.text, modifier = Modifier.weight(1f))
        if (message.isEncrypted) {
            Icon(
                Icons.Rounded.Lock,
                contentDescription = "Зашифровано",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}