// Свайп влево в чате → архивировать
@Composable
fun ChatItemWithSwipe(chat: Chat, onArchive: () -> Unit, onClick: () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.toInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX < -200f) onArchive()  // Порог срабатывания
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceAtMost(0f)
                    }
                )
            }
            .clickable { onClick() }
    ) {
        ChatCardContent(chat)
    }
}

// Double-tap на новости → лайк
@Composable
fun NewsCardWithLike(article: Article, onLike: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    
    Card(
        modifier = Modifier
            .size(340.dp, 240.dp)  // Ваша спецификация
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = 1.2f
                        onLike()
                        // Анимация возврата
                    }
                )
            }
            .scale(scale)
            .animateFloatAsState(targetValue = 1f)
    ) {
        // Содержимое карточки
    }
}

// Pinch-to-zoom для фото
@Composable
fun ZoomableImage(imageUrl: String) {
    var scale by remember { mutableFloatStateOf(1f) }
    
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                }
            }
            .scale(scale)
    )
}