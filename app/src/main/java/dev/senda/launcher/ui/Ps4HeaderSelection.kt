package dev.senda.launcher.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LocalIconPack
import dev.senda.launcher.data.IconTreatment
import dev.senda.launcher.data.loadLocalImage
import dev.senda.launcher.ui.theme.LocalIconTreatment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Composable
internal fun rememberPs4HeaderSelection(
    enabled: Boolean,
    reduced: Boolean,
    list: LazyListState,
    focus: State<Any?>,
    apps: List<LaunchableApp>,
    recent: LaunchableApp?,
    customIcons: Map<String, String>,
    onLoadIcon: (LaunchableApp) -> Drawable,
): State<Color> {
    val base = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val original = LocalIconTreatment.current == IconTreatment.ORIGINAL
    val pack = LocalIconPack.current.takeUnless { original }
    val currentLoader by rememberUpdatedState(onLoadIcon)
    var accent by remember(base) { mutableStateOf(base) }
    LaunchedEffect(enabled, reduced, base, list, focus, apps, recent, customIcons, original, pack) {
        if (!enabled || reduced) {
            accent = base
            return@LaunchedEffect
        }
        snapshotFlow { list.isScrollInProgress to focus.value }.collectLatest { (scrolling, focused) ->
            if (scrolling) return@collectLatest
            val key = focused as? String
            val app = when {
                key == "recent-app" -> recent
                key?.startsWith("favorite-") == true -> apps.firstOrNull { it.packageName == key.removePrefix("favorite-") }
                key?.startsWith("all-") == true -> apps.firstOrNull { it.packageName == key.removePrefix("all-") }
                else -> null
            }
            if (app == null) {
                accent = base
                return@collectLatest
            }
            delay(160)
            val customUri = customIcons["PS4_${app.packageName}"].takeUnless { original }
            accent = withContext(Dispatchers.IO) {
                runCatching {
                    val bitmap = customUri?.let { loadLocalImage(context, it.toUri(), 48) }
                    if (bitmap != null) {
                        sampleCoverAccent(bitmap.asAndroidBitmap(), base)
                    } else {
                        val icon = pack?.drawable(app) ?: currentLoader(app)
                        sampleCoverAccent(icon.toBitmap(32, 32, android.graphics.Bitmap.Config.ARGB_8888), base)
                    }
                }.getOrDefault(base)
            }
        }
    }
    return animateColorAsState(if (enabled) accent else base, tween(if (reduced || !enabled) 0 else 260,
        easing = Ps4Motion.easing), label = "ps4-header-selection")
}

internal fun Modifier.ps4SelectionGlow(tint: State<Color>): Modifier = drawWithCache {
    val accent = tint.value
    val wash = Brush.radialGradient(
        listOf(accent.copy(alpha = .24f), accent.copy(alpha = .10f), Color.Transparent),
        center = Offset(size.width * .64f, size.height * .15f),
        radius = size.width.coerceAtLeast(size.height) * .74f,
    )
    val edge = Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(alpha = .54f), Color.Transparent))
    onDrawBehind {
        drawRect(wash)
        val height = 1.dp.toPx()
        drawRect(edge, topLeft = Offset(0f, (size.height - height).coerceAtLeast(0f)),
            size = androidx.compose.ui.geometry.Size(size.width, height))
    }
}
