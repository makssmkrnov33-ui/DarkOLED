@Composable
fun ReaderModeScreen(article: Article) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)  // Ваши отступы
    ) {
        Text(article.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        // Убираем рекламу, сайдбары — только чистый текст
        Text(article.cleanContent, style = MaterialTheme.typography.bodyLarge)
    }
}