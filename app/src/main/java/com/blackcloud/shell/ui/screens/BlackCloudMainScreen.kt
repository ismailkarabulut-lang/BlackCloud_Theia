// === app/src/main/java/com/blackcloud/shell/ui/screens/BlackCloudMainScreen.kt ===
package com.blackcloud.shell.ui.screens

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blackcloud.shell.ui.components.ActionConfirmationSheet
import com.blackcloud.shell.ui.theme.BorderColor
import com.blackcloud.shell.ui.theme.DarkSurface
import com.blackcloud.shell.ui.theme.TextSecondary
import com.blackcloud.shell.ui.viewmodel.BlackCloudViewModel

/**
 * PROJECT THEIA BLACKCLOUD Ana Ekran Seçici ve Koordinatör Konteyneri.
 * Alt ekranları (Proje Seçici ve Sohbet Ekranı) koordine eder ve üst üste binmesi gereken modal bottom sheet'leri sunar.
 */
@Composable
fun BlackCloudMainScreen(
    viewModel: BlackCloudViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // v1 standardına göre zorunlu collectAsStateWithLifecycle kullanımı (Page 2)
    val currentScreen = viewModel.currentScreen.collectAsStateWithLifecycle()
    val projects = viewModel.projects.collectAsStateWithLifecycle()
    val activeProject = viewModel.activeProject.collectAsStateWithLifecycle()
    val messages = viewModel.messages.collectAsStateWithLifecycle()
    val inputText = viewModel.inputText.collectAsStateWithLifecycle()
    val connectionStatus = viewModel.connectionStatus.collectAsStateWithLifecycle()
    val isListening = viewModel.isListening.collectAsStateWithLifecycle()
    val currentPendingAction = viewModel.currentPendingAction.collectAsStateWithLifecycle()
    val activeTheme = viewModel.activeTheme.collectAsStateWithLifecycle()
    val chatSessions = viewModel.chatSessions.collectAsStateWithLifecycle()

    // 4 sekmeli alt barın durumunu yöneten ve senkronize eden yerel durum
    var activeTab by remember { mutableStateOf("studio") }

    LaunchedEffect(currentScreen.value) {
        activeTab = when (currentScreen.value) {
            BlackCloudViewModel.Screen.ProjectSwitcher -> "studio"
            BlackCloudViewModel.Screen.ChatWorkspace -> "chat"
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(DarkSurface)
                    .border(
                        1.dp,
                        BorderColor,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 1. Studio Sekmesi
                val studioSelected = activeTab == "studio"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { 
                            activeTab = "studio"
                            viewModel.navigateToScreen(BlackCloudViewModel.Screen.ProjectSwitcher)
                        }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = "Studio",
                        tint = if (studioSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "STUDIO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (studioSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                            fontWeight = if (studioSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // 2. Chat Sekmesi
                val chatSelected = activeTab == "chat"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { 
                            activeTab = "chat"
                            viewModel.navigateToScreen(BlackCloudViewModel.Screen.ChatWorkspace)
                        }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "Chat",
                        tint = if (chatSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "CHAT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (chatSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                            fontWeight = if (chatSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // 3. Models Sekmesi
                val modelsSelected = activeTab == "models"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { activeTab = "models" }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Models",
                        tint = if (modelsSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "MODELS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (modelsSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                            fontWeight = if (modelsSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // 4. Config Sekmesi
                val configSelected = activeTab == "config"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { activeTab = "config" }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Config",
                        tint = if (configSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "CONFIG",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (configSelected) MaterialTheme.colorScheme.primary else TextSecondary,
                            fontWeight = if (configSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Ekran geçişlerini yumuşak bir crossfade efektiyle salla
            Crossfade(
                targetState = activeTab,
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    "studio" -> {
                        ProjectSwitcherScreen(
                            projects = projects.value,
                            onProjectSelected = { project ->
                                viewModel.selectProject(project)
                            },
                            onGeneralChatSelected = {
                                viewModel.selectGeneralChat()
                            },
                            selectedTheme = activeTheme.value,
                            onThemeSelected = { theme ->
                                viewModel.setTheme(theme)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "chat" -> {
                        ChatWorkspaceScreen(
                            project = activeProject.value,
                            projectsList = projects.value,
                            onSelectProject = { selectedProj ->
                                viewModel.setWorkspaceProject(selectedProj)
                            },
                            messages = messages.value,
                            inputText = inputText.value,
                            onInputChanged = { text ->
                                viewModel.updateInputText(text)
                            },
                            onSendMessage = {
                                viewModel.sendMessage()
                            },
                            connectionStatus = connectionStatus.value,
                            onReconnect = {
                                viewModel.triggerManualReconnect()
                            },
                            isListening = isListening.value,
                            onStartVoice = {
                                viewModel.startVoiceInput()
                            },
                            onStopVoice = {
                                viewModel.stopVoiceInput()
                            },
                            onLongClickMessage = { message ->
                                // Basılı tutunca asistan mesajını oku
                                viewModel.speakMessage(message.text)
                            },
                            onBack = {
                                viewModel.navigateBackToProjects()
                            },
                            sessions = chatSessions.value,
                            onLoadSession = { targetSessionId ->
                                viewModel.loadChatSession(targetSessionId)
                            },
                            onDeleteSession = { targetSessionId ->
                                viewModel.deleteChatSession(targetSessionId)
                            },
                            onStartNewChat = {
                                viewModel.startNewChat()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "models" -> {
                        ModelsMainframeScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "config" -> {
                        ConfigConsoleScreen(
                            viewModel = viewModel,
                            selectedTheme = activeTheme.value,
                            onThemeSelected = { theme ->
                                viewModel.setTheme(theme)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Güvenli Onay Penceresi (Action Bottom Sheet) (Page 3)
        if (currentPendingAction.value != null) {
            ActionConfirmationSheet(
                action = currentPendingAction.value,
                onConfirm = {
                    viewModel.confirmPendingAction(context)
                },
                onDismiss = {
                    viewModel.dismissPendingAction()
                }
            )
        }
    }
}
