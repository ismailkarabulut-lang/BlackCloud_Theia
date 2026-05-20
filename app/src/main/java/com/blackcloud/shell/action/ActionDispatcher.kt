// === app/src/main/java/com/blackcloud/shell/action/ActionDispatcher.kt ===
package com.blackcloud.shell.action

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.blackcloud.shell.data.model.SseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Eylem sonuçlarını bildirmek için kullanılan veri sınıfı.
 */
data class DispatchResult(
    val actionId: String,
    val success: Boolean,
    val message: String
)

/**
 * PROJECT THEIA BLACKCLOUD Eylem Dağıtıcı (ActionDispatcher) Sınıfı.
 * Gelen eylemleri (Action) uygun cihaz servislerine (Takvim, TTS, STT, Bildirim) yönlendirir.
 * "Gatekeeper" felsefesini uygular; yan etkili eylemler önce kullanıcı onayına sunulur.
 */
object ActionDispatcher {

    private val _pendingActions = MutableSharedFlow<ActionType>(extraBufferCapacity = 10)
    /**
     * UI'ın dinleyip onay penceresi (Bottom Sheet) açması gereken eylemlerin akışı.
     */
    val pendingActions = _pendingActions.asSharedFlow()

    private val _resultsChannel = Channel<DispatchResult>(Channel.BUFFERED)
    /**
     * Eylem sonuçlarını UI'a bildirmek için kanal.
     */
    val resultsChannel = _resultsChannel

    private val scope = CoroutineScope(Dispatchers.Default)

    private var notificationManager: NotificationManager? = null

    /**
     * Bir asistan eylemini çözümler ve ilgili servisleri tetikler.
     */
    fun dispatch(context: Context, action: SseEvent.Action) {
        val actionId = action.actionId ?: "unknown"
        val payload = action.payload

        when (action.actionType) {
            "speak_text" -> {
                val text = payload["text"] as? String ?: ""
                scope.launch {
                    _pendingActions.emit(ActionType.SpeakText(text))
                }
            }

            "request_voice_input" -> {
                scope.launch {
                    _pendingActions.emit(ActionType.RequestVoiceInput)
                }
            }

            "show_notification" -> {
                val title = payload["title"] as? String ?: "Theia Blackcloud"
                val body = payload["body"] as? String ?: ""
                val priority = payload["priority"] as? String ?: "LOW"
                showSystemNotification(context, title, body, priority)
                // Bildirimler zararsızdır, doğrudan başarı bildirelim
                scope.launch {
                    _resultsChannel.send(DispatchResult(actionId, true, "Bildirim gösterildi."))
                }
            }

            "create_calendar_event" -> {
                val title = payload["title"] as? String ?: "Yeni Etkinlik"
                val startTime = payload["startTime"] as? String ?: ""
                val endTime = payload["endTime"] as? String ?: ""
                val description = payload["description"] as? String ?: ""
                val location = payload["location"] as? String ?: ""

                // "Gatekeeper" Felsefesi: Takvim ekleme işlemi kritik olduğundan otomatik onaylanmaz, önce UI'a yönlendirilir.
                scope.launch {
                    _pendingActions.emit(
                        ActionType.CreateCalendarEvent(
                            actionId = actionId,
                            title = title,
                            startTime = startTime,
                            endTime = endTime,
                            description = description,
                            location = location
                        )
                    )
                }
            }

            "request_user_confirmation" -> {
                val message = payload["message"] as? String ?: "İşlemi onaylıyor musunuz?"
                scope.launch {
                    _pendingActions.emit(
                        ActionType.RequestUserConfirmation(
                            actionId = actionId,
                            message = message
                        )
                    )
                }
            }

            else -> {
                scope.launch {
                    _resultsChannel.send(
                        DispatchResult(actionId, false, "Desteklenmeyen eylem türü: ${action.actionType}")
                    )
                }
            }
        }
    }

    private fun showSystemNotification(context: Context, title: String, body: String, priority: String) {
        val channelId = "theia_actions_channel"
        if (notificationManager == null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val valImportance = when (priority.uppercase()) {
                "HIGH" -> NotificationManager.IMPORTANCE_HIGH
                "MEDIUM" -> NotificationManager.IMPORTANCE_DEFAULT
                else -> NotificationManager.IMPORTANCE_LOW
            }
            val channel = NotificationChannel(channelId, "Theia Eylemleri", valImportance).apply {
                description = "Theia asistan eylemleri için bildirim kanalı"
            }
            notificationManager?.createNotificationChannel(channel)
        }

        val valPriority = when (priority.uppercase()) {
            "HIGH" -> NotificationCompat.PRIORITY_HIGH
            "MEDIUM" -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(valPriority)
            .setAutoCancel(true)

        notificationManager?.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
