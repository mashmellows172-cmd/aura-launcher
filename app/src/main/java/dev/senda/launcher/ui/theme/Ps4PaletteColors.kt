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
    Ps4Palette.MIDNIGHT -> Ps4PaletteColors(Color(0xFF101C39),Color(0xFF080F23),Color(0xFF030610),Color(0xFFF1F5FF),Color(0xFFC4CFE5),Color(0xFF14213B),Color(0xFF203253),Color(0xFFA9CEFF))
    Ps4Palette.WHITE -> Ps4PaletteColors(Color(0xFFF8FAFF),Color(0xFFE9EFF9),Color(0xFFDCE6F5),Color(0xFF14294A),Color(0xFF354F71),Color(0xFFF5F8FE),Color(0xFFD6E3F5),Color(0xFF004FA8))
    }
    if (ambient == null || ambient == Ps4AmbientPeriod.DAY) return base
    val white = palette == Ps4Palette.WHITE
    val tint = when {
        white && ambient == Ps4AmbientPeriod.DUSK -> Color(0xFFFFE4D2)
        white -> Color(0xFFB7CDEA)
        ambient == Ps4AmbientPeriod.DUSK -> Color(0xFF58477F)
        else -> Color(0xFF02091C)
    }
    return base.copy(
        top = lerp(base.top, tint, .34f),
        middle = lerp(base.middle, tint, .20f),
        bottom = lerp(base.bottom, tint, .10f),
        surface = lerp(base.surface, tint, .20f),
        raised = lerp(base.raised, tint, .24f),
    )
}
