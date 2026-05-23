// === app/src/main/java/com/blackcloud/shell/ui/screens/ChatWorkspaceScreen.kt ===
package com.blackcloud.shell.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.data.model.Project
import com.blackcloud.shell.ui.components.ConnectionStatus
import com.blackcloud.shell.ui.components.ConnectionStatusBar
import com.blackcloud.shell.ui.components.Message
import com.blackcloud.shell.ui.components.MessageBubble
import com.blackcloud.shell.ui.components.VoiceFab
import com.blackcloud.shell.ui.theme.BorderColor
import com.blackcloud.shell.ui.theme.DarkSurfaceVariant
import com.blackcloud.shell.ui.theme.TextPrimary
import com.blackcloud.shell.ui.theme.TextSecondary
import com.blackcloud.shell.ui.theme.siberMeshBackground

/**
 * PROJECT THEIA BLACKCLOUD Sohbet Çalışma Odası (Workspace).
 * Mesajların dinamik olarak SSE akışıyla büyüdüğü, sesli girdinin (STT) tetiklenebildiği
 * ve asistan mesajlarının basılı tutularak tts üzerinden okunabildiği alandır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatWorkspaceScreen(
    project: Project?,
    projectsList: List<Project>,
    onSelectProject: (Project?) -> Unit,
    messages: List<Message>,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    connectionStatus: ConnectionStatus,
    onReconnect: () -> Unit,
    isListening: Boolean,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onLongClickMessage: (Message) -> Unit,
    onBack: () -> Unit,
    sessions: List<com.blackcloud.shell.data.database.ChatSessionEntity> = emptyList(),
    onLoadSession: (String) -> Unit = {},
    onDeleteSession: (String) -> Unit = {},
    onStartNewChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Yeni mesaj veya chunk eklendiğinde listenin otomatik olarak en alta kayması
    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .siberMeshBackground()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Üst Ara Çubuk (App Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri Dön",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            var dropdownExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { dropdownExpanded = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("workspace_selector_btn")
            ) {
                Column {
                    Text(
                        text = "AKTİF WORKSPACE ▾",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = project?.name ?: "Genel Sohbet Kabuğu",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(DarkSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Genel Sohbet Kabuğu",
                                color = if (project == null) MaterialTheme.colorScheme.primary else TextPrimary,
                                fontWeight = if (project == null) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSelectProject(null)
                            dropdownExpanded = false
                        }
                    )
                    projectsList.forEach { proj ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    proj.name,
                                    color = if (project?.id == proj.id) MaterialTheme.colorScheme.primary else TextPrimary,
                                    fontWeight = if (project?.id == proj.id) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectProject(proj)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Yeni Sohbet ve Geçmiş İşlemleri
            IconButton(
                onClick = onStartNewChat,
                modifier = Modifier.testTag("chat_new_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni Sohbet Başlat",
                    tint = TextPrimary
                )
            }

            IconButton(
                onClick = { showHistoryDialog = true },
                modifier = Modifier.testTag("chat_history_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Sohbet Geçmişi",
                    tint = TextPrimary
                )
            }

            // Durum Göstergesi (Light pulse logic)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(
                        if (connectionStatus == ConnectionStatus.CONNECTED) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                    .padding(end = 12.dp)
            )
        }

        // Bağlantı Koptu/Yeniden Bağlanıyor Bandı (Page 3)
        ConnectionStatusBar(
            status = connectionStatus,
            onReconnect = onReconnect
        )

        // Mesajlaşma Gövdesi
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Theia ile sohbete başlayın...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            onLongClick = { onLongClickMessage(message) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(84.dp)) // VoiceFab için boşluk
                    }
                }
            }

            // Bas-Konuş Mikrofon FAB'ı (Alt Sağ Köşe) (Page 3)
            VoiceFab(
                isListening = isListening,
                onToggle = { if (isListening) onStopVoice() else onStartVoice() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }

        // Alt Giriş Paneli (Input Toolbar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = {
                    Text(
                        text = if (isListening) "Dinleniyor..." else "Mesajınızı yazın...",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                maxLines = 3,
                singleLine = false
            )

            // Gönder Butonu
            IconButton(
                onClick = onSendMessage,
                enabled = inputText.trim().isNotEmpty() && connectionStatus == ConnectionStatus.CONNECTED,
                modifier = Modifier.testTag("chat_send_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Gönder",
                    tint = if (inputText.trim().isNotEmpty() && connectionStatus == ConnectionStatus.CONNECTED)
                        MaterialTheme.colorScheme.primary else TextSecondary
                )
            }
        }

        // --- YEREL BELLEK GEÇMİŞ DIALOG ---
        if (showHistoryDialog) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showHistoryDialog = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D1B2A))
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YEREL BELLEK SOHBETLERİ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            IconButton(onClick = { showHistoryDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Kapat",
                                    tint = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (sessions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Yerel hafızada kayıtlı sohbet bulunamadı.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(sessions) { session ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x11FFFFFF))
                                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                            .clickable {
                                                onLoadSession(session.id)
                                                showHistoryDialog = false
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = session.title,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Model: ${session.modelId ?: "Varsayılan"}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteSession(session.id) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onStartNewChat()
                                    showHistoryDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yeni Sohbet")
                            }
                        }
                    }
                }
            }
        }
    }
}
