package dev.senda.launcher.data

import android.Manifest
import android.app.AlarmManager
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AgendaEvent(val id: Long, val title: String, val begin: Long, val end: Long, val allDay: Boolean)
data class HeaderInfo(val alarmAt: Long? = null, val event: AgendaEvent? = null, val calendarPermission: Boolean = false, val calendarError: Boolean = false, val calendarLoading: Boolean = false)

/** Upcoming instances include recurring occurrences. No calendar writes, networking or persistent event cache. */
class HeaderRepository(private val context: Context) {
    fun canReadCalendar() = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    suspend fun load(settings: HeaderSettings): HeaderInfo = withContext(Dispatchers.IO) {
        val alarm = if (settings.showAlarm) context.getSystemService(AlarmManager::class.java)?.nextAlarmClock?.triggerTime else null
        val permitted = canReadCalendar()
        if (!settings.showCalendar || !permitted) return@withContext HeaderInfo(alarmAt = alarm, calendarPermission = permitted)
        try {
            val now = System.currentTimeMillis()
            val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also {
                ContentUris.appendId(it, now); ContentUris.appendId(it, now + 7 * 86_400_000L)
            }.build()
            val columns = arrayOf(CalendarContract.Instances.EVENT_ID, CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY)
            val selection = "${CalendarContract.Instances.VISIBLE}=1 AND ${CalendarContract.Instances.END}>? AND " +
                "(${CalendarContract.Instances.STATUS} IS NULL OR ${CalendarContract.Instances.STATUS}!=?) AND " +
                "(${CalendarContract.Instances.SELF_ATTENDEE_STATUS} IS NULL OR ${CalendarContract.Instances.SELF_ATTENDEE_STATUS}!=?)"
            val event = context.contentResolver.query(uri, columns, selection,
                arrayOf(now.toString(), CalendarContract.Events.STATUS_CANCELED.toString(), CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED.toString()),
                "${CalendarContract.Instances.BEGIN} ASC")?.use { cursor ->
                if (!cursor.moveToFirst()) null else AgendaEvent(cursor.getLong(0), cursor.getString(1)?.take(200)?.takeIf(String::isNotBlank) ?: "Evento sin título", cursor.getLong(2), cursor.getLong(3), cursor.getInt(4) == 1)
            }
            HeaderInfo(alarm, event, permitted)
        } catch (_: SecurityException) { HeaderInfo(alarmAt = alarm) }
        catch (_: Exception) { HeaderInfo(alarmAt = alarm, calendarPermission = permitted, calendarError = true) }
    }
}
