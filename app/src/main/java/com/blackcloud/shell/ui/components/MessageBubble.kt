// === app/src/main/java/com/blackcloud/shell/ui/components/MessageBubble.kt ===
package com.blackcloud.shell.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.data.model.KkypMetadata
import com.blackcloud.shell.ui.theme.BorderColor
import com.blackcloud.shell.ui.theme.DarkSurfaceVariant
import com.blackcloud.shell.ui.theme.TextPrimary
import com.blackcloud.shell.ui.theme.TextSecondary

/**
 * Gönderici türü. Kullanıcı veya asistan (Theia).
 */
enum class MessageSender {
    USER, ASSISTANT
}

/**
 * Tek bir sohbet mesajı temsil eden model.
 */
data class Message(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val isComplete: Boolean = true,
    val kkyp: KkypMetadata? = null
)

/**
 * Sohbet akışındaki mesaj balonunu temsil eden görsel bileşendir.
 * Uzun basışta asistan yanıtını seslendiren "Sesli Oku" seçeneğini tetikleyebilir.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == MessageSender.USER
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = if (isUser) androidx.compose.foundation.layout.Arrangement.End else androidx.compose.foundation.layout.Arrangement.Start
        ) {
            if (!isUser) {
                // Asistan Simgesi
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .align(Alignment.Top),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Theia",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column {
                // Mesaj Kutusu
                val bubbleShape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 16.dp
                )
                Box(
                    modifier = Modifier
                        .testTag(if (isUser) "user_msg_bubble" else "assistant_msg_bubble")
                        .clip(bubbleShape)
                        .background(if (isUser) DarkSurfaceVariant else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isUser) BorderColor.copy(alpha = 0.4f) else BorderColor,
                            bubbleShape
                        )
                        .combinedClickable(
                            onLongClick = onLongClick,
                            onClick = {}
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                ),
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            
                            // Asistandan doğrulanmış mesaj geldiyse onay rozeti
                            if (!isUser && message.kkyp?.verified == true) {
                                KkypBadge(verified = true, issues = emptyList())
                            }
                        }

                        // Yazıyor / Tamamlanmıyor Durumu
                        if (!isUser && !message.isComplete) {
                            Spacer(modifier = Modifier.size(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "yazıyor...",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // KKYP Uyarısı (Sorun varsa) asistan mesajının altında turuncu sütun halinde gösterilir
                if (!isUser && message.isComplete && message.kkyp != null && (!message.kkyp.verified || message.kkyp.issues.isNotEmpty())) {
                    KkypBadge(verified = false, issues = message.kkyp.issues)
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // Kullanıcı Simgesi
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TextSecondary.copy(alpha = 0.15f))
                        .align(Alignment.Top),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ben",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
