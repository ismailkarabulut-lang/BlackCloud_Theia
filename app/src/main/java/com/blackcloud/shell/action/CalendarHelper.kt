// === app/src/main/java/com/blackcloud/shell/action/CalendarHelper.kt ===
package com.blackcloud.shell.action

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Android yerel takvim işlemlerini sarmalayan yardımcı sınıf.
 * READ_CALENDAR ve WRITE_CALENDAR izinleri gerektirir.
 */
object CalendarHelper {

    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    /**
     * Cihazın yerel takvimine yeni bir etkinlik ekler.
     * @return Başarılı ise true, hata alınırsa veya izin eksikse false.
     */
    fun createEvent(
        context: Context,
        title: String,
        startTimeIso: String,
        endTimeIso: String,
        description: String,
        location: String
    ): Boolean {
        return try {
            val startMillis = parseIsoTime(startTimeIso) ?: System.currentTimeMillis()
            val endMillis = parseIsoTime(endTimeIso) ?: (startMillis + 3600_000) // Varsayılan 1 saat

            val calendarId = getActiveCalendarId(context)

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.EVENT_LOCATION, location)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri != null
        } catch (e: SecurityException) {
            // İzin verilmemiş olabilir
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun parseIsoTime(isoTime: String): Long? {
        return try {
            // ISO formatlarını destekleyelim (örn: T harfinin kırpılması vb.)
            val cleanTime = isoTime.replace("Z", "")
            isoFormatter.parse(cleanTime)?.time
        } catch (e: Exception) {
            null
        }
    }

    private fun getActiveCalendarId(context: Context): Long {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.VISIBLE} = 1"
        return try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getLong(0)
                } else {
                    1L
                }
            } ?: 1L
        } catch (e: Exception) {
            1L
        }
    }
}
