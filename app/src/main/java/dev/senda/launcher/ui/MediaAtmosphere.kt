package dev.senda.launcher.ui

import android.animation.ValueAnimator
import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LocalAppearance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Stable
internal class MediaAtmosphere(val color: State<Color>, val presence: State<Float>)

internal val LocalMediaAtmosphere = staticCompositionLocalOf {
    MediaAtmosphere(mutableStateOf(Color(0xFF88CFFF)), mutableFloatStateOf(0f))
}

/** A bounded sample of existing local artwork, never a bitmap copy or an audio poll. */
@Composable
internal fun rememberMediaAtmosphere(info: ActiveAudioInfo, reduced: Boolean, enabled: Boolean): MediaAtmosphere {
    val base = MaterialTheme.colorScheme.primary
    val blocked = !enabled || LocalAppearance.current.highContrast || LocalSystemPowerSaving.current || LocalThemePreview.current
    val art = info.artwork?.takeUnless { it.isRecycled || it.config == Bitmap.Config.HARDWARE }
    val sampled by produceState(base, art, base, blocked) {
        value = if (art == null || blocked) base else withContext(Dispatchers.Default) {
            sampleCoverAccent(art, base)
        }
    }
    val still = reduced || !ValueAnimator.areAnimatorsEnabled()
    val color = animateColorAsState(sampled, tween(if (still || blocked) 0 else 650), label = "cover-atmosphere")
    val presence = animateFloatAsState(if (!blocked && info.isPlaying && !info.generic && art != null) 1f else 0f,
        tween(if (still || blocked) 0 else 240, easing = Ps4Motion.easing), label = "cover-presence")
    return remember(color, presence) { MediaAtmosphere(color, presence) }
}

private fun sampleCoverAccent(bitmap: Bitmap, fallback: Color): Color {
    return try {
        var red = 0f; var green = 0f; var blue = 0f; var weight = 0f
        val width = bitmap.width; val height = bitmap.height
        if (width <= 0 || height <= 0 || bitmap.isRecycled) return fallback
        for (y in 0 until 12) for (x in 0 until 12) {
            val pixel = bitmap.getPixel(((x + .5f) * width / 12).toInt().coerceAtMost(width - 1),
                ((y + .5f) * height / 12).toInt().coerceAtMost(height - 1))
            val r = android.graphics.Color.red(pixel) / 255f
            val g = android.graphics.Color.green(pixel) / 255f
            val b = android.graphics.Color.blue(pixel) / 255f
            val high = maxOf(r, g, b); val low = minOf(r, g, b)
            val w = (high - low) * android.graphics.Color.alpha(pixel) / 255f
            if (high > .15f && low < .9f) { red += r * w; green += g * w; blue += b * w; weight += w }
        }
        if (weight < .01f) fallback else {
            val source = Color(red / weight, green / weight, blue / weight)
            // Decorative tint only: text/control colors keep their established contrast roles.
            lerp(fallback, source, .55f)
        }
    } catch (_: RuntimeException) { fallback } // A session may recycle artwork between metadata callbacks.
}
