// === app/src/main/java/com/blackcloud/shell/ui/screens/BlackCloudMainScreen.kt ===
package com.blackcloud.shell.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blackcloud.shell.ui.components.ActionConfirmationSheet
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

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        // Ekran geçişlerini yumuşak bir crossfade efektiyle salla
        Crossfade(
            targetState = currentScreen.value,
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                BlackCloudViewModel.Screen.ProjectSwitcher -> {
                    ProjectSwitcherScreen(
                        projects = projects.value,
                        onProjectSelected = { project ->
                            viewModel.selectProject(project)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BlackCloudViewModel.Screen.ChatWorkspace -> {
                    val activeProj = activeProject.value
                    if (activeProj != null) {
                        ChatWorkspaceScreen(
                            project = activeProj,
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
