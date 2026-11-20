package dev.senda.launcher.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import dev.senda.launcher.data.Ps4Palette

val LocalPs4Palette = staticCompositionLocalOf { Ps4Palette.CLASSIC }
internal data class Ps4PaletteColors(val top: Color, val middle: Color, val bottom: Color, val ink: Color, val muted: Color, val surface: Color, val raised: Color, val accent: Color)
internal fun ps4PaletteColors(palette: Ps4Palette, ambient: Ps4AmbientPeriod? = null): Ps4PaletteColors {
    val base = when (palette) {
    Ps4Palette.CLASSIC -> Ps4PaletteColors(Color(0xFF003087),Color(0xFF001343),Color(0xFF000817),Color.White,Color(0xFFD6E8FF),Color(0xFF103D83),Color(0xFF174E9F),Color(0xFFBDE8FF))
    Ps4Palette.MIDNIGHT -> Ps4PaletteColors(Color(0xFF14151B),Color(0xFF0A0B11),Color(0xFF040509),Color(0xFFF5F5F6),Color(0xFFCDD0D9),Color(0xFF171A23),Color(0xFF242936),Color(0xFFF1D9AE))
    Ps4Palette.WHITE -> Ps4PaletteColors(Color(0xFF00394A),Color(0xFF001C30),Color(0xFF000B18),Color(0xFFF4FDFF),Color(0xFFC4E6EC),Color(0xFF093447),Color(0xFF10465B),Color(0xFF8CEEFF))
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
