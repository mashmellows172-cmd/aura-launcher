package dev.senda.launcher

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Authorizes session observation through an optional Android listener identity.
 * Notification payloads are never read, stored, forwarded or dismissed by Aura.
 */
class MediaAccessService : NotificationListenerService() {
    override fun onListenerConnected() { super.onListenerConnected(); connected.value = true }
    override fun onListenerDisconnected() { connected.value = false; super.onListenerDisconnected() }
    override fun onDestroy() { connected.value = false; super.onDestroy() }

    companion object {
        private val connected = MutableStateFlow(false)
        val connection = connected.asStateFlow()
        fun component(context: Context) = ComponentName(context, MediaAccessService::class.java)
        fun isEnabled(context: Context): Boolean = runCatching {
            context.getSystemService(NotificationManager::class.java)?.isNotificationListenerAccessGranted(component(context)) == true
        }.getOrDefault(false)

        /** Opens Android's consent screen; cannot grant access. */
        fun openSettings(context: Context): Boolean {
            val detail = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, component(context).flattenToString())
            for (intent in listOf(detail, Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))) {
                if (runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true }.getOrDefault(false)) return true
            }
            return false
        }
    }
}
