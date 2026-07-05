package com.darkoled.app.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.darkoled.app.engine.PermissionHelper
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.theme.AccentPreset
import com.darkoled.app.theme.LocalThemeState
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val themeState = LocalThemeState.current
    var avatarUri by remember { mutableStateOf("") }
    var importedThemeColors by remember { mutableStateOf<List<Color>?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) avatarUri = uri.toString()
    }
    val stylePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) try {
            val reader = BufferedReader(InputStreamReader(ctx.contentResolver.openInputStream(uri)))
            val json = reader.readText()
            reader.close()
            val colors = json.split(",").mapNotNull { c ->
                try { Color(android.graphics.Color.parseColor(c.trim())) } catch (_: Exception) { null }
            }
            if (colors.size >= 2) {
                importedThemeColors = colors
                themeState.chatTheme = ChatTheme(
                    name = "Custom",
                    backgroundColors = listOf(colors[0], colors.getOrElse(1) { colors[0] }),
                    incomingBubbleColor = colors.getOrElse(2) { Color(0xFFFFB6C1) },
                    outgoingBubbleColor = colors.getOrElse(3) { Color.White },
                    outgoingBorderColor = colors.getOrElse(4) { Color(0xFFFFB6C1) },
                    starColor = colors.getOrElse(5) { Color(0xFFFFD700) }
                )
            }
        } catch (_: Exception) {}
    }
    val storagePermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) avatarPicker.launch("image/*")
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { pad ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(pad)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 32.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable {
                                if (PermissionHelper.isGranted(ctx, PermissionHelper.READ_STORAGE))
                                    avatarPicker.launch("image/*")
                                else storagePermLauncher.launch(PermissionHelper.READ_STORAGE)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUri.isNotEmpty()) {
                            AsyncImage(
                                model = avatarUri, contentDescription = null,
                                modifier = Modifier.size(72.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = AnimeAvatar.getAvatarUrl("user"), contentDescription = null,
                                modifier = Modifier.size(72.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("User", style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text("InstaSIM v1.0", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Chat Background", fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val themes = ChatTheme.themes + if (importedThemeColors != null) listOf(themeState.chatTheme) else emptyList()
                            themes.forEach { theme ->
                                val isSel = theme == themeState.chatTheme
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp, 48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Brush.verticalGradient(theme.backgroundColors))
                                            .then(if (isSel) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier)
                                            .clickable { themeState.chatTheme = theme }
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(theme.name, style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Avatar Style", fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimeAvatar.styles.forEach { (key, label) ->
                                val isSel = key == AnimeAvatar.defaultStyle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { AnimeAvatar.defaultStyle = key }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(label, style = MaterialTheme.typography.labelSmall,
                                        color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Accent Color", fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AccentPreset.entries.forEach { preset ->
                                val isSel = preset == themeState.accentPreset
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp).clip(CircleShape)
                                            .background(preset.color)
                                            .clickable {
                                                themeState.accentPreset = preset
                                                themeState.accentColor = preset.color
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color.White))
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(preset.label, style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { stylePicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.FileUpload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload Custom Style (.json)")
                }

                Spacer(Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = { avatarPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Avatar from Gallery")
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileRow("Theme", if (importedThemeColors != null) "Custom" else "OLED Dark")
                        Spacer(Modifier.height(12.dp))
                        ProfileRow("Encryption", "AES-256 GCM")
                        Spacer(Modifier.height(12.dp))
                        ProfileRow("Version", "1.0.0")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
