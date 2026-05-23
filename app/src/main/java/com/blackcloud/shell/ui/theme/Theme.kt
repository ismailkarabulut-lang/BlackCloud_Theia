// === app/src/main/java/com/blackcloud/shell/ui/theme/Theme.kt ===
package com.blackcloud.shell.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * THEIA BLACKCLOUD Visual Themes enum.
 */
enum class ThemeType(val displayName: String, val description: String) {
    ELEGANT_DARK("Elegant Dark", "Zarif mavi ve titan siyahının asil ahengi"),
    CYBERPUNK_NEON("Cyber Neon", "Göz alıcı mor neonlar ve asit yeşili detaylar"),
    COSMIC_OLED("Cosmic OLED", "Sonsuz derinlikte saf OLED siyahı ve turkuaz siber parıltı"),
    SLATE_GRAY("Slate Gray", "Endüstriyel çelik grisi tonlar ve amber rengi uyarılar")
}

/**
 * PROJECT THEIA BLACKCLOUD ana temanın yapılandırılması.
 * Kullanıcının seçtiği siber detaylara sahip temayı canlandırır.
 */
@Composable
fun TheiaTheme(
    themeType: ThemeType = ThemeType.ELEGANT_DARK,
    content: @Composable () -> Unit
) {
    val (colorScheme, customColors) = when (themeType) {
        ThemeType.ELEGANT_DARK -> {
            darkColorScheme(
                primary = Color(0xFF3B82F6),
                secondary = Color(0xFF1D4ED8),
                tertiary = Color(0xFF94A3B8),
                background = Color(0xFF0A0A0A),
                surface = Color(0xFF141414),
                surfaceVariant = Color(0xFF1A1A1A),
                error = Color(0xFFEF4444)
            ) to CustomThemeColors(
                textPrimary = Color(0xFFE2E8F0),
                textSecondary = Color(0xFF94A3B8),
                borderColor = Color(0xFF1E293B)
            )
        }
        ThemeType.CYBERPUNK_NEON -> {
            darkColorScheme(
                primary = Color(0xFFD946EF),
                secondary = Color(0xFF10B981),
                tertiary = Color(0xFFA1A1AA),
                background = Color(0xFF0D0C10),
                surface = Color(0xFF18151E),
                surfaceVariant = Color(0xFF231D2E),
                error = Color(0xFFEE4444)
            ) to CustomThemeColors(
                textPrimary = Color(0xFFFAFAFA),
                textSecondary = Color(0xFFA1A1AA),
                borderColor = Color(0xFF4A154B)
            )
        }
        ThemeType.COSMIC_OLED -> {
            darkColorScheme(
                primary = Color(0xFF06B6D4),
                secondary = Color(0xFF4F46E5),
                tertiary = Color(0xFF9CA3AF),
                background = Color(0xFF000000),
                surface = Color(0xFF0B0F19),
                surfaceVariant = Color(0xFF111827),
                error = Color(0xFFEF4444)
            ) to CustomThemeColors(
                textPrimary = Color(0xFFFFFFFF),
                textSecondary = Color(0xFF9CA3AF),
                borderColor = Color(0xFF374151)
            )
        }
        ThemeType.SLATE_GRAY -> {
            darkColorScheme(
                primary = Color(0xFFF97316),
                secondary = Color(0xFF64748B),
                tertiary = Color(0xFF94A3B8),
                background = Color(0xFF0F172A),
                surface = Color(0xFF1E293B),
                surfaceVariant = Color(0xFF334155),
                error = Color(0xFFEF4444)
            ) to CustomThemeColors(
                textPrimary = Color(0xFFF1F5F9),
                textSecondary = Color(0xFF94A3B8),
                borderColor = Color(0xFF475569)
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
