package dev.senda.launcher.widgets

import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

data class HomeWidget(val id: Int, val heightDp: Int)
data class WidgetWeather(val temperature: String, val place: String, val icon: androidx.compose.ui.graphics.ImageBitmap?)

/** A fixed host identity and durable IDs keep provider data across process/activity recreation. */
class WidgetController(private val activity: ComponentActivity) {
    val manager: AppWidgetManager = AppWidgetManager.getInstance(activity)
    private val storage = activity.getSharedPreferences("aura_widgets", Context.MODE_PRIVATE)
    var providerRevision by mutableStateOf(0)
        private set
    val host = object : AppWidgetHost(activity, 2048) {
        override fun onProvidersChanged() { providerRevision++ }
        override fun onProviderChanged(appWidgetId: Int, info: AppWidgetProviderInfo) {
            super.onProviderChanged(appWidgetId, info)
            providerRevision++
        }
        override fun onAppWidgetRemoved(appWidgetId: Int) {
            super.onAppWidgetRemoved(appWidgetId)
            providerRevision++
        }
        override fun onCreateView(context: Context, appWidgetId: Int, info: AppWidgetProviderInfo): AppWidgetHostView =
            object : AppWidgetHostView(context) {
                override fun updateAppWidget(remoteViews: RemoteViews?) {
                    try { super.updateAppWidget(remoteViews) }
                    catch (_: RuntimeException) { super.updateAppWidget(null) }
                    if (appWidgetId == weatherWidgetId) post { captureWeather(this) }
                }
                override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                    super.onLayout(changed,left,top,right,bottom)
                    if (appWidgetId == weatherWidgetId) captureWeather(this)
                }
            }
    }
    var weatherWidgetId by mutableStateOf(storage.getInt("weather_widget_id", AppWidgetManager.INVALID_APPWIDGET_ID))
        private set
    var weatherReading by mutableStateOf<WidgetWeather?>(null)
        private set
    var weatherError by mutableStateOf<String?>(null)
        private set
    private var pendingWeather: Boolean
        get() = storage.getBoolean("pending_weather",false)
        set(value) { storage.edit(commit = true) { putBoolean("pending_weather",value) } }
    fun connectWeather(packageName: String) {
        val provider = providers().firstOrNull { it.provider.packageName == packageName }
        if (provider == null) { weatherError = "Esta aplicación no ofrece un widget de clima compatible"; return }
        if (manager.getAppWidgetInfo(weatherWidgetId)?.provider == provider.provider) return
        discardPending(); pendingWeather = true; weatherError = null
        try {
            pendingId = host.allocateAppWidgetId()
            val options = Bundle().apply { putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,120);putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,120);putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,90);putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,90) }
            if (manager.bindAppWidgetIdIfAllowed(pendingId,provider.profile,provider.provider,options)) configureOrFinish()
            else bindRequest.launch(Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,pendingId);putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,provider.provider)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE,provider.profile);putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS,options)
            })
        } catch (_: RuntimeException) { discardPending(); weatherError = "No se pudo conectar el widget del clima" }
    }
    fun disconnectWeather() {
        val id = weatherWidgetId; weatherWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;weatherReading = null
        storage.edit(commit = true) { remove("weather_widget_id") };runCatching { host.deleteAppWidgetId(id) }
    }
    private var weatherIconSignature = 0
    private fun captureWeather(view: android.view.View) {
        val texts = mutableListOf<String>();var signature = 0; var icon: androidx.compose.ui.graphics.ImageBitmap? = null
        fun visit(node: android.view.View) {
            if (node is android.widget.TextView) node.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let(texts::add)
            if (node is android.widget.ImageView && icon == null && node.drawable != null) { signature = node.drawable.constantState?.hashCode() ?: node.drawable.hashCode(); icon = runCatching { node.drawable.toBitmap(96,96).asImageBitmap() }.getOrNull() }
            if (node is android.view.ViewGroup) for (i in 0 until node.childCount) visit(node.getChildAt(i))
        }
        visit(view)
        val temperature = texts.firstOrNull { Regex("[+−-]?\\d{1,3}(?:[.,]\\d{1,2})?\\s*°(?:\\s*[CF])?").matches(it) }
        if (temperature != null) {
            val place = texts.firstOrNull { it != temperature && !it.matches(Regex("[\\d:., ]+")) }.orEmpty()
            val old = weatherReading
            if (old?.temperature != temperature || old.place != place || old.icon == null || signature != weatherIconSignature) { weatherIconSignature = signature; weatherReading = WidgetWeather(temperature,place,icon) }
        } else if (texts.isNotEmpty()) weatherReading = null
    }

    var widgets by mutableStateOf(readWidgets())
        private set
    private var pendingId: Int
        get() = storage.getInt("pending", AppWidgetManager.INVALID_APPWIDGET_ID)
        set(value) { storage.edit(commit = true) { putInt("pending", value) } }

    private val bindRequest = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) configureOrFinish() else discardPending()
    }

    fun start() { providerRevision++; runCatching { host.startListening() }.onFailure { notify("No se pudieron actualizar los widgets") } }
    fun stop() { host.stopListening() }

    fun providers(): List<AppWidgetProviderInfo> = manager.installedProviders
        .sortedBy { it.loadLabel(activity.packageManager).lowercase() }

    fun add(provider: AppWidgetProviderInfo) {
        if (widgets.size >= 4) { notify("Puedes añadir hasta cuatro widgets"); return }
        discardPending()
        try {
            pendingId = host.allocateAppWidgetId()
            if (manager.bindAppWidgetIdIfAllowed(pendingId, provider.profile, provider.provider, Bundle())) configureOrFinish()
            else bindRequest.launch(Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE, provider.profile)
            })
        } catch (_: RuntimeException) { discardPending(); notify("No se pudo añadir este widget") }
    }

    private fun configureOrFinish() {
        val info = manager.getAppWidgetInfo(pendingId)
        if (info == null) { discardPending(); return }
        if (info.configure == null) finishAddition()
        else try {
            host.startAppWidgetConfigureActivityForResult(activity, pendingId, 0, CONFIGURE_REQUEST, null)
        } catch (_: RuntimeException) { discardPending(); notify("El proveedor no pudo configurar el widget") }
    }

    fun handleConfigurationResult(requestCode: Int, resultCode: Int) {
        if (requestCode == CONFIGURE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) finishAddition() else discardPending()
        }
    }

    private fun finishAddition() {
        val id = pendingId
        val info = manager.getAppWidgetInfo(id)
        if (info == null) { discardPending(); return }
        if (pendingWeather) {
            val old = weatherWidgetId; weatherWidgetId = id; weatherReading = null
            storage.edit(commit = true) { putInt("weather_widget_id",id);putBoolean("pending_weather",false) }
            pendingId = AppWidgetManager.INVALID_APPWIDGET_ID
            if (old != id && old != AppWidgetManager.INVALID_APPWIDGET_ID) runCatching { host.deleteAppWidgetId(old) }
            return
        }
        if (widgets.none { it.id == id }) {
            widgets = widgets + HomeWidget(id, (info.minHeight / activity.resources.displayMetrics.density).toInt().coerceIn(100, 360))
            persist()
        }
        pendingId = AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private fun discardPending() {
        val id = pendingId
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID && widgets.none { it.id == id } && id != weatherWidgetId) runCatching { host.deleteAppWidgetId(id) }
        pendingId = AppWidgetManager.INVALID_APPWIDGET_ID; pendingWeather = false
    }

    fun remove(id: Int) {
        widgets = widgets.filterNot { it.id == id }
        persist()
        runCatching { host.deleteAppWidgetId(id) }
    }

    fun resize(id: Int, change: Int) {
        widgets = widgets.map { if (it.id == id) it.copy(heightDp = (it.heightDp + change).coerceIn(100, 420)) else it }
        persist()
    }

    fun move(id: Int, delta: Int) {
        val index = widgets.indexOfFirst { it.id == id }
        val target = index + delta
        if (index < 0 || target !in widgets.indices) return
        widgets = widgets.toMutableList().apply { add(target, removeAt(index)) }
        persist()
    }

    private fun readWidgets(): List<HomeWidget> = storage.getString("widgets", "").orEmpty().split(';').mapNotNull { entry ->
        val parts = entry.split(':')
        val id = parts.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
        val height = parts.getOrNull(1)?.toIntOrNull() ?: 180
        if (id <= 0) null else HomeWidget(id, height.coerceIn(100, 420))
    }.distinctBy { it.id }.take(4)

    private fun persist() { storage.edit(commit = true) { putString("widgets", widgets.joinToString(";") { "${it.id}:${it.heightDp}" }) } }
    private fun notify(message: String) { Toast.makeText(activity, message, Toast.LENGTH_LONG).show() }
    companion object { const val CONFIGURE_REQUEST = 4102 }
}
