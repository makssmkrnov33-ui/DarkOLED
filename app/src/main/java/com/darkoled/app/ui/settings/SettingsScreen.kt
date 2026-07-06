package com.darkoled.app.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.darkoled.app.engine.PermissionHelper
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.ChatTheme
import com.darkoled.app.theme.AccentPreset
import com.darkoled.app.theme.LocalThemeState
import kotlinx.coroutines.launch

private val wallColors = listOf(
    "Современный серый" to listOf("#2C3E50", "#3498DB"),
    "Стальной хром"     to listOf("#7F8C8D", "#BDC3C7"),
    "Тёмный металл"     to listOf("#1A1A2E", "#2D2D44"),
    "Неоновый лёд"      to listOf("#00D2FF", "#3A7BD5"),
    "Розовое золото"    to listOf("#FF69B4", "#FFB6C1"),
    "Фиолетовый туман"  to listOf("#8E44AD", "#9B59B6"),
    "Изумруд"           to listOf("#1ABC9C", "#2ECC71"),
    "Закат"             to listOf("#E74C3C", "#F39C12")
)

private val notifModes = listOf(
    "off"    to "Выкл",
    "important" to "Важные",
    "all"    to "Все",
    "crazy"  to "Чокнутый"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onOpenSecurity: () -> Unit = {}) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val themeState = LocalThemeState.current

    val saved = remember { SettingsStorage.load(ctx) }
    var avatarUri by remember { mutableStateOf(saved.avatarUri) }
    var nickname by remember { mutableStateOf(saved.nickname) }
    var notifMode by remember { mutableStateOf(saved.notificationMode) }
    var selectedWall by remember { mutableStateOf(if (saved.themeColors.size >= 2) saved.themeColors[0] + "," + saved.themeColors[1] else "") }
    var importedThemeColors by remember { mutableStateOf<List<Color>?>(null) }
    var showWallDialog by remember { mutableStateOf(false) }

    var profileSaved by remember { mutableStateOf(true) }
    var notifSaved by remember { mutableStateOf(true) }
    var styleSaved by remember { mutableStateOf(true) }

    fun saveAll() {
        val prefs = UserPrefs(
            avatarUri = avatarUri,
            nickname = nickname,
            notificationMode = notifMode,
            themeColors = if (selectedWall.isNotBlank()) selectedWall.split(",") else emptyList()
        )
        SettingsStorage.save(ctx, prefs)
        profileSaved = true
        notifSaved = true
        styleSaved = true
    }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { avatarUri = uri.toString(); profileSaved = false }
    }
    val stylePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) try {
            val reader = java.io.BufferedReader(java.io.InputStreamReader(ctx.contentResolver.openInputStream(uri)))
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
            // Profile header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                    .padding(start = 20.dp, end = 8.dp, top = 20.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    AsyncImage(
                                        model = if (avatarUri.isNotEmpty()) avatarUri else AnimeAvatar.getAvatarUrl(nickname),
                                        contentDescription = null,
                                        modifier = Modifier.size(72.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    var editing by remember { mutableStateOf(false) }
                                    if (editing) {
                                        BasicTextField(
                                            value = nickname,
                                            onValueChange = { nickname = it; profileSaved = false },
                                            singleLine = true,
                                            cursorBrush = SolidColor(Color.White),
                                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                                color = Color.White, fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        TextButton(onClick = { editing = false; profileSaved = false }) {
                                            Text("Готово", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                        }
                                    } else {
                                        Text(nickname, style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold, color = Color.White,
                                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.height(2.dp))
                                        Text("Нажмите на аватар чтобы изменить",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.clickable {
                                                if (PermissionHelper.isGranted(ctx, PermissionHelper.READ_STORAGE))
                                                    avatarPicker.launch("image/*")
                                                else storagePermLauncher.launch(PermissionHelper.READ_STORAGE)
                                            })
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = profileSaved,
                                    onCheckedChange = { if (!profileSaved) saveAll() },
                                    colors = CheckboxDefaults.colors(checkedColor = Color.White, uncheckedColor = Color.White.copy(alpha = 0.5f)),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Сохранить профиль", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                        }
                    }
                    IconButton(onClick = { scope.launch { snackbarHost.showSnackbar("Настройки") } }) {
                        Icon(Icons.Rounded.Settings, "Settings", tint = Color.White,
                            modifier = Modifier.size(28.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // --- Security ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSecurity() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF4081).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) { Text("🛡", fontSize = 14.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Безопасность", fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface)
                            Text("Защита от атак и маскировка трафика",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Text("›", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))

                // --- Notifications ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Notifications, "Notif", tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Уведомления", fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(Modifier.height(12.dp))
                        notifModes.forEach { (key, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    notifMode = key; notifSaved = false
                                }.padding(vertical = 4.dp)) {
                                RadioButton(
                                    selected = notifMode == key,
                                    onClick = { notifMode = key; notifSaved = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(label, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        if (notifMode == "crazy") {
                            Spacer(Modifier.height(8.dp))
                            Text("Интенсивность:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Slider(
                                value = 0.7f,
                                onValueChange = {},
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFF69B4),
                                    activeTrackColor = Color(0xFFFF69B4),
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = notifSaved,
                                onCheckedChange = { if (!notifSaved) saveAll() },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Сохранить уведомления", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // --- Styles ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Стиль оформления", fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            wallColors.forEach { (name, hexes) ->
                                val key = hexes.joinToString(",")
                                val isSel = selectedWall == key
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    hexes.map { Color(android.graphics.Color.parseColor(it)) }
                                                )
                                            )
                                            .then(if (isSel) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier)
                                            .clickable {
                                                selectedWall = key
                                                styleSaved = false
                                                val colors = hexes.map { Color(android.graphics.Color.parseColor(it)) }
                                                themeState.chatTheme = ChatTheme(
                                                    name = name,
                                                    backgroundColors = colors,
                                                    incomingBubbleColor = colors.last().copy(alpha = 0.4f),
                                                    outgoingBubbleColor = Color.White,
                                                    outgoingBorderColor = colors.last(),
                                                    starColor = Color(0xFFFFD700)
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) Text("\u2713", color = Color.White, fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(name, fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { showWallDialog = true }) {
                            Text("Больше стилей и цветов", color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = styleSaved,
                                onCheckedChange = { if (!styleSaved) saveAll() },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Сохранить стиль", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- Accent & Avatar style ---
                AccentStyleSection(
                    importThemeColors = importedThemeColors,
                    onStylePick = { stylePicker.launch("*/*") },
                    onSave = { saveAll() }
                )

                Spacer(Modifier.height(24.dp))

                // Info
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileRow("Шифрование", "AES-256 GCM")
                        Spacer(Modifier.height(12.dp))
                        ProfileRow("Версия", "1.0.0")
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showWallDialog) {
        AlertDialog(
            onDismissRequest = { showWallDialog = false },
            title = { Text("Акцентный цвет") },
            text = {
                Column {
                    Text("Выберите акцент:", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccentPreset.entries.forEach { preset ->
                            val isSel = preset == themeState.accentPreset
                            Box(
                                modifier = Modifier
                                    .size(44.dp).clip(CircleShape)
                                    .background(preset.color)
                                    .clickable {
                                        themeState.accentPreset = preset
                                        themeState.accentColor = preset.color
                                        styleSaved = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color.White))
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Стиль аватара:", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnimeAvatar.styles.forEach { (key, label) ->
                            val isSel = key == AnimeAvatar.defaultStyle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { AnimeAvatar.defaultStyle = key }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(label, fontSize = 10.sp,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showWallDialog = false; saveAll() }) { Text("Готово") } },
            dismissButton = { TextButton(onClick = { showWallDialog = false }) { Text("Отмена") } }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccentStyleSection(
    importThemeColors: List<Color>?,
    onStylePick: () -> Unit,
    onSave: () -> Unit
) {
    val themeState = LocalThemeState.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Тема чата", fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val themes = ChatTheme.themes + if (importThemeColors != null) listOf(themeState.chatTheme) else emptyList()
                themes.forEach { theme ->
                    val isSel = theme == themeState.chatTheme
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp, 36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.verticalGradient(theme.backgroundColors))
                                .then(if (isSel) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)) else Modifier)
                                .clickable { themeState.chatTheme = theme }
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(theme.name, fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onStylePick) {
                Text("Загрузить свой стиль (.json)", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
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
