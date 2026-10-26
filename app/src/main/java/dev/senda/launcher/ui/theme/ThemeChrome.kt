package dev.senda.launcher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme

/** The rail marker and search share the same material silhouette, without changing their hit areas. */
internal fun HistoricalProfile?.chromeShape(): Shape = when (this?.material) {
    HistoricalMaterial.CHROME -> RoundedCornerShape(6.dp)
    HistoricalMaterial.PAPER -> RoundedCornerShape(3.dp)
    HistoricalMaterial.FLOW, HistoricalMaterial.CONSOLE -> RoundedCornerShape(2.dp)
    HistoricalMaterial.LCD -> CutCornerShape(4.dp)
    HistoricalMaterial.WATER -> RoundedCornerShape(18.dp)
    else -> RoundedCornerShape(22.dp)
}

@Composable
internal fun ThemedSectionHeading(title: String, count: Int, theme: LauncherTheme) {
    val profile = historicalProfile(theme).takeUnless { dev.senda.launcher.data.LocalAppearance.current.highContrast }
    val accent = MaterialTheme.colorScheme.primary
    val frame = MaterialTheme.colorScheme.outline.copy(alpha = .35f)
    Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 6.dp)
        .drawWithCache {
            val bottom = size.height - .5.dp.toPx()
            val dots = List(3) { Offset(it * 4.dp.toPx() + 1.dp.toPx(), bottom) }
            onDrawBehind {
                when (profile?.material) {
                    HistoricalMaterial.DOTS -> dots.forEach { drawCircle(accent, .8.dp.toPx(), it) }
                    HistoricalMaterial.CHROME -> {
                        drawLine(frame, Offset(0f, bottom), Offset(size.width, bottom), 1.dp.toPx())
                        drawLine(Color.White.copy(alpha = .08f), Offset(0f, bottom - 1.dp.toPx()), Offset(size.width, bottom - 1.dp.toPx()), .5.dp.toPx())
                    }
                    HistoricalMaterial.PAPER -> drawLine(accent, Offset(0f, bottom), Offset(size.width * .12f, bottom), 2.dp.toPx())
                    HistoricalMaterial.FLOW -> drawLine(frame, Offset(size.width * .45f, bottom), Offset(size.width, bottom), .5.dp.toPx())
                    HistoricalMaterial.LCD -> drawLine(frame, Offset(0f, bottom), Offset(size.width, bottom), 1.dp.toPx())
                    else -> Unit
                }
            }
        }.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, Modifier.weight(1f).padding(end = 8.dp), style = MaterialTheme.typography.titleMedium,
            fontWeight = if (profile?.material == HistoricalMaterial.FLOW) FontWeight.Normal else FontWeight.SemiBold)
        Text(count.toString(), Modifier.background(MaterialTheme.colorScheme.surfaceVariant, profile.chromeShape())
            .padding(horizontal = 9.dp, vertical = 3.dp), style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** One holder silhouette for both generated artwork and native/custom drawables. */
@Composable
internal fun animatedIconHolderShape(holder: IconHolder, durationMs: Int): Shape {
    val reduced = dev.senda.launcher.ui.LocalMotionSettings.current.reduceMotion || !android.animation.ValueAnimator.areAnimatorsEnabled()
    val target = when (holder) {
        IconHolder.SQUIRCLE -> 15.dp
        IconHolder.ROUNDED -> 12.dp
        IconHolder.BEVELED -> 5.dp
        else -> 21.dp
    }
    val radius by animateDpAsState(target, tween(if (reduced) 0 else durationMs), label = "shared-icon-holder")
    return when (holder) {
        IconHolder.CIRCLE -> androidx.compose.foundation.shape.CircleShape
        IconHolder.PILL -> RoundedCornerShape(50)
        IconHolder.BEVELED -> CutCornerShape(radius)
        else -> RoundedCornerShape(radius)
    }
}
