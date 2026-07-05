package com.darkoled.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.darkoled.app.ui.home.HomeScreen
import com.darkoled.app.ui.chats.ChatListScreen
import com.darkoled.app.ui.news.NewsFeedScreen
import com.darkoled.app.ui.settings.SettingsScreen

@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabCount = 4

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
                    NavBarItem(0, "Home", selectedTab == 0, { selectedTab = 0 }) {
                        GlassHomeIcon(selectedTab == 0, Modifier.size(24.dp))
                    }
                    NavBarItem(1, "Chats", selectedTab == 1, { selectedTab = 1 }) {
                        GlassChatIcon(selectedTab == 1, Modifier.size(24.dp))
                    }
                    NavBarItem(2, "News", selectedTab == 2, { selectedTab = 2 }) {
                        GlassNewsIcon(selectedTab == 2, Modifier.size(24.dp))
                    }
                    NavBarItem(3, "Profile", selectedTab == 3, { selectedTab = 3 }) {
                        GlassProfileIcon(selectedTab == 3, Modifier.size(24.dp))
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
                slideInHorizontally { w -> if (targetState > initialState) w else -w }
                    .togetherWith(slideOutHorizontally { w -> if (targetState > initialState) -w else w })
            },
            label = "tabContent"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(modifier = Modifier.padding(innerPadding))
                1 -> ChatListScreen(modifier = Modifier.padding(innerPadding))
                2 -> NewsFeedScreen(modifier = Modifier.padding(innerPadding))
                3 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun NavBarItem(
    index: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 0.95f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "iconScale"
    )

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale },
                contentAlignment = Alignment.Center
            ) { icon() }
        },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
    )
}
