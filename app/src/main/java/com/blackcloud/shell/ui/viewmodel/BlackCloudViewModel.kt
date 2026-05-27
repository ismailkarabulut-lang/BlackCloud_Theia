// === app/src/main/java/com/blackcloud/shell/ui/viewmodel/BlackCloudViewModel.kt ===
package com.blackcloud.shell.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blackcloud.shell.action.ActionDispatcher
import com.blackcloud.shell.action.ActionType
import com.blackcloud.shell.action.CalendarHelper
import com.blackcloud.shell.ui.components.ConnectionStatus
import com.blackcloud.shell.ui.components.Message
import com.blackcloud.shell.ui.components.MessageSender
import com.blackcloud.shell.data.model.ChatRequest
import com.blackcloud.shell.data.model.Project
import com.blackcloud.shell.data.model.SseEvent
import com.blackcloud.shell.data.repository.TheiaRepository
import com.blackcloud.shell.data.api.TheiaApiClient
import com.blackcloud.shell.service.BlackCloudForegroundService
import com.blackcloud.shell.voice.VoiceInputManager
import com.blackcloud.shell.voice.VoiceOutputManager
import com.blackcloud.shell.voice.VoiceResult
import com.blackcloud.shell.ui.theme.ThemeType
import com.blackcloud.shell.data.repository.HistoryRepository
import com.blackcloud.shell.data.database.ChatSessionEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * PROJECT THEIA BLACKCLOUD Ana ViewModel Sınıfı.
 * Ekranlar arası geçiş durumunu, sohbet akışlarını, ses giriş / çıkış yönetimini
 * ve yerel Python çekirdeği (FastAPI) ile koordinasyonu sağlar.
 */
class BlackCloudViewModel(
    private val context: Context,
    private val repository: TheiaRepository = TheiaRepository(),
    private val voiceInputManager: VoiceInputManager,
    private val voiceOutputManager: VoiceOutputManager
) : ViewModel() {

    // Tema seçimi ve SharedPreferences ile kalıcı depolama
    private val themePrefs = context.getSharedPreferences("theia_theme_prefs", Context.MODE_PRIVATE)
    private val _activeTheme = MutableStateFlow(loadPersistedTheme())
    val activeTheme = _activeTheme.asStateFlow()

    private val apiPrefs = context.getSharedPreferences("theia_api_config", Context.MODE_PRIVATE)
    private val _baseUrl = MutableStateFlow(loadPersistedBaseUrl())
    val baseUrl = _baseUrl.asStateFlow()

    private val _modelName = MutableStateFlow(loadPersistedModelName())
    val modelName = _modelName.asStateFlow()

    private val _apiKey = MutableStateFlow(loadPersistedApiKey())
    val apiKey = _apiKey.asStateFlow()

    private fun loadPersistedTheme(): ThemeType {
        val savedName = themePrefs.getString("selected_theme", ThemeType.ELEGANT_DARK.name)
        return try {
            ThemeType.valueOf(savedName ?: ThemeType.ELEGANT_DARK.name)
        } catch (e: Exception) {
            ThemeType.ELEGANT_DARK
        }
    }

    private fun loadPersistedBaseUrl(): String {
        val savedUrl = apiPrefs.getString("base_url", "http://10.202.60.1:8000/api/") ?: "http://10.202.60.1:8000/api/"
        TheiaApiClient.updateBaseUrl(savedUrl)
        return TheiaApiClient.getBaseUrl()
    }

    private fun loadPersistedModelName(): String {
        val savedModel = apiPrefs.getString("selected_model", "deep_synthesis_v4") ?: "deep_synthesis_v4"
        TheiaApiClient.updateModelName(savedModel)
        return savedModel
    }

    private fun loadPersistedApiKey(): String {
        val savedKey = apiPrefs.getString("api_key", "") ?: ""
        TheiaApiClient.updateApiKey(savedKey)
        return savedKey
    }

    fun setTheme(themeType: ThemeType) {
        _activeTheme.value = themeType
        themePrefs.edit().putString("selected_theme", themeType.name).apply()
    }

    fun setBaseUrl(newUrl: String) {
        var sanitized = newUrl.trim()
        if (sanitized.isNotEmpty()) {
            if (!sanitized.endsWith("/")) {
                sanitized += "/"
            }
            _baseUrl.value = sanitized
            apiPrefs.edit().putString("base_url", sanitized).apply()
            TheiaApiClient.updateBaseUrl(sanitized)
            loadProjects()
        }
    }

    fun setModelName(newModel: String) {
        val trimmed = newModel.trim()
        _modelName.value = trimmed
        apiPrefs.edit().putString("selected_model", trimmed).apply()
        TheiaApiClient.updateModelName(trimmed)
    }

    fun setApiKey(newKey: String) {
        val trimmed = newKey.trim()
        _apiKey.value = trimmed
        apiPrefs.edit().putString("api_key", trimmed).apply()
        TheiaApiClient.updateApiKey(trimmed)
    }

    // Ekran durumları
    enum class Screen {
        ProjectSwitcher,
        ChatWorkspace
    }

    private val _currentScreen = MutableStateFlow(Screen.ChatWorkspace)
    val currentScreen = _currentScreen.asStateFlow()

    // Projeler listesi ve seçili proje
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject = _activeProject.asStateFlow()

    // Sohbet akışı mesajları
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    // Bağlantı durumu ve otomatik yeniden bağlanma parametreleri
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus = _connectionStatus.asStateFlow()

    private var sttJob: Job? = null
    private var chatStreamJob: Job? = null
    private var pingJob: Job? = null
    
    // Mikrofon ve konuşma ses durumu
    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _voiceInputText = MutableStateFlow("")

    private val _currentPendingAction = MutableStateFlow<ActionType?>(null)
    val currentPendingAction = _currentPendingAction.asStateFlow()

    private val historyRepository = HistoryRepository(context)

    val chatSessions = historyRepository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var sessionId = UUID.randomUUID().toString()

    init {
        // Gelişmiş Sohbet Kabuğu için varsayılan karşılama mesajını tanımla
        _messages.value = listOf(
            Message(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.ASSISTANT,
                text = "Gelişmiş Sohbet Kabuğu aktif. Size nasıl destek olabilirim?",
                isComplete = true
            )
        )

        // Foreground service'den gelen canlı bağlantı bilgisini izliyoruz
        viewModelScope.launch {
            BlackCloudForegroundService.isBackendAlive.collect { isAlive ->
                _connectionStatus.value = if (isAlive) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
            }
        }

        // ActionDispatcher'dan gelen asistan eylemlerini izle
        viewModelScope.launch {
            ActionDispatcher.pendingActions.collect { action ->
                handleIncomingAction(action)
            }
        }

        startPeriodicChecking()
        loadProjects()
    }

    /**
     * Yerel sunucudan projeleri yükler.
     */
    fun loadProjects() {
        viewModelScope.launch {
            val fetched = repository.getProjects()
            if (fetched.isNotEmpty()) {
                _projects.value = fetched
                val active = repository.getActiveProject()
                _activeProject.value = active
            }
        }
    }

    /**
     * Bir projeyi aktif hale getirip sohbet odasını (Workspace) açar.
     */
    fun selectProject(project: Project) {
        _activeProject.value = project
        _currentScreen.value = Screen.ChatWorkspace
        // Örnek başlangıç asistan karşılama iletisi
        _messages.value = listOf(
            Message(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.ASSISTANT,
                text = "${project.name} projesi yüklendi. Size nasıl destek olabilirim?",
                isComplete = true
            )
        )
    }

    /**
     * Genel sohbeti başlatıp sohbet odasına yönlendirir.
     */
    fun selectGeneralChat() {
        _activeProject.value = null
        _currentScreen.value = Screen.ChatWorkspace
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.ASSISTANT,
                    text = "Genel Sohbet başlatıldı. Herhangi bir projeyi seçebilir veya bana genel bir soru yöneltebilirsiniz.",
                    isComplete = true
                )
            )
        }
    }

    /**
     * Çalışma alanındaki proje bağlamını dinamik olarak değiştirir.
     */
    fun setWorkspaceProject(project: Project?) {
        _activeProject.value = project
        val projectName = project?.name ?: "Genel Sohbet"
        _messages.value = _messages.value + Message(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.ASSISTANT,
            text = "Çalışma alanı değiştirildi: $projectName. Sohbet bağlamı güncellendi.",
            isComplete = true
        )
    }

    /**
     * Ekran geçişlerini koordine eder.
     */
    fun navigateToScreen(screen: Screen) {
        _currentScreen.value = screen
        if (screen == Screen.ChatWorkspace && _activeProject.value == null && _messages.value.isEmpty()) {
            _messages.value = listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.ASSISTANT,
                    text = "Genel Sohbet başlatıldı. Herhangi bir projeyi seçebilir veya bana genel bir soru yöneltebilirsiniz.",
                    isComplete = true
                )
            )
        }
    }

    /**
     * Proje seçim ekranına geri döner.
     */
    fun navigateBackToProjects() {
        _currentScreen.value = Screen.ProjectSwitcher
    }

    /**
     * Starts a brand new blank chat session, freeing the view.
     */
    fun startNewChat() {
        chatStreamJob?.cancel()
        sessionId = UUID.randomUUID().toString()
        val introMessage = if (_activeProject.value != null) {
            "${_activeProject.value!!.name} projesi yüklendi. Size nasıl destek olabilirim?"
        } else {
            "Genel Sohbet başlatıldı. Herhangi bir projeyi seçebilir veya bana genel bir soru yöneltebilirsiniz."
        }
        _messages.value = listOf(
            Message(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.ASSISTANT,
                text = introMessage,
                isComplete = true
            )
        )
    }

    /**
     * Loads messages and state from an existing historical chat session.
     */
    fun loadChatSession(targetSessionId: String) {
        chatStreamJob?.cancel()
        sessionId = targetSessionId
        viewModelScope.launch {
            val pastMessages = historyRepository.getMessagesForSession(targetSessionId)
            val currentSessions = historyRepository.allSessions.stateIn(viewModelScope).value
            val target = currentSessions.find { it.id == targetSessionId }
            if (target != null) {
                // Restore project context
                _activeProject.value = _projects.value.find { it.id == target.projectId }
            }
            _messages.value = pastMessages
        }
    }

    /**
     * Deletes a specified chat session from local storage.
     */
    fun deleteChatSession(targetSessionId: String) {
        viewModelScope.launch {
            historyRepository.deleteSession(targetSessionId)
            if (sessionId == targetSessionId) {
                startNewChat()
            }
        }
    }

    /**
     * Clears all local chat session data from the device.
     */
    fun clearAllChatHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            startNewChat()
        }
    }

    /**
     * Girdi metnini günceller.
     */
    fun updateInputText(text: String) {
        _inputText.value = text
    }

    /**
     * Asistan cevabını sesli okur.
     */
    fun speakMessage(text: String) {
        voiceOutputManager.speak(text)
    }

    /**
     * Metin mesajı gönderir ve asistan sse akışını başlatır.
     */
    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        _inputText.value = ""
        triggerChatQuery(text)
    }

    private fun triggerChatQuery(text: String) {
        // Kullanıcı mesajını ekle
        val userMsgId = UUID.randomUUID().toString()
        val formattedUserMsg = Message(id = userMsgId, sender = MessageSender.USER, text = text, isComplete = true)
        _messages.value = _messages.value + formattedUserMsg

        // Yerel SQLite tablosuna yaz
        viewModelScope.launch {
            historyRepository.saveMessage(sessionId, formattedUserMsg, _activeProject.value?.id)
        }

        // Asistan için boş mesaj taslağı oluştur
        val assistantMsgId = UUID.randomUUID().toString()
        val starterAssistantMsg = Message(id = assistantMsgId, sender = MessageSender.ASSISTANT, text = "", isComplete = false)
        _messages.value = _messages.value + starterAssistantMsg

        chatStreamJob?.cancel()
        chatStreamJob = viewModelScope.launch {
            val req = ChatRequest(
                projectId = _activeProject.value?.id,
                message = text,
                sessionId = sessionId
            )

            repository.streamChat(req)
                .catch { error ->
                    updateAssistantMessage(assistantMsgId, "Sunucuyla olan bağlantıda teknik aksaklık oluştu: ${error.message}", true)
                    handleNetworkFailure()
                }
                .collect { event ->
                    when (event) {
                        is SseEvent.TextChunk -> {
                            appendAssistantText(assistantMsgId, event.text)
                        }
                        is SseEvent.Action -> {
                            // Eylemi sisteme dağıt
                            ActionDispatcher.dispatch(context, event)
                        }
                        is SseEvent.Metadata -> {
                            // Doğrulama durumunu asistan balonuna ekle
                            attachKkypMetadata(assistantMsgId, event.kkyp)
                        }
                        is SseEvent.Done -> {
                            markAssistantMessageComplete(assistantMsgId)
                        }
                        is SseEvent.Error -> {
                            updateAssistantMessage(assistantMsgId, "Hata: ${event.message}", true)
                        }
                    }
                }
        }
    }

    // Mesaj balonlarını güncelleme yardımcı fonksiyonları
    private fun appendAssistantText(msgId: String, newText: String) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == msgId) {
                msg.copy(text = msg.text + newText)
            } else msg
        }
    }

    private fun updateAssistantMessage(msgId: String, finalText: String, isComplete: Boolean) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == msgId) {
                val updated = msg.copy(text = finalText, isComplete = isComplete)
                if (isComplete) {
                    viewModelScope.launch {
                        historyRepository.saveMessage(sessionId, updated, _activeProject.value?.id)
                    }
                }
                updated
            } else msg
        }
    }

    private fun attachKkypMetadata(msgId: String, metadata: com.blackcloud.shell.data.model.KkypMetadata) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == msgId) {
                val updated = msg.copy(kkyp = metadata, isComplete = true)
                viewModelScope.launch {
                    historyRepository.saveMessage(sessionId, updated, _activeProject.value?.id)
                }
                updated
            } else msg
        }
    }

    private fun markAssistantMessageComplete(msgId: String) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == msgId) {
                val updated = msg.copy(isComplete = true)
                viewModelScope.launch {
                    historyRepository.saveMessage(sessionId, updated, _activeProject.value?.id)
                }
                updated
            } else msg
        }
    }

    /**
     * Mikrofon basılı tutulmaya başladığında STT dinlemesini başlatır.
     */
    fun startVoiceInput() {
        _isListening.value = true
        _voiceInputText.value = ""
        sttJob?.cancel()
        sttJob = viewModelScope.launch {
            voiceInputManager.startListening().collect { result ->
                when (result) {
                    is VoiceResult.Partial -> {
                        _voiceInputText.value = result.text
                    }
                    is VoiceResult.Success -> {
                        _voiceInputText.value = result.text
                        _isListening.value = false
                        voiceInputManager.stopListening()
                        val voiceText = result.text.trim()
                        if (voiceText.isNotEmpty()) {
                            triggerChatQuery(voiceText)
                        }
                        sttJob?.cancel()
                    }
                    is VoiceResult.Error -> {
                        _isListening.value = false
                        sttJob?.cancel()
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * Mikrofon bırakıldığında dinlemeyi bitirir ve toplanan metni asistan akışına yollar.
     */
    fun stopVoiceInput() {
        _isListening.value = false
        voiceInputManager.stopListening()
        sttJob?.cancel()
        sttJob = null

        val voiceText = _voiceInputText.value.trim()
        if (voiceText.isNotEmpty()) {
            triggerChatQuery(voiceText)
        }
    }

    /**
     * Askıdaki eylemi kullanıcı onayından geçmesi için kaydeder.
     */
    private fun handleIncomingAction(action: ActionType) {
        when (action) {
            is ActionType.SpeakText -> {
                speakMessage(action.text)
            }
            is ActionType.RequestVoiceInput -> {
                viewModelScope.launch {
                    delay(1000) // TTS kaydı bitsin diye hafif bekleme
                    startVoiceInput()
                }
            }
            else -> {
                // Sadece takvim ve asıl onay isteyenleri Bottom Sheet'e yönlendiriyoruz
                _currentPendingAction.value = action
            }
        }
    }

    /**
     * Askıdaki (takvim vb.) eylemi yeşil ışık yakarak onaylar ve yerel işlemini icra eder.
     */
    fun confirmPendingAction(context: Context) {
        val action = _currentPendingAction.value ?: return
        _currentPendingAction.value = null

        viewModelScope.launch {
            val actionId = when (action) {
                is ActionType.CreateCalendarEvent -> action.actionId ?: "unknown"
                is ActionType.RequestUserConfirmation -> action.actionId
                else -> "unknown"
            }

            var success = false
            var msg = "Eylem tamamlanamadı."

            when (action) {
                is ActionType.CreateCalendarEvent -> {
                    success = CalendarHelper.createEvent(
                        context,
                        title = action.title,
                        startTimeIso = action.startTime,
                        endTimeIso = action.endTime,
                        description = action.description,
                        location = action.location
                    )
                    msg = if (success) "Takvim etkinliği başarıyla kaydedildi." else "Takvime yazma hatası oluştu."
                }
                is ActionType.RequestUserConfirmation -> {
                    success = true
                    msg = "Kullanıcı işlemi onayladı."
                }
                else -> {}
            }

            // Backend'e geri rapor et
            repository.reportActionResult(actionId, success, msg)
        }
    }

    /**
     * Askıdaki eylemi reddeder ve sunucuya geri bildirir.
     */
    fun dismissPendingAction() {
        val action = _currentPendingAction.value ?: return
        _currentPendingAction.value = null

        viewModelScope.launch {
            val actionId = when (action) {
                is ActionType.CreateCalendarEvent -> action.actionId ?: "unknown"
                is ActionType.RequestUserConfirmation -> action.actionId
                else -> "unknown"
            }
            repository.reportActionResult(actionId, false, "Kullanıcı işlemi reddetti (Gatekeeper engeli).")
        }
    }

    /**
     * Chat ekranından doğrudan takvim entegreli yerel görev kurgular.
     */
    fun createLocalTaskReminder(title: String, description: String, dateIso: String, location: String) {
        viewModelScope.launch {
            val success = CalendarHelper.createEvent(
                context = context,
                title = title,
                startTimeIso = dateIso,
                endTimeIso = dateIso,
                description = description,
                location = location
            )
            
            val feedbackMsgId = UUID.randomUUID().toString()
            val feedbackText = if (success) {
                "📅 **Yeni Görev Takvime Eklendi!**\n\n**Başlık:** $title\n**Zaman:** $dateIso\n**Açıklama:** $description\n**Konum:** $location\n\n*Görev hatırlatıcınız yerel takvim veritabanı ile başarıyla senkronize edildi.*"
            } else {
                "❌ **Takvim Hatası!**\n\n'$title' görevi takvime eklenirken bir hata oluştu veya takvim yazma izinleri eksik. Lütfen ayarlardan takvim izinlerini kontrol edin."
            }
            
            val feedbackMsg = Message(
                id = feedbackMsgId,
                sender = MessageSender.ASSISTANT,
                text = feedbackText,
                isComplete = true
            )
            
            _messages.value = _messages.value + feedbackMsg
            
            // Yerel SQLite tablosuna kaydet
            historyRepository.saveMessage(sessionId, feedbackMsg, _activeProject.value?.id)
        }
    }

    /**
     * Bağlantı kesildiğinde otomatik exponential backoff mekanizmasını devreye alır.
     * Denemeler: 2sn, 4sn, 8sn, 16sn. Sonrasında manuel moda geçilir.
     */
    private fun handleNetworkFailure() {
        viewModelScope.launch {
            _connectionStatus.value = ConnectionStatus.RECONNECTING
            val backoffs = listOf(2000L, 4000L, 8000L, 16000L)
            
            for (delayMs in backoffs) {
                delay(delayMs)
                val alive = repository.ping()
                if (alive) {
                    _connectionStatus.value = ConnectionStatus.CONNECTED
                    loadProjects()
                    return@launch
                }
            }
            // Tüm denemeler biterse kullanıcıya bırakıyoruz
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
        }
    }

    /**
     * Butondan elle yeniden bağlanmayı tetikler.
     */
    fun triggerManualReconnect() {
        viewModelScope.launch {
            _connectionStatus.value = ConnectionStatus.RECONNECTING
            val alive = repository.ping()
            if (alive) {
                _connectionStatus.value = ConnectionStatus.CONNECTED
                loadProjects()
            } else {
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
            }
        }
    }

    private fun startPeriodicChecking() {
        pingJob?.cancel()
        pingJob = viewModelScope.launch {
            while (true) {
                delay(15000)
                val alive = repository.ping()
                if (alive) {
                    if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
                        _connectionStatus.value = ConnectionStatus.CONNECTED
                        loadProjects()
                    }
                } else {
                    if (_connectionStatus.value == ConnectionStatus.CONNECTED) {
                        handleNetworkFailure()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sttJob?.cancel()
        chatStreamJob?.cancel()
        pingJob?.cancel()
        voiceOutputManager.shutdown()
    }
}

/**
 * ViewModel üretimi için özel fabrika (Factory) sınıfı.
 * Ses servislerinin doğru Context ile başlatılabilmesini sağlar.
 */
class BlackCloudViewModelFactory(
    private val context: Context,
    private val repository: TheiaRepository = TheiaRepository()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlackCloudViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlackCloudViewModel(
                context = context,
                repository = repository,
                voiceInputManager = VoiceInputManager(context),
                voiceOutputManager = VoiceOutputManager(context)
            ) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı")
    }
}
