package dev.senda.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

/** A launchable activity exposed by Android. Display names and icons are read from the OS. */
data class LaunchableApp(
    val packageName: String,
    val label: String,
    val component: ComponentName,
    val revision: Long = 0L,
)

class LauncherAppsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    suspend fun loadApps(): List<LaunchableApp> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                launcherIntent,
                PackageManager.ResolveInfoFlags.of(0L),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(launcherIntent, 0)
        }

        val collator = Collator.getInstance(Locale.getDefault())
        activities.asSequence()
            .filter { it.activityInfo?.packageName != appContext.packageName }
            .mapNotNull(::toLaunchableApp)
            .distinctBy(LaunchableApp::packageName)
            .sortedWith { first, second -> collator.compare(first.label, second.label) }
            .toList()
    }

    private fun toLaunchableApp(resolveInfo: ResolveInfo): LaunchableApp? {
        val activity = resolveInfo.activityInfo ?: return null
        if (!activity.enabled || !activity.applicationInfo.enabled) return null
        val label = runCatching { activity.applicationInfo.loadLabel(packageManager).toString() }
            .getOrNull()
            ?.takeIf(String::isNotBlank)
            ?: activity.packageName
        return LaunchableApp(
            packageName = activity.packageName,
            label = label,
            component = ComponentName(activity.packageName, activity.name),
            revision = runCatching { packageManager.getPackageInfo(activity.packageName, 0).lastUpdateTime }.getOrDefault(0L),
        )
    }

    fun launch(app: LaunchableApp) {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(app.component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }

    fun appIcon(app: LaunchableApp) = runCatching { packageManager.getActivityIcon(app.component) }
        .getOrElse { packageManager.defaultActivityIcon }
}
