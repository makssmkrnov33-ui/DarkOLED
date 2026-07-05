package com.darkoled.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.darkoled.app.ui.chats.ChatListScreen
import com.darkoled.app.ui.news.NewsFeedScreen
import com.darkoled.app.ui.settings.SettingsScreen

private data class NavTab(val label: String, val icon: ImageVector)

private val tabs = listOf(
    NavTab("Chats", Icons.Rounded.Chat),
    NavTab("News", Icons.Rounded.Newspaper),
    NavTab("Profile", Icons.Rounded.Person)
)

@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                        label = "iconScale"
                    )
                    val iconRotation by animateFloatAsState(
                        targetValue = if (isSelected) 360f else 0f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
                        label = "iconRotate"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale)
                                    .rotate(iconRotation)
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
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
                0 -> ChatListScreen(modifier = Modifier.padding(innerPadding))
                1 -> NewsFeedScreen(modifier = Modifier.padding(innerPadding))
                2 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
