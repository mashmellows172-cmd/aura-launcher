package dev.senda.launcher.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.Ps4Palette

/** Shared geometry and cadence for each console identity. Persisted palette IDs stay unchanged. */
@Immutable
internal data class Ps4Design(
    val panelRadius: Int,
    val tileRadius: Int,
    val holder: IconHolder,
    val motionMillis: Int,
    val elevation: Int,
)

private val classicDesign = Ps4Design(10, 2, IconHolder.BEVELED, 280, 0)
private val nextGenDesign = Ps4Design(24, 14, IconHolder.ROUNDED, 240, 3)
private val portableDesign = Ps4Design(28, 100, IconHolder.CIRCLE, 260, 1)

internal fun ps4Design(palette: Ps4Palette): Ps4Design = when (palette) {
    Ps4Palette.CLASSIC -> classicDesign
    Ps4Palette.MIDNIGHT -> nextGenDesign
    Ps4Palette.WHITE -> portableDesign
}

private fun identityShapes(radiusDp: Int): Shapes {
    val radius = radiusDp.dp
    return Shapes(
        extraSmall = RoundedCornerShape(radius / 3),
        small = RoundedCornerShape(radius / 2),
        medium = RoundedCornerShape(radius),
        large = RoundedCornerShape(radius),
        extraLarge = RoundedCornerShape(radius + 4.dp),
    )
}


private val classicShapes = identityShapes(10)
private val nextGenShapes = identityShapes(24)
private val portableShapes = identityShapes(28)

internal fun ps4Shapes(palette: Ps4Palette): Shapes = when (palette) {
    Ps4Palette.CLASSIC -> classicShapes
    Ps4Palette.MIDNIGHT -> nextGenShapes
    Ps4Palette.WHITE -> portableShapes
}
