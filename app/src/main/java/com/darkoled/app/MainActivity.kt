package com.darkoled.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.darkoled.app.theme.InstaSimTheme
import com.darkoled.app.theme.LocalThemeState
import com.darkoled.app.theme.ThemeState
import com.darkoled.app.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState = remember { ThemeState() }
            CompositionLocalProvider(LocalThemeState provides themeState) {
                InstaSimTheme(
                    themeMode = themeState.themeMode,
                    accentColor = themeState.accentColor
                ) {
                    MainScreen()
                }
            }
        }
    }
}
