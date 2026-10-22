package dev.senda.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import androidx.compose.runtime.staticCompositionLocalOf

val LocalIconPack = staticCompositionLocalOf<IconPackMapping?> { null }

data class IconPack(val packageName: String, val label: String, val revision: Long)
data class IconPackMapping(val pack: IconPack, val resources: Resources, val icons: Map<String, String>) {
    fun drawable(app: LaunchableApp): Drawable? = runCatching {
        val name = icons[app.component.flattenToString()] ?: return null
        val id = resources.getIdentifier(name, "drawable", pack.packageName)
        if (id == 0) null else resources.getDrawable(id, null)
    }.getOrNull()
}

/** Reads only standard appfilter mappings and drawable resources; never loads pack code. */
class IconPacks(private val context: Context) {
    suspend fun installed(): List<IconPack> = withContext(Dispatchers.IO) {
        listOf("org.adw.launcher.THEMES", "com.gau.go.launcherex.theme", "com.novalauncher.THEME", "app.lawnchair.icons.THEMED_ICON")
            .flatMap { context.packageManager.queryIntentActivities(Intent(it), 0) }
            .mapNotNull { result -> runCatching {
                val info = result.activityInfo.applicationInfo
                IconPack(info.packageName, info.loadLabel(context.packageManager).toString(), context.packageManager.getPackageInfo(info.packageName, 0).lastUpdateTime)
            }.getOrNull() }.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
    }

    suspend fun read(pack: IconPack): IconPackMapping = withContext(Dispatchers.IO) {
        val resources = context.packageManager.getResourcesForApplication(pack.packageName)
        val xmlId = resources.getIdentifier("appfilter", "xml", pack.packageName)
        val icons = mutableMapOf<String, String>()
        fun parse(parser: XmlPullParser) {
            var events = 0
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                require(++events <= 200_000) { "Paquete de iconos demasiado grande" }
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "item") {
                    val component = parser.getAttributeValue(null, "component")?.removePrefix("ComponentInfo{")?.removeSuffix("}")
                    val drawable = parser.getAttributeValue(null, "drawable")
                    val normalized = component?.let(ComponentName::unflattenFromString)?.flattenToString()
                    if (normalized != null && drawable != null && Regex("[a-zA-Z0-9_]+").matches(drawable)) icons[normalized] = drawable
                }
                parser.next()
            }
        }
        if (xmlId != 0) resources.getXml(xmlId).use(::parse)
        else resources.assets.open("appfilter.xml").use { stream -> parse(Xml.newPullParser().apply { setInput(stream, "UTF-8") }) }
        require(icons.isNotEmpty()) { "Este paquete no contiene un appfilter compatible" }
        IconPackMapping(pack, resources, icons)
    }
}
