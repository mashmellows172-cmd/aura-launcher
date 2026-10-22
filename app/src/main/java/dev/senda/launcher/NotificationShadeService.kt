package dev.senda.launcher

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import java.lang.ref.WeakReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Optional, user-enabled bridge for explicitly requested public global actions. Does not inspect events or window content. */
class NotificationShadeService : AccessibilityService() {
    override fun onServiceConnected() { active = WeakReference(this); connected.value = true }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit
    override fun onUnbind(intent: Intent?): Boolean { clearConnection(); return super.onUnbind(intent) }
    override fun onDestroy() { clearConnection(); super.onDestroy() }
    private fun clearConnection() { if (active.get() === this) { active.clear(); connected.value = false } }
    companion object {
        private var active = WeakReference<NotificationShadeService>(null)
        private val connected = MutableStateFlow(false)
        val connection = connected.asStateFlow()
        fun isConnected() = active.get() != null
        private var lastLockAt = 0L
        fun lock(): Boolean {
            val service = active.get() ?: return false
            val now = android.os.SystemClock.uptimeMillis()
            if (now - lastLockAt < 700) return true
            val result = service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            if (result) lastLockAt = now
            return result
        }
        fun open(): Boolean = active.get()?.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS) == true
    }
}
