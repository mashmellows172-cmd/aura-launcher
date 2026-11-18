package dev.senda.launcher.ui

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance

/** Frost only a bounded decorative image. Text and controls retain their crisp, opaque ink. */
@Composable
internal fun BoxScope.SonyGlassBackdrop(
    artwork: Bitmap?,
    accent: State<Color>,
    reduced: Boolean,
    constrained: State<Boolean> = LocalDecorationConstrained.current,
) {
    val cover = artwork?.takeUnless { it.isRecycled }
    val contrast = LocalAppearance.current.highContrast
    val enabled = !contrast && !reduced && !LocalLightMode.current && !LocalSystemPowerSaving.current &&
        !constrained.value
    if (!enabled) return
    val image = remember(cover) { cover?.asImageBitmap() }
    Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(200.dp).clipToBounds()) {
        if (image != null) Image(image, null,
            Modifier.matchParentSize()
                .then(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Modifier.blur(20.dp) else Modifier),
            contentScale = ContentScale.Crop, alpha = .38f)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(
            listOf(Color(0x7713223B), Color(0xFF0B1730)),
        )).drawWithCache {
            onDrawBehind { drawRect(accent.value, alpha = .06f) }
        })
    }
}

/** Cache geometry; read the finite artwork transition only during drawing. */
internal fun Modifier.sonyGlassRim(accent: State<Color>): Modifier = drawWithCache {
    val radius = CornerRadius(18.dp.toPx())
    val inset = 1.dp.toPx()
    val rim = Stroke(1.dp.toPx())
    onDrawBehind {
        val color = accent.value
        drawRoundRect(color, Offset(inset, inset),
            androidx.compose.ui.geometry.Size((size.width - 2 * inset).coerceAtLeast(0f),
                (size.height - 2 * inset).coerceAtLeast(0f)),
            radius, alpha = .30f, style = rim)
        drawRoundRect(color, cornerRadius = radius, alpha = .045f)
    }
}

