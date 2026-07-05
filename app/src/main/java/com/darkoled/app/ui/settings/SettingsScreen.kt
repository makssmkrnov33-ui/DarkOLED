package com.darkoled.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text("Theme: Dark OLED", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text("E2EE: Enabled", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text("Version: 1.0.0", style = MaterialTheme.typography.labelMedium)
    }
}
