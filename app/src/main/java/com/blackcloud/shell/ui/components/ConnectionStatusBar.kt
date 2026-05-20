// === app/src/main/java/com/blackcloud/shell/ui/components/ConnectionStatusBar.kt ===
package com.blackcloud.shell.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.ui.theme.AlertRed
import com.blackcloud.shell.ui.theme.CyberGreen
import com.blackcloud.shell.ui.theme.WarningOrange

/**
 * Bağlantı durumlarını modeller.
 */
enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    RECONNECTING
}

/**
 * PROJECT THEIA BLACKCLOUD Bağlantı Durumu Göstergesi.
 * Bağlantı durumuna göre üst veya alt kısımda hafif, siber şık bir durum çubuğu görüntüler.
 * Çevrimdışı durumda iken manuel tetikleme için "Yeniden Bağlan" butonu barındırır.
 */
@Composable
fun ConnectionStatusBar(
    status: ConnectionStatus,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = status != ConnectionStatus.CONNECTED,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        val (bgColor, textColor, label) = when (status) {
            ConnectionStatus.RECONNECTING -> Triple(
                WarningOrange.copy(alpha = 0.15f),
                WarningOrange,
                "Theia ile Bağlantı Kesildi • Otomatik Yeniden Bağlanma Deneniyor..."
            )
            ConnectionStatus.DISCONNECTED -> Triple(
                AlertRed.copy(alpha = 0.15f),
                AlertRed,
                "Bağlantı Çevrimdışı • Yerel API'ye Erişim Yok"
            )
            ConnectionStatus.CONNECTED -> Triple(
                CyberGreen.copy(alpha = 0.15f),
                CyberGreen,
                "THEIA BAĞLANTISI AKTİF"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("connection_status_bar"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Durum Noktası (Dot pulse indicator)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(textColor, CircleShape)
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (status == ConnectionStatus.DISCONNECTED) {
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(
                        onClick = onReconnect,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = textColor
                        ),
                        modifier = Modifier
                            .height(28.dp)
                            .padding(0.dp)
                            .testTag("manual_reconnect_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Yeniden Bağlan",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "YENİDEN BAĞLAN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
