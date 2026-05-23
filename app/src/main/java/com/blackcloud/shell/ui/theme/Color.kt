// === app/src/main/java/com/blackcloud/shell/ui/theme/Color.kt ===
package com.blackcloud.shell.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * THEIA BLACKCLOUD temasına ait renk paleti.
 * CompositionLocal ve MaterialTheme desteğiyle dinamik temalandırmayı destekler.
 */

data class CustomThemeColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val borderColor: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomThemeColors(
        textPrimary = Color(0xFFE2E8F0),
        textSecondary = Color(0xFF94A3B8),
        borderColor = Color(0xFF1E293B)
    )
}

val DarkBackground: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val DarkSurface: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val DarkSurfaceVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val CyberGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val DarkGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val AlertRed: Color
    @Composable
    get() = MaterialTheme.colorScheme.error

val WarningOrange = Color(0xFFF59E0B) // Uyarı tonu (Amber)

val TextPrimary: Color
    @Composable
    get() = LocalCustomColors.current.textPrimary

val TextSecondary: Color
    @Composable
    get() = LocalCustomColors.current.textSecondary

val BorderColor: Color
    @Composable
    get() = LocalCustomColors.current.borderColor

/**
 * Siber örgü (mesh gradient) arka plan çizici modifier bileşeni.
 * Tasarımdaki derin mavi-mor atmosferi yüksek performanslı yerel bileşenlerle çizer.
 */
fun Modifier.siberMeshBackground(): Modifier = this.drawBehind {
    // Derin siber arka plan rengi
    drawRect(color = Color(0xFF10131A))
    
    // Sol üst soluk siber fırça aurası (#adc6ff fırçası)
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFADC6FF).copy(alpha = 0.14f), Color.Transparent),
            center = Offset(0f, 0f),
            radius = size.width * 1.1f
        )
    )
    
    // Sağ alt mor siber fırça aurası (#7901cd fırçası)
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF7901CD).copy(alpha = 0.08f), Color.Transparent),
            center = Offset(size.width, size.height),
            radius = size.width * 1.1f
        )
    )
}
