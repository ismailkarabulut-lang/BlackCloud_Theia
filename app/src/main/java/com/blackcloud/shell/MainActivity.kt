// === app/src/main/java/com/blackcloud/shell/MainActivity.kt ===
package com.blackcloud.shell

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import com.blackcloud.shell.data.model.ModelManager
import com.blackcloud.shell.service.BlackCloudForegroundService
import com.blackcloud.shell.ui.screens.BlackCloudMainScreen
import com.blackcloud.shell.ui.theme.TheiaTheme
import com.blackcloud.shell.ui.viewmodel.BlackCloudViewModel
import com.blackcloud.shell.ui.viewmodel.BlackCloudViewModelFactory

/**
 * PROJECT THEIA BLACKCLOUD Ana Giriş Noktası (MainActivity).
 * Uygulamanın başlatılmasını, yetkilendirme isteklerini, ön plan servisinin ayağa kaldırılmasını
 * ve bildirim paneli "Sesli Komut" niyetlerinin (intent) dinlenmesini yönetir.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: BlackCloudViewModel by viewModels {
        BlackCloudViewModelFactory(applicationContext)
    }

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge To Edge Desteği (Sistem çubuğu bütünleşimi)
        enableEdgeToEdge()

        // Model yöneticisini başlat
        ModelManager.init(this)

        // Çalışma zamanında gerekli izinleri talep et (Sesli komut & Takvim entegrasyonu)
        requestPermissions(requiredPermissions, 123)

        // Arka Plan/Ön Plan Servisini Başlat (Bağlantı durum pinglemesi için)
        bootForegroundService()

        // Başlangıç niyetini yorumla
        intent?.let { handleVoiceIntent(it) }

        setContent {
            val activeThemeState = viewModel.activeTheme.collectAsState()
            TheiaTheme(themeType = activeThemeState.value) {
                BlackCloudMainScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleVoiceIntent(intent)
    }

    private fun bootForegroundService() {
        val serviceIntent = Intent(this, BlackCloudForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun handleVoiceIntent(intent: Intent) {
        val action = intent.getStringExtra("action")
        if (action == "voice_input") {
            // Bildirim panelindeki ses butonuna basıldıysa STT başlat
            viewModel.startVoiceInput()
        }
    }
}
