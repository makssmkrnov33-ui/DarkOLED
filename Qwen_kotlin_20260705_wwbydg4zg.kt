import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Палитра
val LightColors = lightColorScheme(
    primary = Color(0xFF0084FF),
    background = Color(0xFFF5F5F5),
    surface = Color.White
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF0084FF),
    background = Color.Black,        // OLED черный
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White
)

// Шрифты (Inter/Roboto)
val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = Inter),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = Inter),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = Inter)
)

@Composable
fun MessengerTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    accentColor: Color = Color(0xFF0084FF),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColors.copy(primary = accentColor)
        ThemeMode.DARK -> DarkColors.copy(primary = accentColor)
        ThemeMode.AUTO -> if (isSystemInDarkTheme()) DarkColors.copy(primary = accentColor) 
                          else LightColors.copy(primary = accentColor)
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

enum class ThemeMode { LIGHT, DARK, AUTO }