// === app/src/main/java/com/blackcloud/shell/action/ActionType.kt ===
package com.blackcloud.shell.action

/**
 * Backend'den gelen eylem taleplerini güvenli ve tipli bir şekilde yöneten mühürlü sınıf.
 * Beş temel eylem türünü modeller: Takvim Ekleme, Metin Okuma, Ses Girişi İsteme, Bildirim Gösterme ve Onay İsteme.
 */
sealed class ActionType {

    /**
     * Takvime yeni bir etkinlik ekleme isteği.
     */
    data class CreateCalendarEvent(
        val actionId: String?,
        val title: String,
        val startTime: String, // ISO 8601 biçiminde (ör. "2026-05-21T14:00:00")
        val endTime: String,
        val description: String,
        val location: String
    ) : ActionType()

    /**
     * Sunucudan gelen metni sesli olarak cihaza okutma isteği (TTS).
     */
    data class SpeakText(
        val text: String
    ) : ActionType()

    /**
     * Kullanıcıdan sesli girdi alıp STT üzerinden metne çevirerek gönderme isteği.
     */
    object RequestVoiceInput : ActionType()

    /**
     * Cihazda yerel bildirim gösterme isteği.
     */
    data class ShowNotification(
        val title: String,
        val body: String,
        val priority: String // HIGH, MEDIUM, LOW
    ) : ActionType()

    /**
     * Kritik veya yan etkileri olan eylemler için kullanıcıdan onay isteme ekranı (Bottom Sheet).
     */
    data class RequestUserConfirmation(
        val actionId: String,
        val message: String
    ) : ActionType()
}
