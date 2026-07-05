package com.darkoled.app.ui.news

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(isActive) }
    var volume by remember { mutableFloatStateOf(0.7f) }

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = isActive
            this.volume = volume
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            exoPlayer.play()
            isPlaying = true
        } else {
            exoPlayer.pause()
            isPlaying = false
        }
    }

    LaunchedEffect(volume) {
        exoPlayer.volume = volume
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                val pv = PlayerView(ctx)
                pv.useController = false
                pv.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                pv.player = exoPlayer
                pv
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                        showControls = !showControls
                    }
                }
        )

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC1A1A2E)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Slider(
                        value = exoPlayer.currentPosition.toFloat().coerceAtLeast(0f),
                        onValueChange = { exoPlayer.seekTo(it.toLong()) },
                        valueRange = 0f..(exoPlayer.duration.coerceAtLeast(1).toFloat()),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF69B4),
                            activeTrackColor = Color(0xFFFF69B4),
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(4.dp)
                                    .clickable { exoPlayer.seekTo(exoPlayer.currentPosition - 10000) },
                                imageVector = Icons.Outlined.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = Color.White
                            )

                            Canvas(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        if (exoPlayer.isPlaying) {
                                            exoPlayer.pause(); isPlaying = false
                                        } else {
                                            exoPlayer.play(); isPlaying = true
                                        }
                                    }
                            ) {
                                val r = size.minDimension / 2
                                drawCircle(Color.White.copy(alpha = 0.2f), radius = r, center = center)
                                drawCircle(Color.White.copy(alpha = 0.5f), radius = r * 0.85f, center = center, style = Stroke(1.5f))
                                if (isPlaying) {
                                    val pw = size.width * 0.09f
                                    val ph = size.height * 0.35f
                                    drawRect(Color.White, center.copy(x = center.x - pw * 1.2f, y = center.y - ph / 2), size = androidx.compose.ui.geometry.Size(pw, ph))
                                    drawRect(Color.White, center.copy(x = center.x + pw * 0.2f, y = center.y - ph / 2), size = androidx.compose.ui.geometry.Size(pw, ph))
                                } else {
                                    val path = Path().apply {
                                        moveTo(center.x - size.width * 0.12f, center.y - size.height * 0.2f)
                                        lineTo(center.x + size.width * 0.2f, center.y)
                                        lineTo(center.x - size.width * 0.12f, center.y + size.height * 0.2f)
                                        close()
                                    }
                                    drawPath(path, Color.White)
                                }
                            }

                            Icon(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(4.dp)
                                    .clickable { exoPlayer.seekTo(exoPlayer.currentPosition + 10000) },
                                imageVector = Icons.Outlined.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White
                            )
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 100.dp)
                        .width(36.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            val h = size.height
                            val w = size.width
                            drawRoundRect(
                                Color.White.copy(alpha = 0.2f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2, w / 2)
                            )
                            val fillH = h * volume
                            drawRoundRect(
                                Color(0xFFFF69B4),
                                topLeft = androidx.compose.ui.geometry.Offset(0f, h - fillH),
                                size = androidx.compose.ui.geometry.Size(w, fillH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2, w / 2)
                            )
                        }
                    }
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        modifier = Modifier.fillMaxSize(),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                            )
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
