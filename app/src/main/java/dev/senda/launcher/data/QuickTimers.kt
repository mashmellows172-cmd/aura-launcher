package dev.senda.launcher.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.senda.launcher.MainActivity
import dev.senda.launcher.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

internal data class QuickTimer(
    val id: String,
    val name: String,
    val duration: Long,
    val remaining: Long,
    val deadline: Long = 0L,
    val wallDeadline: Long = 0L,
) {
    val running: Boolean get() = deadline != 0L

    fun timeLeft(now: Long): Long = if (running) (deadline - now).coerceAtLeast(0) else remaining

    fun start(now: Long, wallNow: Long, time: Long = remaining) =
        copy(remaining = time, deadline = now + time, wallDeadline = wallNow + time)

    fun pause(now: Long) = copy(remaining = timeLeft(now), deadline = 0L, wallDeadline = 0L)
}

/** Main-thread store shared by the launcher and the system alarm receiver. */
internal class QuickTimers private constructor(private val context: Context) {
    private val prefs = context.getSharedPreferences("quick_timers", Context.MODE_PRIVATE)
    private val boot = Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    private val items = MutableStateFlow(load())
    val timers = items.asStateFlow()

    private val alarm = context.getSystemService(AlarmManager::class.java)
    private val pendingAlarm = PendingIntent.getBroadcast(context, 0,
        Intent(context, QuickTimerReceiver::class.java).setAction(EXPIRE),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    private var scheduledDeadline: Long? = null
    private var scheduledExact = false

    fun add(name: String, minutes: Int) {
        expire()
        if (items.value.size >= MAX_TIMERS) return
        val duration = minutes.coerceIn(1, 999) * 60_000L
        val timer = QuickTimer(UUID.randomUUID().toString(), name, duration, duration)
            .start(SystemClock.elapsedRealtime(), System.currentTimeMillis())
        save(items.value + timer)
    }

    fun toggle(id: String) = update(id) { timer, now, wall ->
        when {
            timer.running -> timer.pause(now)
            timer.remaining > 0L -> timer.start(now, wall)
            else -> timer.start(now, wall, timer.duration)
        }
    }

    fun restart(id: String) = update(id) { timer, now, wall -> timer.start(now, wall, timer.duration) }

    fun addMinute(id: String) = update(id) { timer, now, wall ->
        val time = timer.timeLeft(now) + 60_000L
        if (timer.running || timer.remaining == 0L) timer.start(now, wall, time)
        else timer.copy(remaining = time)
    }

    fun remove(id: String) {
        save(items.value.filterNot { it.id == id })
        context.getSystemService(NotificationManager::class.java).cancel(id, NOTIFICATION_ID)
    }

    fun expire() {
        val now = SystemClock.elapsedRealtime()
        val due = items.value.filter { it.running && it.timeLeft(now) == 0L }
        if (due.isEmpty()) {
            schedule()
            return
        }
        val ids = due.mapTo(mutableSetOf()) { it.id }
        save(items.value.map { if (it.id in ids) it.pause(now) else it })
        due.forEach(::notifyFinished)
    }

    private fun update(id: String, change: (QuickTimer, Long, Long) -> QuickTimer) {
        expire()
        val now = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        save(items.value.map { if (it.id == id) change(it, now, wall) else it })
    }

    fun exactAlertsEnabled(): Boolean = Build.VERSION.SDK_INT < 31 || alarm.canScheduleExactAlarms()

    fun onAlarmReceived() {
        scheduledDeadline = null
        expire()
    }

    @SuppressLint("ScheduleExactAlarm") // Exact delivery is optional and permission is checked at runtime.
    private fun schedule() {
        val deadline = items.value.asSequence().filter(QuickTimer::running).minOfOrNull(QuickTimer::deadline)
        val exact = exactAlertsEnabled()
        if (deadline == scheduledDeadline && exact == scheduledExact) return
        scheduledExact = exact
        if (deadline == null) alarm.cancel(pendingAlarm)
        else if (exact) {
            try {
                alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, deadline, pendingAlarm)
            } catch (_: SecurityException) {
                scheduledExact = false
                alarm.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, deadline, pendingAlarm)
            }
        } else alarm.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, deadline, pendingAlarm)
        scheduledDeadline = deadline
    }

    private fun load(): List<QuickTimer> = runCatching {
        val array = JSONArray(prefs.getString("items", "[]") ?: "[]")
        val sameBoot = boot >= 0 && boot == prefs.getInt("boot", -1)
        val now = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        List(array.length()) { index ->
            val value = array.getJSONObject(index)
            val timer = QuickTimer(value.getString("id"), value.getString("name"),
                value.getLong("duration"), value.getLong("remaining"),
                value.getLong("deadline"), value.getLong("wallDeadline"))
            if (timer.running && !sameBoot) {
                timer.copy(deadline = (now + (timer.wallDeadline - wall).coerceAtLeast(0)).coerceAtLeast(1))
            } else timer
        }
    }.getOrDefault(emptyList())

    private fun save(next: List<QuickTimer>) {
        val array = JSONArray()
        next.forEach { timer ->
            array.put(JSONObject().put("id", timer.id).put("name", timer.name)
                .put("duration", timer.duration).put("remaining", timer.remaining)
                .put("deadline", timer.deadline).put("wallDeadline", timer.wallDeadline))
        }
        prefs.edit().putInt("boot", boot).putString("items", array.toString()).apply()
        items.value = next
        schedule()
    }

    fun alertsEnabled(): Boolean {
        val manager = context.getSystemService(NotificationManager::class.java)
        val granted = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return granted && manager.areNotificationsEnabled() &&
            manager.getNotificationChannel(CHANNEL)?.importance != NotificationManager.IMPORTANCE_NONE
    }

    @SuppressLint("MissingPermission") // alertsEnabled checks Android's runtime permission before posting.
    private fun notifyFinished(timer: QuickTimer) {
        if (!alertsEnabled()) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Temporizadores de Aura",
            NotificationManager.IMPORTANCE_HIGH))
        val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        manager.notify(timer.id, NOTIFICATION_ID, NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_quick_timer_notification)
            .setContentTitle("Temporizador terminado").setContentText(timer.name)
            .setContentIntent(open).setAutoCancel(true).setCategory(NotificationCompat.CATEGORY_ALARM)
            .build())
    }

    companion object {
        const val MAX_TIMERS = 12
        private const val CHANNEL = "aura_quick_timers"
        private const val EXPIRE = "dev.senda.launcher.QUICK_TIMER_EXPIRE"
        private const val NOTIFICATION_ID = 1
        private var instance: QuickTimers? = null

        fun get(context: Context): QuickTimers = instance ?: QuickTimers(context.applicationContext)
            .also { instance = it }
    }
}

class QuickTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        QuickTimers.get(context).onAlarmReceived()
    }
}
