package dev.senda.launcher.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.LruCache
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LocalIconPack
import dev.senda.launcher.data.IconTreatment
import dev.senda.launcher.data.LocalRetroAssets
import dev.senda.launcher.data.loadLocalImage
import dev.senda.launcher.ui.theme.LocalIconTreatment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Stable
internal class Ps4AppAccent(val settled: State<Color>, val animated: State<Color>)

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
): Ps4AppAccent {
    val base = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val treatment = LocalIconTreatment.current
    val original = treatment == IconTreatment.ORIGINAL
    val pack = LocalIconPack.current.takeUnless { original }
    val retro = LocalRetroAssets.current.takeIf { treatment == IconTreatment.AUTO && pack == null }
    val currentLoader by rememberUpdatedState(onLoadIcon)
    val accent = remember(base) { mutableStateOf(base) }
    val cache = remember(base, treatment, pack, retro) { LruCache<Pair<String, String?>, Color>(48) }
    LaunchedEffect(enabled, base, list, focus, apps, recent, customIcons, treatment, pack, retro) {
        if (!enabled) {
            accent.value = base
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
                accent.value = base
                return@collectLatest
            }
            delay(120)
            val customUri = if (original) null else customIcons["PS4_${app.packageName}"]
                ?: retro?.icon(app.packageName, dev.senda.launcher.ui.theme.glyphFor(app.packageName)?.name)
            val cacheKey = "${app.component.flattenToString()}:${app.revision}" to customUri
            accent.value = cache.get(cacheKey) ?: withContext(Dispatchers.IO) {
                runCatching {
                    val personal = customUri?.let { loadLocalImage(context, it.toUri(), 48)?.asAndroidBitmap() }
                    val icon = personal ?: (pack?.drawable(app) ?: currentLoader(app)).toBitmap(32, 32, Bitmap.Config.ARGB_8888)
                    sampleAppAccent(icon, base)
                }.getOrDefault(base)
            }.also { cache.put(cacheKey, it) }
        }
    }
    val animated = animateColorAsState(if (enabled) accent.value else base, tween(if (reduced || !enabled) 0 else 220,
        easing = Ps4Motion.easing), label = "ps4-header-selection")
    return remember(accent, animated) { Ps4AppAccent(accent, animated) }
}

private fun sampleAppAccent(bitmap: Bitmap, fallback: Color): Color {
    if (bitmap.isRecycled || bitmap.config == Bitmap.Config.HARDWARE) return fallback
    val bins = FloatArray(12)
    val hsv = FloatArray(3)
    for (y in 0 until 12) for (x in 0 until 12) {
        val pixel = bitmap.getPixel((x * bitmap.width / 12).coerceIn(0, bitmap.width - 1),
            (y * bitmap.height / 12).coerceIn(0, bitmap.height - 1))
        android.graphics.Color.colorToHSV(pixel, hsv)
        if (hsv[1] < .23f || hsv[2] < .24f) continue
        val normalizedHue = hsv[0] / 30f
        val bin = normalizedHue.toInt() % bins.size
        val weight = (android.graphics.Color.alpha(pixel) / 255f) * hsv[1] * hsv[1] * hsv[2]
        val fraction = normalizedHue - normalizedHue.toInt()
        bins[bin] += weight * (1f - fraction)
        bins[(bin + 1) % bins.size] += weight * fraction
    }
    val strongest = bins.indices.maxBy { bins[it] }
    if (bins[strongest] < .28f) return fallback
    val previous = bins[(strongest + bins.lastIndex) % bins.size]
    val next = bins[(strongest + 1) % bins.size]
    val hue = ((strongest + (next - previous) / (previous + bins[strongest] + next)) * 30f + 360f) % 360f
    val color = Color.hsv(hue, .72f, 1f)
    return lerp(color, Color.White, .25f)
}

internal fun Modifier.ps4SelectionGlow(tint: State<Color>): Modifier = drawWithCache {
    val accent = tint.value
    if (size.width <= 0f || size.height <= 0f) return@drawWithCache onDrawBehind { }
    val center = Offset(size.width * .57f, size.height * .49f)
    val radiusY = size.height * .46f
    val stretch = size.width * .42f / radiusY
    val wash = Brush.radialGradient(
        0f to accent.copy(alpha = .42f),
        .48f to accent.copy(alpha = .22f),
        .78f to accent.copy(alpha = .06f),
        1f to accent.copy(alpha = 0f),
        center = center,
        radius = radiusY,
    )
    val edge = Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(alpha = .85f), Color.Transparent))
    onDrawBehind {
        scale(scaleX = stretch, scaleY = 1f, pivot = center) {
            drawCircle(wash, radius = radiusY, center = center)
        }
        val height = 1.5.dp.toPx()
        drawRect(edge, topLeft = Offset(0f, (size.height - height).coerceAtLeast(0f)),
            size = androidx.compose.ui.geometry.Size(size.width, height))
    }
}
