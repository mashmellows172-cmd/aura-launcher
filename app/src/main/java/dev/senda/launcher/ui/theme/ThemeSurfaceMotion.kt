package dev.senda.launcher.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.Ps4Palette

/** Finite interaction cadence shared by search, the index and transient surfaces. */
internal enum class SurfaceMotionKind { DOTS, HINGE, RIPPLE, LAYERS, GLIDE, SEGMENTS, PS4, PS5, VITA, STANDARD }

internal data class ThemeSurfaceMotion(
    val kind: SurfaceMotionKind,
    val durationMs: Int,
    val easing: Easing,
) {
    fun duration(requestedMs: Int, closing: Boolean = false): Int =
        (requestedMs * durationMs / 280f * if (closing) .7f else 1f).toInt().coerceIn(80, 600)
}

private val directArrival = CubicBezierEasing(.16f, 1f, .3f, 1f)
private val mechanicalArrival = CubicBezierEasing(.24f, .72f, .2f, 1f)
private val flowingArrival = CubicBezierEasing(.2f, .65f, .25f, 1f)

internal fun themeSurfaceMotion(theme: LauncherTheme?, palette: Ps4Palette): ThemeSurfaceMotion {
    if (theme == LauncherTheme.PS4) return when (palette) {
        Ps4Palette.CLASSIC -> ThemeSurfaceMotion(SurfaceMotionKind.PS4, ps4Design(palette).motionMillis, flowingArrival)
        Ps4Palette.MIDNIGHT -> ThemeSurfaceMotion(SurfaceMotionKind.PS5, ps4Design(palette).motionMillis, directArrival)
        Ps4Palette.WHITE -> ThemeSurfaceMotion(SurfaceMotionKind.VITA, ps4Design(palette).motionMillis, flowingArrival)
    }
    val profile = theme?.let(::historicalProfile)
    val kind = when (profile?.motion) {
        HistoricalMotion.DOT_REVEAL -> SurfaceMotionKind.DOTS
        HistoricalMotion.HINGE -> SurfaceMotionKind.HINGE
        HistoricalMotion.RIPPLE -> SurfaceMotionKind.RIPPLE
        HistoricalMotion.LAYERS -> SurfaceMotionKind.LAYERS
        HistoricalMotion.GLIDE -> SurfaceMotionKind.GLIDE
        HistoricalMotion.SEGMENTS -> SurfaceMotionKind.SEGMENTS
        null -> SurfaceMotionKind.STANDARD
    }
    return ThemeSurfaceMotion(kind, profile?.durationMs ?: 280, when (kind) {
        SurfaceMotionKind.HINGE, SurfaceMotionKind.SEGMENTS -> mechanicalArrival
        SurfaceMotionKind.RIPPLE, SurfaceMotionKind.GLIDE -> flowingArrival
        else -> directArrival
    })
}

/** Drawing-layer changes only: layout, hit regions and gesture ownership stay intact. */
internal fun GraphicsLayerScope.applyThemeSurfaceMotion(
    motion: ThemeSurfaceMotion, progress: Float, lateral: Boolean = false, direction: Float = 1f,
) {
    val remaining = 1f - progress.coerceIn(0f, 1f)
    rotationX = 0f
    translationX = 0f
    translationY = 0f
    scaleX = 1f
    scaleY = 1f
    transformOrigin = TransformOrigin.Center
    cameraDistance = maxOf(size.width, size.height, 48.dp.toPx()) * 2f
    when (motion.kind) {
        SurfaceMotionKind.DOTS -> Unit // Segmented reveal is drawn without moving the surface.
        SurfaceMotionKind.HINGE -> {
            transformOrigin = TransformOrigin(.5f, 0f)
            rotationX = -9f * remaining
            scaleY = 1f - .025f * remaining
        }
        SurfaceMotionKind.RIPPLE -> {
            scaleX = 1f - .025f * remaining
            scaleY = scaleX
        }
        SurfaceMotionKind.LAYERS -> {
            translationX = 7.dp.toPx() * remaining * direction
            translationY = 12.dp.toPx() * remaining
        }
        SurfaceMotionKind.GLIDE -> translationX = -20.dp.toPx() * remaining * direction
        SurfaceMotionKind.SEGMENTS -> {
            transformOrigin = TransformOrigin(.5f, .5f)
            rotationX = -6f * remaining
            scaleY = 1f - .035f * remaining
        }
        SurfaceMotionKind.PS5 -> {
            scaleX = 1f - .035f * remaining
            scaleY = scaleX
        }
        SurfaceMotionKind.VITA -> {
            translationY = 16.dp.toPx() * remaining
            scaleX = 1f - .015f * remaining
            scaleY = scaleX
        }
        SurfaceMotionKind.PS4, SurfaceMotionKind.STANDARD -> {
            if (lateral) translationX = -24.dp.toPx() * remaining * direction
            else translationY = 20.dp.toPx() * remaining
        }
    }
}
