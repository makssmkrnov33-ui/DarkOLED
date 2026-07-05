package com.darkoled.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.darkoled.app.theme.DarkOledTheme
import com.darkoled.app.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DarkOledTheme {
                MainScreen()
            }
        }
    }
}
