package dev.senda.launcher.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import dev.senda.launcher.data.Ps4Palette

val LocalPs4Palette = staticCompositionLocalOf { Ps4Palette.CLASSIC }
internal data class Ps4PaletteColors(
    val top: Color, val middle: Color, val bottom: Color, val ink: Color, val muted: Color,
    val surface: Color, val raised: Color, val accent: Color,
    val focusRim: Color = accent,
    val focusHaloAlpha: Float = .22f,
    val panelAlpha: Float = .84f,
    val glassReflectionAlpha: Float = .12f,
    val hudGlowAlpha: Float = .14f,
    val focusSweepAlpha: Float = .38f,
)
internal fun ps4PaletteColors(palette: Ps4Palette, ambient: Ps4AmbientPeriod? = null): Ps4PaletteColors {
    val base = when (palette) {
    Ps4Palette.CLASSIC -> Ps4PaletteColors(Color(0xFF003087),Color(0xFF001343),Color(0xFF000817),Color.White,Color(0xFFD6E8FF),Color(0xFF103D83),Color(0xFF174E9F),Color(0xFFBDE8FF), Color(0xFF73C8FF), .24f, .80f, .15f, .18f, .46f)
    Ps4Palette.MIDNIGHT -> Ps4PaletteColors(Color(0xFF151C2B),Color(0xFF101520),Color(0xFF080B12),Color(0xFFF5F5F6),Color(0xFFCDD0D9),Color(0xFF171A23),Color(0xFF242936),Color(0xFFF2F4FF), Color(0xFFFFFFFF), .16f, .94f, .07f, .10f, .26f)
    Ps4Palette.WHITE -> Ps4PaletteColors(Color(0xFF0063AE),Color(0xFF003B7D),Color(0xFF001B4B),Color(0xFFF4FDFF),Color(0xFFC4E6EC),Color(0xFF073D78),Color(0xFF114D8A),Color(0xFFCBF6FF), Color(0xFF75F2FF), .30f, .72f, .19f, .22f, .52f)
    }
    if (ambient == null || ambient == Ps4AmbientPeriod.DAY) return base
    val tint = when {
        ambient != Ps4AmbientPeriod.DUSK -> Color(0xFF02091C)
        palette == Ps4Palette.MIDNIGHT -> Color(0xFF473521)
        palette == Ps4Palette.WHITE -> Color(0xFF064F60)
        else -> Color(0xFF58477F)
    }
    return base.copy(
        top = lerp(base.top, tint, .34f),
        middle = lerp(base.middle, tint, .20f),
        bottom = lerp(base.bottom, tint, .10f),
        surface = lerp(base.surface, tint, .20f),
        raised = lerp(base.raised, tint, .24f),
    )
}
