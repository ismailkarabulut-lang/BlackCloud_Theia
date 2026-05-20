// === app/src/main/java/com/blackcloud/shell/ui/components/VoiceFab.kt ===
package com.blackcloud.shell.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Mikrofon Bas-Konuş (Push-to-Talk) Butonu.
 * Kullanıcı parmağını basılı tuttuğunda dinlemeye başlar, bıraktığında dinlemeyi durdurup veriyi iletir.
 * Dinleme esnasında dairesel hareketli/animasyonlu ses dalgası yayar.
 */
@Composable
fun VoiceFab(
    isListening: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animasyonlu Dalga Göstergesi (Infinite wave effect)
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "Pulse1"
    )
    
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "Alpha1"
    )

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isListening) {
            // Animasyon dairesi
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale1)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha1),
                        shape = CircleShape
                    )
            )
        }

        // Ana FAB Butonu
        FloatingActionButton(
            onClick = {}, // Tıklanmayı handle etmeyip doğrudan jest algılayıcıyı kullanacağız
            shape = CircleShape,
            containerColor = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isListening) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(56.dp)
                .testTag("voice_input_fab")
                .pointerInput(isListening) {
                    detectTapGestures(
                        onPress = {
                            try {
                                onPress()
                                awaitRelease()
                            } finally {
                                onRelease()
                            }
                        }
                    )
                }
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (isListening) "Dinleniyor..." else "Konuşmak için basılı tutun",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
