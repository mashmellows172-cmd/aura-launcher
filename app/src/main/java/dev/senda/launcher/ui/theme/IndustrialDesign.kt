package dev.senda.launcher.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.senda.launcher.R

/** Shared tokens for the Nothing-inspired finish. The artwork and clock are Aura originals. */
object IndustrialTokens {
    val background = Color.Black
    val surface = Color(0xFF121212)
    val raised = Color(0xFF202020)
    val text = Color(0xFFF5F5F1)
    val muted = Color(0xFFB0B0AE)
    val outline = Color(0xFF808080)
    val signal = Color(0xFFFF5B61)
    val radius = 28.dp
    val iconBackground = Color(0xFFE6E6E2)
    val iconInk = Color(0xFF141414)
}
data class IndustrialStyle(val enabled: Boolean = false, val monochromeIcons: Boolean = false)
val LocalIndustrialStyle = staticCompositionLocalOf { IndustrialStyle() }

@OptIn(ExperimentalTextApi::class)
private fun variableFamily(resource: Int) = FontFamily(
    *listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { weight ->
        Font(resource, weight = weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))
    }.toTypedArray()
)
private val geist = variableFamily(R.font.geist_variable)
private val mono = variableFamily(R.font.geist_mono_variable)
private val base = Typography()
val IndustrialTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = geist), displayMedium = base.displayMedium.copy(fontFamily = geist), displaySmall = base.displaySmall.copy(fontFamily = geist),
    headlineLarge = base.headlineLarge.copy(fontFamily = geist), headlineMedium = base.headlineMedium.copy(fontFamily = geist), headlineSmall = base.headlineSmall.copy(fontFamily = geist),
    titleLarge = base.titleLarge.copy(fontFamily = geist), titleMedium = base.titleMedium.copy(fontFamily = geist), titleSmall = base.titleSmall.copy(fontFamily = geist),
    bodyLarge = base.bodyLarge.copy(fontFamily = geist), bodyMedium = base.bodyMedium.copy(fontFamily = geist), bodySmall = base.bodySmall.copy(fontFamily = geist),
    labelLarge = base.labelLarge.copy(fontFamily = mono), labelMedium = base.labelMedium.copy(fontFamily = mono), labelSmall = base.labelSmall.copy(fontFamily = mono),
)
val IndustrialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(16.dp), medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(IndustrialTokens.radius), extraLarge = RoundedCornerShape(32.dp),
)

private val clockDots = mapOf(
    '0' to listOf("01110","11011","11011","11011","11011","11011","01110"),
    '1' to listOf("00100","01100","00100","00100","00100","00100","01110"),
    '2' to listOf("01110","10001","00001","00010","00100","01000","11111"),
    '3' to listOf("11110","00001","00001","01110","00001","00001","11110"),
    '4' to listOf("00010","00110","01010","10010","11111","00010","00010"),
    '5' to listOf("11111","10000","10000","11110","00001","00001","11110"),
    '6' to listOf("01110","10000","10000","11110","10001","10001","01110"),
    '7' to listOf("11111","00001","00010","00100","01000","01000","01000"),
    '8' to listOf("01110","10001","10001","01110","10001","10001","01110"),
    '9' to listOf("01110","10001","10001","01111","00001","00001","01110"),
    ':' to listOf("000","010","010","000","010","010","000"),
)

/** Static dots: redraws only on minute/theme changes, never runs an idle animation. */
@Composable
fun DotClock(time: String, modifier: Modifier = Modifier, color: Color = IndustrialTokens.text) {
    Canvas(modifier.height(56.dp).semantics { contentDescription = time }) {
        val columns = time.sumOf { if (it == ':') 4 else 6 } - 1
        val pitch = minOf(size.width / columns.coerceAtLeast(1), size.height / 7f)
        val top = (size.height - pitch * 7f) / 2f
        var x = 0f
        time.forEach { digit ->
            clockDots[digit]?.forEachIndexed { row, line -> line.forEachIndexed { col, bit ->
                if (bit == '1') drawCircle(color, pitch * .34f, Offset(x + (col + .5f) * pitch, top + (row + .5f) * pitch))
            } }
            x += (if (digit == ':') 4 else 6) * pitch
        }
    }
}
