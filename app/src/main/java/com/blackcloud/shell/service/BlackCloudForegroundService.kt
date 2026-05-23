// === app/src/main/java/com/blackcloud/shell/service/BlackCloudForegroundService.kt ===
package com.blackcloud.shell.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blackcloud.shell.MainActivity
import com.blackcloud.shell.data.repository.TheiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * PROJECT THEIA BLACKCLOUD Arka Plan Ön Plan Servisi (Foreground Service).
 * Yerel Python çekirdeğiyle (FastAPI) bağlantıyı ayakta tutar, periyodik ping atar
 * ve bağlantı durumunu hem bildirim panelinde hem de StateFlow üzerinden UI katmanında paylaşır.
 */
class BlackCloudForegroundService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val repository = TheiaRepository()
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val NOTIFICATION_ID = 8765
        private const val CHANNEL_ID = "blackcloud_foreground_service"
        private const val CHANNEL_NAME = "Theia BlackCloud Durumu"

        /**
         * Arka plan API servisinin ayakta olup olmadığını UI katmanının izlemesi için ortak akış.
         */
        val isBackendAlive = MutableStateFlow(false)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // İlk bildirimi kapalı/çevrimdışı olarak başlat
        val notification = buildStatusNotification(isBackendAlive.value)
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe fallback: try starting without type descriptor if restricted on Android 14+ / targetSdk 36
            try {
                startForeground(NOTIFICATION_ID, notification)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // 10 saniyede bir ping atıp durumu güncelleyen döngüyü başlat
        serviceScope.launch {
            while (isActive) {
                val alive = repository.ping()
                if (isBackendAlive.value != alive) {
                    isBackendAlive.value = alive
                    // Durum değişince bildirimi tazele
                    notificationManager.notify(NOTIFICATION_ID, buildStatusNotification(alive))
                }
                delay(10000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Theia Blackcloud bağlantısı ve arka plan işlemleri"
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Bağlantı durumuna göre bildirimi inşa eder.
     * Bağlıyken yeşil (0xFF00E676), kopukken kırmızı (0xFFFF1744) durum rengi kullanılır.
     */
    private fun buildStatusNotification(isAlive: Boolean): Notification {
        val title = if (isAlive) "Theia BlackCloud: BAĞLI" else "Theia BlackCloud: ÇEVRİMDIŞI"
        val subtitle = if (isAlive) "Yerel asistan çekirdeği aktif ve çalışıyor." else "Yerel asistan çekirdeğine erişilemiyor. (Termux'u kontrol edin)"
        val statusColor = if (isAlive) 0xFF00E676.toInt() else 0xFFFF1744.toInt()

        // Bildirime tıklayınca MainActivity'i açan niyet
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, openIntent, pendingIntentFlags)

        // Sesli komut butonu niyeti (Zarif bir kolaylık)
        val voiceIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "voice_input")
        }
        val voicePendingIntent = PendingIntent.getActivity(this, 1, voiceIntent, pendingIntentFlags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(android.R.drawable.presence_online)
            .setColor(statusColor)
            .setColorized(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(subtitle))
            .addAction(
                android.R.drawable.ic_btn_speak_now,
                "Sesli Komut Ver",
                voicePendingIntent
            )
            .build()
    }
}
