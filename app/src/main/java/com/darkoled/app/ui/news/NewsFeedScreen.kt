package com.darkoled.app.ui.news

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.darkoled.app.model.AnimeAvatar
import com.darkoled.app.model.ShortVideo
import com.darkoled.app.theme.LocalThemeState
import com.darkoled.app.theme.ThemeMode

private val shortsData = listOf(
    ShortVideo("s1", "Первый клип на VK Видео", "https://vkvideo.ru/clip-108140506_456245247",
        "VK Клипы", 1543, 89, 432),
    ShortVideo("s2", "Тестовое видео Google", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "Google Sample", 8921, 456, 2341),
    ShortVideo("s3", "Веселье на природе", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "Travel Vlog", 6543, 321, 1876),
    ShortVideo("s4", "Красивый закат", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "Nature", 3210, 189, 765),
    ShortVideo("s5", "Музыкальный клип", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "Music", 15678, 1023, 5678),
    ShortVideo("s6", "Экстремальный спорт", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        "Sport", 7654, 567, 2345)
)

@Composable
fun NewsFeedScreen(modifier: Modifier = Modifier) {
    val themeState = LocalThemeState.current
    var likedSet by remember { mutableStateOf(setOf<String>()) }
    var starredSet by remember { mutableStateOf(setOf<String>()) }

    val pagerState = rememberPagerState(pageCount = { shortsData.size })
    val ctxForShare = LocalContext.current

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ShortsPage(
                video = shortsData[page],
                isActive = page == pagerState.currentPage,
                isLiked = shortsData[page].id in likedSet,
                isStarred = shortsData[page].id in starredSet,
                onLike = {
                    val s = likedSet.toMutableSet()
                    if (shortsData[page].id in s) s.remove(shortsData[page].id) else s.add(shortsData[page].id)
                    likedSet = s
                },
                onStar = {
                    val s = starredSet.toMutableSet()
                    if (shortsData[page].id in s) s.remove(shortsData[page].id) else s.add(shortsData[page].id)
                    starredSet = s
                },
                onShare = { video ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, "${video.title} - ${video.videoUrl}")
                    }
                    ctxForShare.startActivity(android.content.Intent.createChooser(intent, "Share"))
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xCC0F0F1E), Color.Transparent),
                        startY = 0f, endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        if (themeState.themeMode == ThemeMode.DARK) "\uD83C\uDF19 Shorts" else "\u2600\uFE0F Shorts",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable {
                            themeState.themeMode = when (themeState.themeMode) {
                                ThemeMode.DARK -> ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.DARK
                                ThemeMode.AUTO -> ThemeMode.DARK
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (themeState.themeMode == ThemeMode.DARK) "\uD83C\uDF19" else "\u2600\uFE0F", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun ShortsPage(
    video: ShortVideo,
    isActive: Boolean,
    isLiked: Boolean,
    isStarred: Boolean,
    onLike: () -> Unit,
    onStar: () -> Unit,
    onShare: (ShortVideo) -> Unit
) {
    val ctx = LocalContext.current
    var likes by remember(video.id, isLiked) { mutableIntStateOf(video.likes + if (isLiked) 1 else 0) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (video.videoUrl.contains("vkvideo.ru") || video.videoUrl.contains("vk.com")) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        settings.javaScriptEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        loadUrl(video.videoUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            VideoPlayer(
                url = video.videoUrl,
                modifier = Modifier.fillMaxSize(),
                isActive = isActive
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC0A0A14)),
                        startY = 0f, endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 72.dp, bottom = 60.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = AnimeAvatar.getAvatarUrl(video.author),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(video.author, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFF69B4).copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Подписаться", color = Color(0xFFFF69B4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(video.title, color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text("${video.likes} просмотров", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShortsBtn(if (isStarred) "\u2B50" else "\u2606", "", isStarred, Color(0xFFFFD700), onStar)
            ShortsBtn(if (isLiked) "\uD83D\uDC97" else "\uD83E\uDD0D", formatCount(likes), isLiked, Color(0xFFFF4081), onLike)
            ShortsBtn("\uD83D\uDCAC", formatCount(video.comments), false, Color.White, {})
            ShortsBtn("\uD83D\uDCE4", formatCount(video.shares), false, Color.White, { onShare(video) })
        }
    }
}

@Composable
private fun ShortsBtn(emoji: String, label: String, active: Boolean, activeColor: Color, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.8f else 1f, spring(0.4f, 600f), label = "s")
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(44.dp).clip(CircleShape).clickable(interactionSource = src, indication = null, onClick = onClick).scale(scale).padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape)
            .background(if (active) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 22.sp)
        }
        if (label.isNotEmpty()) Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

private fun formatCount(c: Int) = when {
    c >= 1_000_000 -> "${c / 1_000_000}M"
    c >= 1_000 -> "${c / 1_000}K"
    else -> "$c"
}
