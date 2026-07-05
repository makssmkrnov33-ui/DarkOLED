@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),  // Ваша спецификация
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Chat, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("Чаты", style = MaterialTheme.typography.labelMedium) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Newspaper, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("Новости") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("Ещё") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ChatListScreen()
                1 -> NewsFeedScreen()
                2 -> SettingsScreen()
            }
        }
    }
}