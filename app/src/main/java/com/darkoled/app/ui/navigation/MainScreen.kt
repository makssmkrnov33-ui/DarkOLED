package com.darkoled.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.darkoled.app.model.Chat
import com.darkoled.app.ui.home.HomeScreen
import com.darkoled.app.ui.chats.ChatDetailScreen
import com.darkoled.app.ui.chats.ChatListScreen
import com.darkoled.app.ui.news.NewsFeedScreen
import com.darkoled.app.ui.security.SecurityScreen
import com.darkoled.app.ui.settings.SettingsScreen
import com.darkoled.app.theme.LocalThemeState

@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedChat by rememberSaveable { mutableStateOf<Chat?>(null) }
    var showSecurityScreen by remember { mutableStateOf(false) }
    val tabCount = 4
    val themeState = LocalThemeState.current

    if (selectedChat != null) {
        ChatDetailScreen(
            chat = selectedChat!!,
            theme = themeState.chatTheme,
            onBack = { selectedChat = null }
        )
        return
    }

    if (showSecurityScreen) {
        SecurityScreen(onBack = { showSecurityScreen = false })
        return
    }

    Scaffold(
        bottomBar = {
            Box {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .graphicsLayer {
                            shadowElevation = 20f
                            alpha = 0.98f
                        },
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    val tabLabels = listOf("Voice AI", "Chats", "Shorts", "Profile")

                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val navScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.85f else 1f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                            label = "navBounce"
                        )
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            interactionSource = interactionSource,
                            modifier = Modifier.scale(navScale),
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.15f else 0.95f,
                                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                                    label = "iconScale"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (index) {
                                        0 -> GlassHomeIcon(isSelected, Modifier.size(24.dp))
                                        1 -> GlassChatIcon(isSelected, Modifier.size(24.dp))
                                        2 -> GlassNewsIcon(isSelected, Modifier.size(24.dp))
                                        3 -> GlassProfileIcon(isSelected, Modifier.size(24.dp))
                                    }
                                }
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxWidth().height(3.dp)) {
                    val tabWidth = size.width / tabCount
                    val lineX = selectedTab * tabWidth + tabWidth / 2
                    val lineLen = tabWidth * 0.5f
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0084FF), Color(0xFF7C4DFF), Color(0xFFFF4081))
                        ),
                        start = Offset(lineX - lineLen, size.height / 2),
                        end = Offset(lineX + lineLen, size.height / 2),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                slideInHorizontally { width -> if (targetState > initialState) width else -width }
                    .togetherWith(slideOutHorizontally { width -> if (targetState > initialState) -width else width })
            },
            label = "tabContent"
        ) { tab ->
            when (tab) {
                    0 -> HomeScreen(modifier = Modifier.padding(innerPadding))
                 1 -> ChatListScreen(modifier = Modifier.padding(innerPadding), onChatClick = { selectedChat = it })
                2 -> NewsFeedScreen(modifier = Modifier.padding(innerPadding))
                 3 -> SettingsScreen(modifier = Modifier.padding(innerPadding), onOpenSecurity = { showSecurityScreen = true })
            }
        }
    }
}
